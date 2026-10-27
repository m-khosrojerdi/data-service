package com.mapnadigital.insights.insights.data.service;

import com.mapnadigital.insights.insights.common.view.topicTree.TopicTreeView;
import com.mapnadigital.insights.insights.data.view.DataValue;
import com.mapnadigital.insights.insights.data.view.ExportRequestView;
import com.mapnadigital.insights.insights.data.view.SignalView;

import java.util.List;

public interface IDataService {

    List<DataValue> aggregate(String topic, String startDate, String endDate, String effectiveInterval, SignalView signalDTO);

    String export(ExportRequestView exportRequestDTO);

    TopicTreeView getTopicTree(String topic);

}
