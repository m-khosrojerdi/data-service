package com.mapnamind.data.service;

import com.mapnamind.data.dto.BaselineDTO;

public interface IBaselineService {

    BaselineDTO get(String topic, String interval);

    BaselineDTO save(BaselineDTO dto);

    void delete(BaselineDTO dto);

    Double getValue(String topic, String interval);
}
