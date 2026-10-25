package com.mapnadigital.insights.insights.data.mapper;

import com.mapnadigital.insights.insights.common.mapper.BaseMapper;
import com.mapnadigital.insights.insights.common.model.entity.TopicTree;
import com.mapnadigital.insights.insights.data.dto.TopicTreeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TopicTreeDTOMapper extends BaseMapper<TopicTree, TopicTreeDTO> {

    @Override
    @Mapping(source = "parent.id", target = "parentId")
    TopicTreeDTO sourceToTarget(TopicTree source);

}
