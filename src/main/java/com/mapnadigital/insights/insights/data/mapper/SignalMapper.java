package com.mapnadigital.insights.insights.data.mapper;

import com.mapnadigital.insights.insights.common.mapper.BaseMapper;
import com.mapnadigital.insights.insights.data.dto.SignalDTO;
import com.mapnadigital.insights.insights.data.view.SignalView;
import org.mapstruct.Mapper;

@Mapper
public interface SignalMapper extends BaseMapper<SignalDTO, SignalView> {
}
