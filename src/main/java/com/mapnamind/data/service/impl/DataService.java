package com.mapnamind.data.service.impl;

import com.google.gson.Gson;
import com.mapnamind.common.execption.ServiceException;
import com.mapnamind.common.file.FileManagerConfig;
import com.mapnamind.common.payload.file.FileManagerResponse;
import com.mapnamind.common.util.DateTimeUtil;
import com.mapnamind.common.util.ExcelUtil;
import com.mapnamind.common.util.StringUtil;
import com.mapnamind.common.util.ZipUtil;
import com.mapnamind.data.dto.*;
import com.mapnamind.data.repository.DataRepository;
import com.mapnamind.data.service.IDataService;
import com.mapnamind.data.service.WebSocketService;
import com.mapnamind.web.file.FileManagerTemplate;
import com.mapnamind.web.util.HttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mapnamind.common.util.Constant.*;
import static com.mapnamind.data.util.Constant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService implements IDataService {

    private final FileManagerConfig fileManagerConfig;
    private final WebSocketService webSocketService;
    private final TopicTreeService topicTreeService;
    private final DataRepository dataRepository;
    private final JmsTemplate jmsTemplate;
    private final Gson gson;

    public List<DataProjectionDTO> aggregate(String startDate, String endDate, String effectiveInterval, SignalDTO signalDTO) {
        try {
            resolveProperty(signalDTO);
            return (dataRepository.aggregate(signalDTO.getTopic(), startDate, endDate, signalDTO.getIsProperty(),
                    effectiveInterval,
                    signalDTO.getAggregation()))
                    .stream().map(dataProjection -> DataProjectionDTO.builder()
                            .dateTime(dataProjection.getDateTime().toString())
                            .value(decorateValue(signalDTO.getTopic(), dataProjection.getValue())).build()).toList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("service.common.databaseError.9003");
        }
    }

    private Double decorateValue(String topic, Double value) {
        return topic.matches(SIGNAL_SELECTOR_KILO_TOPICS_REGEX) ? value / 1_000 : value;
    }

    @Override
    public String export(ExportRequest exportRequest) {

        String uuid = UUID.randomUUID().toString();
        exportRequest.setUuid(uuid);
        exportRequest.setUsername(HttpRequest.getUsername());
        jmsTemplate.convertAndSend(SIGNAL_SELECTOR_EXPORT_CONSUMER_QUEUE_NAME, exportRequest);
        return uuid;
    }

    private void resolveProperty(SignalDTO signalDTO) {
        if (signalDTO.getIsProperty().equals("0")) {
            signalDTO.setIsProperty(null);
        } else if (signalDTO.getIsProperty().equals("1")) {
            Pattern pattern = Pattern.compile("\"\\w+\"");
            Matcher matcher = pattern.matcher(signalDTO.getTopic());
            if (matcher.find()) {
                signalDTO.setIsProperty(matcher.group().replaceAll("\"", ""));
                signalDTO.setTopic(signalDTO.getTopic().replace("/{" + matcher.group() + "}", ""));
            }
        }
    }

    @JmsListener(destination = SIGNAL_SELECTOR_EXPORT_CONSUMER_QUEUE_NAME,
            concurrency = SIGNAL_SELECTOR_EXPORT_CONSUMER_QUEUE_CONCURRENCY)
    public void consumeExportRequest(ExportRequest exportRequest) throws Exception {

        FileManagerResponse file;

        try {
            Workbook workbook = createExcel(exportRequest);

            file = FileManagerTemplate.getInstance(fileManagerConfig).upload(ZipUtil.toZip(ExcelUtil.toBytes(workbook),
                    SIGNAL_SELECTOR_EXCEL_FILE_NAME), SIGNAL_SELECTOR_EXCEL_ZIP_FILE_NAME);
        } catch (Exception e) {
            webSocketService.sendToUser(exportRequest.getUsername(), e.getMessage());
            throw new Exception(e);
        }

        webSocketService.sendToUser(exportRequest.getUsername(), gson.toJson(ExportRequest.builder().uuid(exportRequest.getUuid()).file(file).build()));
    }

    private Workbook createExcel(ExportRequest exportRequest) {
        Workbook workbook = new XSSFWorkbook();
        for (SignalDTO signalDTO : exportRequest.getSignalDTOs()) {
            resolveProperty(signalDTO);
            List<ExportDataProjectionDTO> data = dataRepository.aggregate(
                            signalDTO.getTopic(), exportRequest.getStartDate(), exportRequest.getEndDate(), signalDTO.getIsProperty(), signalDTO.getInterval(), signalDTO.getAggregation())
                    .stream().map(dataProjection -> {
                        String[] dateTime = (exportRequest.getIsPersian() ? DateTimeUtil.toJalaliDateTime(dataProjection.getDateTime(), ZoneId.systemDefault()) : dataProjection.getDateTime().toString()).split(SPACE);
                        return ExportDataProjectionDTO.builder()
                                .date(dateTime[0])
                                .time(dateTime[1])
                                .value(decorateValue(signalDTO.getTopic(), dataProjection.getValue())).build();
                    }).toList();
            String topicParts[] = signalDTO.getTopic().split(SLASH);
            TopicTreeDTO topicTreeDTO = topicTreeService.findByPath(topicParts[0] + SLASH +
                    Arrays.stream(topicParts).skip(2).collect(Collectors.joining(SLASH)));
            String sheetName = StringUtil.get(topicTreeDTO.getDescription()).orElse(topicTreeDTO.getTopic()) + DASH + topicParts[1];
            ExcelUtil.createSheet(workbook, SIGNAL_SELECTOR_EXCEL_COLUMNS, data, sheetName, exportRequest.getIsPersian());
        }
        return workbook;
    }
}
