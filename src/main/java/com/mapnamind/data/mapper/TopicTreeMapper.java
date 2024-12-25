package com.mapnamind.data.mapper;

import com.mapnamind.common.mapper.BaseMapper;
import com.mapnamind.data.dto.TopicTreeDTO;
import com.mapnamind.data.view.TopicTreeView;
import org.mapstruct.Mapper;

@Mapper
public interface TopicTreeMapper extends BaseMapper<TopicTreeDTO, TopicTreeView> {
}
