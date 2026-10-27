package com.mapnadigital.insights.insights.data.service;

import com.mapnadigital.insights.insights.common.dto.topicTree.TopicTreeDto;

import java.util.List;

public interface ITopicTreeService {

    List<TopicTreeDto> findParentsTree();

    List<TopicTreeDto> findTopicsByDeviceType(Long id);

    TopicTreeDto update(TopicTreeDto view);

    List<TopicTreeDto> findChildrenTreeFromParent(Long parentId);

    List<TopicTreeDto> findBySerialOrDescription(String deviceName, String serialNumber, String description, Long companyId);

    TopicTreeDto findByPath(String path);

    TopicTreeDto findTopicsById(Long id);

    List<TopicTreeDto> findAllRoot();

}
