package com.mapnamind.data.service.impl;

import com.mapnamind.common.execption.ServiceException;
import com.mapnamind.common.model.entity.Data;
import com.mapnamind.data.repository.DataRepository;
import com.mapnamind.data.service.IDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService implements IDataService {

    private final DataRepository dataRepository;

    public List<Data> findAllByTopicAndLogTime(String topic, String startDate, String endDate, String isProperty) {
        try {
            return dataRepository.findAllByTopicAndLogTime(topic, startDate, endDate, isProperty);

        } catch (Exception e) {
            throw new ServiceException("service.common.databaseError.5003");
        }

    }
}
