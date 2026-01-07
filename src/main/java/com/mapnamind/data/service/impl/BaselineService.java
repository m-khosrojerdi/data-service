package com.mapnamind.data.service.impl;

import com.mapnamind.common.execption.ServiceException;
import com.mapnamind.common.model.entity.Baseline;
import com.mapnamind.data.dto.BaselineDTO;
import com.mapnamind.data.mapper.BaselineDTOMapper;
import com.mapnamind.data.repository.BaselineRepository;
import com.mapnamind.data.service.IBaselineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaselineService implements IBaselineService {

    private final BaselineRepository repository;

    public BaselineDTO get(Long userDeviceId, Long topicTreeId, String interval) {
        return Mappers.getMapper(BaselineDTOMapper.class)
                .sourceToTarget(repository.findByUserDeviceIdAndTopicTreeIdAndInterval(userDeviceId, topicTreeId, interval)
                        .orElseThrow(() -> new ServiceException("service.baseline.notFound.9011")));
    }

    public BaselineDTO save(BaselineDTO dto) {
        return Mappers.getMapper(BaselineDTOMapper.class).sourceToTarget(repository.save(
                repository.findByUserDeviceIdAndTopicTreeIdAndInterval(dto.getUserDeviceId(), dto.getTopicTreeId(), dto.getInterval())
                        .map(baseline -> Mappers.getMapper(BaselineDTOMapper.class).updateFromDto(dto, baseline))
                        .orElseGet(() -> Mappers.getMapper(BaselineDTOMapper.class).targetToSource(dto))));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Double getValue(Long userDeviceId, Long topicTreeId, String interval) {
        return repository.findByUserDeviceIdAndTopicTreeIdAndInterval(userDeviceId, topicTreeId, interval)
                .map(Baseline::getValue)
                .orElse(null);
    }
}
