package com.mapnamind.data.service;

import com.mapnamind.common.model.view.DataProjection;
import com.mapnamind.data.dto.DataProjectionDTO;

import java.util.List;

public interface IDataService {

    List<DataProjectionDTO> findAllByTopicAndLogTime(String topic, String startDate, String endDate, String isProperty, Integer sampleCount);
}
