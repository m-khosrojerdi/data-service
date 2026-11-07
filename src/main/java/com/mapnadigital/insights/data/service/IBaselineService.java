package com.mapnadigital.insights.data.service;

import com.mapnadigital.insights.data.dto.baseline.BaselineDto;

public interface IBaselineService {

    BaselineDto get(String topic, String interval);

    BaselineDto save(BaselineDto dto);

    void delete(BaselineDto dto);

    Double getValue(String topic, String interval);
}
