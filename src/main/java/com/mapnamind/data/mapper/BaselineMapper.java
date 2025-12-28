package com.mapnamind.data.mapper;

import com.mapnamind.common.mapper.BaseMapper;
import com.mapnamind.data.dto.BaselineDTO;
import com.mapnamind.data.view.BaselineView;
import org.mapstruct.Mapper;

@Mapper
public interface BaselineMapper extends BaseMapper<BaselineDTO, BaselineView> {
}
