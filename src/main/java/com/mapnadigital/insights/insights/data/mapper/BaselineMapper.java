package com.mapnadigital.insights.insights.data.mapper;

import com.mapnadigital.insights.insights.common.mapper.BaseMapper;
import com.mapnadigital.insights.insights.data.dto.BaselineDTO;
import com.mapnadigital.insights.insights.data.view.BaselineView;
import org.mapstruct.Mapper;

@Mapper
public interface BaselineMapper extends BaseMapper<BaselineDTO, BaselineView> {
}
