package com.mapnadigital.insights.insights.data.service;

import com.mapnadigital.insights.insights.common.dto.topicTree.TopicTreeDto;
import com.mapnadigital.insights.insights.data.dto.DataValue;
import com.mapnadigital.insights.insights.data.dto.ExportRequestDto;
import com.mapnadigital.insights.insights.data.dto.SignalDto;

import java.util.List;

public interface IDataService {

    List<DataValue> aggregate(String topic, String startDate, String endDate, String effectiveInterval, SignalDto signalDTO);

    String export(ExportRequestDto exportRequestDTO);

    TopicTreeDto getTopicTree(String topic);

}
