package com.mapnamind.data.mapper;

import com.mapnamind.common.mapper.BaseMapper;
import com.mapnamind.data.dto.OfflineDataDTO;
import com.mapnamind.data.view.OfflineDataView;
import org.mapstruct.Mapper;

@Mapper
public interface OfflineDataMapper extends BaseMapper<OfflineDataDTO, OfflineDataView> {
}
