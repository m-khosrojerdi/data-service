package com.mapnadigital.insights.insights.data.service.impl;

import com.google.gson.reflect.TypeToken;
import com.mapnadigital.insights.insights.common.view.device.DeviceView;
import com.mapnadigital.insights.insights.common.execption.ServiceException;
import com.mapnadigital.insights.insights.common.model.entity.Baseline;
import com.mapnadigital.insights.insights.common.mapper.topicTree.TopicTreeMapper;
import com.mapnadigital.insights.insights.common.model.entity.Device;
import com.mapnadigital.insights.insights.data.view.BaselineView;
import com.mapnadigital.insights.insights.data.mapper.BaselineViewMapper;
import com.mapnadigital.insights.insights.data.repository.BaselineRepository;
import com.mapnadigital.insights.insights.data.service.IBaselineService;
import com.mapnadigital.insights.web.api.ApiService;
import com.mapnadigital.insights.web.enumeration.ServiceName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

import static com.mapnadigital.insights.insights.common.util.Constant.SLASH;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaselineService implements IBaselineService {

    private final BaselineRepository repository;
    private final DataService dataService;
    private final ApiService apiService;

    public BaselineView get(String topic, String interval) {
        return Mappers.getMapper(BaselineViewMapper.class)
                .sourceToTarget(getBaseline(topic, interval)
                        .orElseThrow(() -> new ServiceException("service.baseline.notFound.9011")));
    }

    public BaselineView save(BaselineView dto) {
        return Mappers.getMapper(BaselineViewMapper.class).sourceToTarget(repository.save(
                getBaseline(dto.getTopic(), dto.getInterval())
                        .map(baseline -> Mappers.getMapper(BaselineViewMapper.class).updateFromDto(dto, baseline))
                        .orElseGet(() -> {
                            Baseline baseline = Mappers.getMapper(BaselineViewMapper.class).targetToSource(dto);
                            Device device = new Device();
                            device.setId(getDevice(dto.getTopic()).getId());
                            baseline.setDevice(device);
                            baseline.setTopicTree(Mappers.getMapper(TopicTreeMapper.class).targetToSource(dataService.getTopicTree(dto.getTopic())));
                            return baseline;
                        })));
    }

    public void delete(BaselineView dto) {
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

    private DeviceView getDevice(String topic) {
        return apiService.get(ServiceName.DEVICE_FIND_BY_SERIAL,
                Collections.singletonList(topic.split(SLASH)[1]), new TypeToken<DeviceView>() {
                }.getType());
    }
}
