package com.mapnadigital.insights.insights.data.mapper;

import com.mapnadigital.insights.insights.common.mapper.BaseMapper;
import com.mapnadigital.insights.insights.data.dto.OfflineDataDTO;
import com.mapnadigital.insights.insights.data.view.OfflineDataView;
import org.mapstruct.Mapper;

@Mapper
public interface OfflineDataMapper extends BaseMapper<OfflineDataDTO, OfflineDataView> {
}
