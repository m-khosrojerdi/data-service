package com.mapnamind.data.service;

import com.mapnamind.data.dto.DataProjectionDTO;
import com.mapnamind.data.dto.ExportRequest;
import com.mapnamind.data.dto.SignalDTO;
import com.mapnamind.data.dto.TopicTreeDTO;

import java.util.List;

public interface IDataService {

    List<DataProjectionDTO> aggregate(String startDate, String endDate, String effectiveInterval, SignalDTO signalDTO);
    String export(ExportRequest exportRequestDTO);
    TopicTreeDTO getTopicTree(String topic);
}
