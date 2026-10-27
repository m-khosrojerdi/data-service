package com.mapnadigital.insights.insights.data.mapper;

import com.mapnadigital.insights.insights.common.mapper.BaseMapper;
import com.mapnadigital.insights.insights.common.model.entity.Baseline;
import com.mapnadigital.insights.insights.data.view.BaselineView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Entity/view conversion for baselines. It replaces the former
 * {@code BaselineDTO} pair of mappers ({@code BaselineDTOMapper} +
 * {@code BaselineMapper}).
 */
@Mapper
public interface BaselineViewMapper extends BaseMapper<Baseline, BaselineView> {

    @Mapping(target = "id", ignore = true)
    Baseline updateFromDto(BaselineView dto, @MappingTarget Baseline baseline);
}
