package com.mapnamind.data.mapper;

import com.mapnamind.common.mapper.BaseMapper;
import com.mapnamind.common.model.entity.TopicTree;
import com.mapnamind.data.dto.TopicTreeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TopicTreeDTOMapper extends BaseMapper<TopicTree, TopicTreeDTO> {

    @Override
    @Mapping(source = "parent.id", target = "parentId")
    TopicTreeDTO sourceToTarget(TopicTree source);

}
