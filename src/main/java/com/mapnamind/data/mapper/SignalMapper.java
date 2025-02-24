package com.mapnamind.data.mapper;

import com.mapnamind.common.mapper.BaseMapper;
import com.mapnamind.data.dto.SignalDTO;
import com.mapnamind.data.view.SignalView;
import org.mapstruct.Mapper;

@Mapper
public interface SignalMapper extends BaseMapper<SignalDTO, SignalView> {
}
