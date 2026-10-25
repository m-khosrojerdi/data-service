package com.mapnadigital.insights.insights.data.service;

import com.mapnadigital.insights.insights.common.model.entity.TopicTree;
import com.mapnadigital.insights.insights.data.dto.DataProjectionDTO;
import com.mapnadigital.insights.insights.data.dto.ExportRequest;
import com.mapnadigital.insights.insights.data.dto.SignalDTO;

import java.util.List;

public interface IDataService {

    List<DataProjectionDTO> aggregate(String topic, String startDate, String endDate, String effectiveInterval, SignalDTO signalDTO);

    String export(ExportRequest exportRequestDTO);

    TopicTree getTopicTree(String topic);

}
