package com.mapnadigital.insights.insights.data.service.impl;

import com.mapnadigital.insights.insights.data.view.OfflineDataView;
import com.mapnadigital.insights.insights.data.repository.IOfflineDataRepository;
import com.mapnadigital.insights.insights.data.service.IOfflineDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfflineDataService implements IOfflineDataService {

    private final IOfflineDataRepository repository;

    @Override
    public void save(OfflineDataView dto) {
        repository.save(dto);
    }

}
