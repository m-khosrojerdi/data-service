package com.mapnamind.data.service;

import com.mapnamind.common.model.entity.TopicTree;
import com.mapnamind.data.dto.DataProjectionDTO;
import com.mapnamind.data.dto.ExportRequest;
import com.mapnamind.data.dto.SignalDTO;

import java.util.List;

public interface IDataService {

    List<DataProjectionDTO> aggregate(String topic, String startDate, String endDate, String effectiveInterval, SignalDTO signalDTO);

    String export(ExportRequest exportRequestDTO);

    TopicTree getTopicTree(String topic);

}
