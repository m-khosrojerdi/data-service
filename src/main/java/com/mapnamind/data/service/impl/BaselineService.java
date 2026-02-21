package com.mapnamind.data.service.impl;

import com.google.gson.reflect.TypeToken;
import com.mapnamind.common.dto.device.DeviceDTO;
import com.mapnamind.common.execption.ServiceException;
import com.mapnamind.common.model.entity.Baseline;
import com.mapnamind.common.model.entity.Device;
import com.mapnamind.data.dto.BaselineDTO;
import com.mapnamind.data.mapper.BaselineDTOMapper;
import com.mapnamind.data.mapper.TopicTreeDTOMapper;
import com.mapnamind.data.repository.BaselineRepository;
import com.mapnamind.data.service.IBaselineService;
import com.mapnamind.web.api.ApiService;
import com.mapnamind.web.enumeration.ServiceName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.mapnamind.common.util.Constant.SLASH;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaselineService implements IBaselineService {

    private final BaselineRepository repository;
    private final DataService dataService;
    private final ApiService apiService;

    public BaselineDTO get(String topic, String interval) {
        return Mappers.getMapper(BaselineDTOMapper.class)
                .sourceToTarget(getBaseline(topic, interval)
                        .orElseThrow(() -> new ServiceException("service.baseline.notFound.9011")));
    }

    public BaselineDTO save(BaselineDTO dto) {
        return Mappers.getMapper(BaselineDTOMapper.class).sourceToTarget(repository.save(
                getBaseline(dto.getTopic(), dto.getInterval())
                        .map(baseline -> Mappers.getMapper(BaselineDTOMapper.class).updateFromDto(dto, baseline))
                        .orElseGet(() -> {
                            Baseline baseline = Mappers.getMapper(BaselineDTOMapper.class).targetToSource(dto);
                            Device device = new Device();
                            device.setId(getDevice(dto.getTopic()).getId());
                            baseline.setDevice(device);
                            baseline.setTopicTree(Mappers.getMapper(TopicTreeDTOMapper.class).targetToSource(dataService.getTopicTree(dto.getTopic())));
                            return baseline;
                        })));
    }

    public void delete(BaselineDTO dto) {
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

    private DeviceDTO getDevice(String topic) {
        return apiService.call(ServiceName.DEVICE_FIND_BY_SERIAL, HttpMethod.GET, null,
                Collections.singletonList(topic.split(SLASH)[1]), null,  new TypeToken<DeviceDTO>() {
                }.getType());
    }
}
