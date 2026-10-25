package com.mapnadigital.insights.insights.data.mapper;

import com.mapnadigital.insights.insights.common.mapper.BaseMapper;
import com.mapnadigital.insights.insights.common.view.topicTree.TopicTreeView;
import com.mapnadigital.insights.insights.data.dto.TopicTreeDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TopicTreeViewMapper extends BaseMapper<TopicTreeDTO, TopicTreeView> {
}
