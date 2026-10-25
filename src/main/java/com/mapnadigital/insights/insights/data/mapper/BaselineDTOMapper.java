package com.mapnadigital.insights.insights.data.mapper;

import com.mapnadigital.insights.insights.common.mapper.BaseMapper;
import com.mapnadigital.insights.insights.common.model.entity.Baseline;
import com.mapnadigital.insights.insights.data.dto.BaselineDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface BaselineDTOMapper extends BaseMapper<Baseline, BaselineDTO> {

    @Mapping(target = "id", ignore = true)
    Baseline updateFromDto(BaselineDTO dto, @MappingTarget Baseline baseline);
}
