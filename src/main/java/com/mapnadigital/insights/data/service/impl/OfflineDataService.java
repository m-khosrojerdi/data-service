package com.mapnadigital.insights.data.service.impl;

import com.mapnadigital.insights.data.dto.offlineData.OfflineDataDto;
import com.mapnadigital.insights.data.repository.IOfflineDataRepository;
import com.mapnadigital.insights.data.service.IOfflineDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfflineDataService implements IOfflineDataService {

    private final IOfflineDataRepository repository;

    @Override
    public void save(OfflineDataDto dto) {
        repository.save(dto);
    }

}
