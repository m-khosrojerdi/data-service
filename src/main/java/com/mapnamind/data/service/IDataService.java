package com.mapnamind.data.service;

import com.mapnamind.common.model.entity.Data;

import java.util.List;

public interface IDataService {

    List<Data> findAllByTopicAndLogTime(String topic, String startDate, String endDate, String isProperty);
}
