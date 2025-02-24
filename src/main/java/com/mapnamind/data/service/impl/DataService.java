package com.mapnamind.data.service.impl;

import com.mapnamind.common.execption.ServiceException;
import com.mapnamind.common.model.view.DataProjection;
import com.mapnamind.data.dto.DataProjectionDTO;
import com.mapnamind.data.dto.SignalDTO;
import com.mapnamind.data.repository.DataRepository;
import com.mapnamind.data.service.IDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService implements IDataService {

    private final DataRepository dataRepository;

    public List<DataProjectionDTO> findAllByTopicAndLogTime(String startDate, String endDate, SignalDTO signalDTO) {
        try {
            resolveProperty(signalDTO);
            return (Objects.isNull(signalDTO.getInterval()) ?
                    dataRepository.findAllByTopicAndLogTime(signalDTO.getTopic(), startDate, endDate, signalDTO.getIsProperty(), signalDTO.getSampleCount()) :
                    dataRepository.delta(signalDTO.getTopic(), startDate, endDate, signalDTO.getIsProperty(), signalDTO.getInterval()))
                    .stream().map(dataProjection -> DataProjectionDTO.builder()
                            .dateTime(dataProjection.getDateTime())
                            .value(dataProjection.getValue())
                            .valueChanges(dataProjection.getValueChanges()).build()).toList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("service.common.databaseError.9003");
        }
    }

    @Override
    public void export(String startDate, String endDate, SignalDTO signalDTO) {
        /*resolveProperty(signalDTO);
        List<DataProjection> data = dataRepository.findAllByTopicAndLogTime(
                signalDTO.getTopic(), startDate, endDate, signalDTO.getIsProperty(), null);
        ExcelUtil.toBytes(ExcelUtil.createWorkbook(data, ""));*/
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
