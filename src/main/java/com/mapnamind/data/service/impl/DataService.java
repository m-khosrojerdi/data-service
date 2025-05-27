package com.mapnamind.data.service.impl;

import com.mapnamind.common.execption.ServiceException;
import com.mapnamind.common.util.DateTimeUtil;
import com.mapnamind.common.util.ExcelUtil;
import com.mapnamind.common.util.ZipUtil;
import com.mapnamind.data.dto.DataProjectionDTO;
import com.mapnamind.data.dto.SignalDTO;
import com.mapnamind.data.repository.DataRepository;
import com.mapnamind.data.service.IDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mapnamind.common.util.Constant.DASH;
import static com.mapnamind.common.util.Constant.SLASH;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService implements IDataService {

    private final DataRepository dataRepository;
    private final TopicTreeService topicTreeService;

    public List<DataProjectionDTO> findAllByTopicAndLogTime(String startDate, String endDate, SignalDTO signalDTO) {
        try {
            resolveProperty(signalDTO);
            return (Objects.isNull(signalDTO.getInterval()) ?
                    dataRepository.findAllByTopicAndLogTime(signalDTO.getTopic(), startDate, endDate, signalDTO.getIsProperty(), signalDTO.getSampleCount()) :
                    dataRepository.delta(signalDTO.getTopic(), startDate, endDate, signalDTO.getIsProperty(), signalDTO.getInterval(), signalDTO.getAggregation()))
                    .stream().map(dataProjection -> DataProjectionDTO.builder()
                            .dateTime(dataProjection.getDateTime().toString())
                            .value(dataProjection.getValue())
                            .valueChanges(dataProjection.getValueChanges()).build()).toList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("service.common.databaseError.9003");
        }
    }

    @Override
    public byte[] export(String startDate, String endDate, List<SignalDTO> signalDTOs, Boolean isPersian) {
        Workbook workbook = new XSSFWorkbook();
        for (SignalDTO signalDTO : signalDTOs) {
            resolveProperty(signalDTO);
            List<DataProjectionDTO> data = dataRepository.findAllByTopicAndLogTime(
                            signalDTO.getTopic(), startDate, endDate, signalDTO.getIsProperty(), null)
                    .stream().map(dataProjection -> DataProjectionDTO.builder()
                            .dateTime(isPersian ? DateTimeUtil.toJalaliDateTime(dataProjection.getDateTime(), ZoneId.systemDefault()) : dataProjection.getDateTime().toString())
                            .value(dataProjection.getValue())
                            .valueChanges(dataProjection.getValueChanges()).build()).toList();
            String topicParts[] = signalDTO.getTopic().split(SLASH);
            String sheetName = topicParts[1] + DASH + topicTreeService.findByPath(topicParts[0] + SLASH +
                    Arrays.stream(topicParts).skip(2).collect(Collectors.joining(SLASH))).getTopic();
            ExcelUtil.createSheet(workbook, List.of("dateTime:تاریخ", "value:مقدار"), data, sheetName, isPersian);
        }
        return ZipUtil.toZip(ExcelUtil.toBytes(workbook), "data.xlsx");
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
}
