package com.mapnamind.data.service.impl;

import com.mapnamind.data.dto.OfflineDataDTO;
import com.mapnamind.data.repository.IOfflineDataRepository;
import com.mapnamind.data.service.IOfflineDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfflineDataService implements IOfflineDataService {

    private final IOfflineDataRepository repository;

    @Override
    public void save(OfflineDataDTO dto) {
        repository.save(dto);
    }

}
