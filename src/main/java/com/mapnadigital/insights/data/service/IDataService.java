package com.mapnadigital.insights.data.service;

import com.mapnadigital.insights.data.dto.topicTree.TopicTreeDto;
import com.mapnadigital.insights.data.dto.data.DataValueDto;
import com.mapnadigital.insights.data.dto.data.ExportRequestDto;
import com.mapnadigital.insights.data.dto.data.SignalDto;

import java.util.List;

public interface IDataService {

    List<DataValueDto> aggregate(String topic, String startDate, String endDate, String effectiveInterval, SignalDto signalDTO);

    String export(ExportRequestDto exportRequestDTO);

    TopicTreeDto getTopicTree(String topic);

}
