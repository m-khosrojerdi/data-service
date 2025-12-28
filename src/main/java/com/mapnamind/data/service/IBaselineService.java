package com.mapnamind.data.service;

import com.mapnamind.data.dto.BaselineDTO;

public interface IBaselineService {

    BaselineDTO get(Long userDeviceId, Long topicTreeId);

    BaselineDTO save(BaselineDTO dto);

    void delete(Long id);

    Double getValue(Long userDeviceId, Long topicTreeId);
}
