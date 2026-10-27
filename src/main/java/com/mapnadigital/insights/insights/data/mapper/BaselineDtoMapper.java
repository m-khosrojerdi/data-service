package com.mapnadigital.insights.insights.data.mapper;

import com.mapnadigital.insights.insights.common.mapper.BaseMapper;
import com.mapnadigital.insights.insights.common.model.entity.Baseline;
import com.mapnadigital.insights.insights.data.dto.BaselineDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Entity/view conversion for baselines. It replaces the former
 * {@code BaselineDTO} pair of mappers ({@code BaselineDTOMapper} +
 * {@code BaselineMapper}).
 */
@Mapper
public interface BaselineDtoMapper extends BaseMapper<Baseline, BaselineDto> {

    @Mapping(target = "id", ignore = true)
    Baseline updateFromDto(BaselineDto dto, @MappingTarget Baseline baseline);
}
