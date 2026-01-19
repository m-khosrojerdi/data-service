package com.mapnamind.data.mapper;

import com.mapnamind.common.mapper.BaseMapper;
import com.mapnamind.common.model.entity.Baseline;
import com.mapnamind.data.dto.BaselineDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface BaselineDTOMapper extends BaseMapper<Baseline, BaselineDTO> {

    @Mapping(target = "id", ignore = true)
    Baseline updateFromDto(BaselineDTO dto, @MappingTarget Baseline baseline);
}
