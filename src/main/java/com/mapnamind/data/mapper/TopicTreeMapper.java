package com.mapnamind.data.mapper;

import com.mapnamind.common.mapper.BaseMapper;
import com.mapnamind.common.view.topicTree.TopicTreeView;
import com.mapnamind.data.dto.TopicTreeDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TopicTreeMapper extends BaseMapper<TopicTreeDTO, TopicTreeView> {
}
