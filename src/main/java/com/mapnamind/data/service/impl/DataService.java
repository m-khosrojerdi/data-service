package com.mapnamind.data.service.impl;

import com.mapnamind.common.execption.ServiceException;
import com.mapnamind.common.model.view.DataProjection;
import com.mapnamind.data.dto.DataProjectionDTO;
import com.mapnamind.data.repository.DataRepository;
import com.mapnamind.data.service.IDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DataService implements IDataService {

    private final DataRepository dataRepository;

    public List<DataProjectionDTO> findAllByTopicAndLogTime(String topic, String startDate, String endDate, String isProperty) {
        try {
            List<DataProjection> data = dataRepository.findAllByTopicAndLogTime(topic, startDate, endDate, isProperty);
            return data.stream().map(item -> DataProjectionDTO.builder()
                    .value(item.getValue()).dateTime(item.getDateTime()).build()).toList();
        } catch (Exception e) {
            throw new ServiceException("service.common.databaseError.9003");
        }
    }

}
