package com.mapnadigital.insights.data.service;

import com.mapnadigital.insights.data.dto.topicTree.TopicTreeDto;

import java.util.List;

public interface ITopicTreeService {

    List<TopicTreeDto> findParentsTree();

    List<TopicTreeDto> findTopicsByDeviceType(Long id);

    TopicTreeDto update(TopicTreeDto view);

    List<TopicTreeDto> findChildrenTreeFromParent(Long parentId);

    List<TopicTreeDto> findBySerialOrDescription(String deviceName, String serialNumber, String description, Long companyId);

    TopicTreeDto findByPath(String path);

    TopicTreeDto findTopicsById(Long id);

    /**
     * Reads the given topics of the tree in a single call.
     *
     * @param ids the identifiers of the topics
     * @return the found topics; unknown identifiers are simply absent
     */
    List<TopicTreeDto> findTopicsByIds(List<Long> ids);

    List<TopicTreeDto> findAllRoot();

}
