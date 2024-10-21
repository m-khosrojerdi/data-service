package com.mapnamind.data.mapper;

import com.mapnamind.common.mapper.BaseMapper;
import com.mapnamind.common.model.entity.Data;
import com.mapnamind.common.model.entity.Wallet;
import com.mapnamind.data.view.DataValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DataMapper extends BaseMapper<Data, DataValue> {

    @Override
    @Mapping(source = "dateTime", target = "x")
    @Mapping(source = "value", target = "y")
    DataValue sourceToTarget(Data data);

}
