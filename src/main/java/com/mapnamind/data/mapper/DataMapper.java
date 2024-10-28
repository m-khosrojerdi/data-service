package com.mapnamind.data.mapper;

import com.mapnamind.common.mapper.BaseMapper;
import com.mapnamind.data.dto.DataProjectionDTO;
import com.mapnamind.data.view.DataValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DataMapper extends BaseMapper<DataProjectionDTO, DataValue> {

    @Override
    @Mapping(source = "dateTime", target = "x")
    @Mapping(source = "value", target = "y")
    DataValue sourceToTarget(DataProjectionDTO data);

}
