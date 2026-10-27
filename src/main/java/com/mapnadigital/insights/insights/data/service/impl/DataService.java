package com.mapnadigital.insights.insights.data.service.impl;

import com.google.gson.Gson;
import com.mapnadigital.insights.insights.common.execption.ServiceException;
import com.mapnadigital.insights.insights.common.payload.file.FileManagerResponse;
import com.mapnadigital.insights.insights.common.util.DateTimeUtil;
import com.mapnadigital.insights.insights.common.util.ExcelUtil;
import com.mapnadigital.insights.insights.common.util.StringUtil;
import com.mapnadigital.insights.insights.common.util.ZipUtil;
import com.mapnadigital.insights.insights.common.view.topicTree.TopicTreeView;
import com.mapnadigital.insights.insights.data.view.DataValue;
import com.mapnadigital.insights.insights.data.view.ExportDataProjectionView;
import com.mapnadigital.insights.insights.data.view.ExportRequestView;
import com.mapnadigital.insights.insights.data.view.SignalView;
import com.mapnadigital.insights.insights.data.repository.DataRepository;
import com.mapnadigital.insights.insights.data.service.IDataService;
import com.mapnadigital.insights.insights.data.service.WebSocketService;
import com.mapnadigital.insights.web.file.FileManagerTemplate;
import com.mapnadigital.insights.web.util.HttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mapnadigital.insights.insights.common.util.Constant.*;
import static com.mapnadigital.insights.insights.common.util.StringUtil.isNullOrBlank;
import static com.mapnadigital.insights.insights.data.util.Constant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService implements IDataService {

    private final FileManagerTemplate fileManagerTemplate;
    private final WebSocketService webSocketService;
    private final TopicTreeService topicTreeService;
    private final DataRepository dataRepository;
    private final JmsTemplate jmsTemplate;
    private final Gson gson;

    public List<DataValue> aggregate(String topic, String startDate, String endDate, String effectiveInterval, SignalView signalDTO) {
        try {
            signalDTO.setTopic(topic);
            resolveProperty(signalDTO);
            return (dataRepository.aggregate(signalDTO.getTopic(), startDate, endDate, signalDTO.getIsProperty(),
                    effectiveInterval,
                    signalDTO.getAggregation()))
                    .stream().map(dataProjection -> new DataValue(dataProjection.getDateTime().toString(),
                            decorateValue(signalDTO.getTopic(), dataProjection.getValue()))).toList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("service.common.databaseError.9003");
        }
    }

    private Double decorateValue(String topic, Double value) {
        return topic.matches(SIGNAL_SELECTOR_KILO_TOPICS_REGEX) ? value / 1_000 : value;
    }

    @Override
    public String export(ExportRequestView exportRequest) {

        String uuid = UUID.randomUUID().toString();
        exportRequest.setUuid(uuid);
        exportRequest.setUsername(HttpRequest.getUsername());
        jmsTemplate.convertAndSend(SIGNAL_SELECTOR_EXPORT_CONSUMER_QUEUE_NAME, exportRequest);
        return uuid;
    }

    private void resolveProperty(SignalView signalDTO) {

        if (signalDTO.getIsProperty() == null) {
            return;
        }

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
    public void consumeExportRequestView(ExportRequestView exportRequest) throws Exception {

        FileManagerResponse file;

        try {
            Workbook workbook = createExcel(exportRequest);

            file = fileManagerTemplate.upload(ZipUtil.toZip(ExcelUtil.toBytes(workbook),
                    SIGNAL_SELECTOR_EXCEL_FILE_NAME), SIGNAL_SELECTOR_EXCEL_ZIP_FILE_NAME);
        } catch (Exception e) {
            webSocketService.sendToUser(exportRequest.getUsername(), e.getMessage());
            throw new Exception(e);
        }

        webSocketService.sendToUser(exportRequest.getUsername(), gson.toJson(ExportRequestView.builder().uuid(exportRequest.getUuid()).file(file).build()));
    }

    private Workbook createExcel(ExportRequestView exportRequest) {
        Workbook workbook = new XSSFWorkbook();
        for (SignalView signalDTO : exportRequest.getSignals()) {
            resolveProperty(signalDTO);
            List<ExportDataProjectionView> data = dataRepository.aggregate(
                            signalDTO.getTopic(), exportRequest.getStartDate(), exportRequest.getEndDate(), signalDTO.getIsProperty(), signalDTO.getInterval(), signalDTO.getAggregation())
                    .stream().map(dataProjection -> {
                        String[] dateTime = (exportRequest.getIsPersian() ? DateTimeUtil.toJalaliDateTime(dataProjection.getDateTime(), ZoneId.systemDefault()) : dataProjection.getDateTime().toString()).split(SPACE);
                        return ExportDataProjectionView.builder()
                                .date(dateTime[0])
                                .time(dateTime[1])
                                .value(decorateValue(signalDTO.getTopic(), dataProjection.getValue())).build();
                    }).toList();
            TopicTreeView topicTree = getTopicTree(signalDTO.getTopic());
            String sheetName = StringUtil.keepLeading(StringUtil.get(topicTree.getDescription()).orElse(topicTree.getTopic()), 24) +
                    DASH +
                    StringUtil.keepTrailing(signalDTO.getTopic().replaceFirst("[^/]+/([^/]+)(?:/[^/]+){2}", "$1"), 6);
            ExcelUtil.createSheet(workbook, SIGNAL_SELECTOR_EXCEL_COLUMNS.stream()
                            .map(s -> new ExcelUtil.FieldSet(s.split(COLON)[0], s.split(COLON)[1], s.contains("value") && !isNullOrBlank(topicTree.getUnit()) ? topicTree.getUnit() : null)).toList(),
                    data, sheetName, exportRequest.getIsPersian());
        }
        return workbook;
    }

    public TopicTreeView getTopicTree(String topic) {
        return topicTreeService.findByPath(topic.replaceFirst("(/[^/]+){2}", "$1"));
    }
}
