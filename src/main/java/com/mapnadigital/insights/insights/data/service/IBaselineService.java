package com.mapnadigital.insights.insights.data.service;

import com.mapnadigital.insights.insights.data.dto.BaselineDto;

public interface IBaselineService {

    BaselineDto get(String topic, String interval);

    BaselineDto save(BaselineDto dto);

    void delete(BaselineDto dto);

    Double getValue(String topic, String interval);
}
