package com.mapnadigital.insights.data.service.impl;

import com.google.gson.reflect.TypeToken;
import com.mapnadigital.insights.common.execption.ServiceException;
import com.mapnadigital.insights.data.api.ApiClient;
import com.mapnadigital.insights.data.dto.baseline.BaselineDto;
import com.mapnadigital.insights.data.dto.device.DeviceDto;
import com.mapnadigital.insights.data.mapper.BaselineDtoMapper;
import com.mapnadigital.insights.data.mapper.TopicTreeMapper;
import com.mapnadigital.insights.data.model.entity.Baseline;
import com.mapnadigital.insights.data.repository.BaselineRepository;
import com.mapnadigital.insights.data.service.IBaselineService;
import com.mapnadigital.insights.web.api.ApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

import static com.mapnadigital.insights.common.util.Constant.SLASH;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaselineService implements IBaselineService {

    private final BaselineRepository repository;
    private final DataService dataService;
    private final ApiService apiService;

    public BaselineDto get(String topic, String interval) {
        return Mappers.getMapper(BaselineDtoMapper.class)
                .sourceToTarget(getBaseline(topic, interval)
                        .orElseThrow(() -> new ServiceException("service.baseline.notFound.9011")));
    }

    public BaselineDto save(BaselineDto dto) {
        return Mappers.getMapper(BaselineDtoMapper.class).sourceToTarget(repository.save(
                getBaseline(dto.getTopic(), dto.getInterval())
                        .map(baseline -> Mappers.getMapper(BaselineDtoMapper.class).updateFromDto(dto, baseline))
                        .orElseGet(() -> {
                            Baseline baseline = Mappers.getMapper(BaselineDtoMapper.class).targetToSource(dto);
                            baseline.setDeviceId(getDevice(dto.getTopic()).getId());
                            baseline.setTopicTree(Mappers.getMapper(TopicTreeMapper.class).targetToSource(dataService.getTopicTree(dto.getTopic())));
                            return baseline;
                        })));
    }

    public void delete(BaselineDto dto) {
        repository.deleteById(get(dto.getTopic(), dto.getInterval()).getId());
    }

    public Double getValue(String topic, String interval) {
        return getBaseline(topic, interval)
                .map(Baseline::getValue)
                .orElse(null);
    }

    private Optional<Baseline> getBaseline(String topic, String interval) {
        return repository.findByDeviceIdAndTopicTreeIdAndInterval(
                getDevice(topic).getId(), dataService.getTopicTree(topic).getId(), interval);
    }

    private DeviceDto getDevice(String topic) {
        return apiService.get(ApiClient.DEVICE_FIND_BY_SERIAL,
                Collections.singletonList(topic.split(SLASH)[1]), new TypeToken<DeviceDto>() {
                }.getType());
    }
}
