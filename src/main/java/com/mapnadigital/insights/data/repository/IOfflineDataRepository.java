package com.mapnadigital.insights.data.repository;

import com.mapnadigital.insights.data.dto.offlineData.OfflineDataDto;

public interface IOfflineDataRepository {

    void save(OfflineDataDto dto);

}
