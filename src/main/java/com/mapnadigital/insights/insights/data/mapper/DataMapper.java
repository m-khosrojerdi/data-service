package com.mapnadigital.insights.insights.data.mapper;

import com.mapnadigital.insights.insights.common.mapper.BaseMapper;
import com.mapnadigital.insights.insights.data.dto.DataProjectionDTO;
import com.mapnadigital.insights.insights.data.view.DataValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DataMapper extends BaseMapper<DataProjectionDTO, DataValue> {

    @Override
    @Mapping(source = "dateTime", target = "x")
    @Mapping(source = "value", target = "y")
    DataValue sourceToTarget(DataProjectionDTO data);

}
