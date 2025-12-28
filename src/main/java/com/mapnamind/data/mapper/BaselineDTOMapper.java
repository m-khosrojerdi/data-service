package com.mapnamind.data.mapper;

import com.mapnamind.common.mapper.BaseMapper;
import com.mapnamind.common.model.entity.Baseline;
import com.mapnamind.data.dto.BaselineDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface BaselineDTOMapper extends BaseMapper<Baseline, BaselineDTO> {

    @Override
    @Mapping(source = "userDevice.id", target = "userDeviceId")
    @Mapping(source = "topicTree.id", target = "topicTreeId")
    BaselineDTO sourceToTarget(Baseline source);

    @Override
    @Mapping(source = "userDeviceId", target = "userDevice.id")
    @Mapping(source = "topicTreeId", target = "topicTree.id")
    Baseline targetToSource(BaselineDTO target);

    @Mapping(target = "id", ignore = true)
    Baseline updateFromDto(BaselineDTO dto, @MappingTarget Baseline baseline);
}
