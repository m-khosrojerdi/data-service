package com.mapnadigital.insights.insights.data.service;

import com.mapnadigital.insights.insights.data.dto.BaselineDTO;

public interface IBaselineService {

    BaselineDTO get(String topic, String interval);

    BaselineDTO save(BaselineDTO dto);

    void delete(BaselineDTO dto);

    Double getValue(String topic, String interval);
}
