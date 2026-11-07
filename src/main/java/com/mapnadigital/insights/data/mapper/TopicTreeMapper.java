package com.mapnadigital.insights.data.mapper;

import com.mapnadigital.insights.data.dto.topicTree.TopicTreeDto;
import com.mapnadigital.insights.common.mapper.BaseMapper;
import com.mapnadigital.insights.data.model.entity.TopicTree;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Entity/view conversion for the topic tree owned by this module.
 * <p>
 * It replaces the {@code map-common} mapper, which mapped the shared {@code TopicTree} entity; the entity now lives
 * in {@code data.entity} so the module does not import an entity of the shared library.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Mapper
public interface TopicTreeMapper extends BaseMapper<TopicTree, TopicTreeDto> {

    @Override
    @Mapping(source = "parent.id", target = "parentId")
    TopicTreeDto sourceToTarget(TopicTree source);

    @Override
    @Mapping(source = "parentId", target = "parent.id")
    TopicTree targetToSource(TopicTreeDto target);

}
