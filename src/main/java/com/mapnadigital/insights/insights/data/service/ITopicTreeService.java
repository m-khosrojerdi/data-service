package com.mapnadigital.insights.insights.data.service;

import com.mapnadigital.insights.insights.common.model.entity.TopicTree;
import com.mapnadigital.insights.insights.data.dto.TopicTreeDTO;

import java.util.List;

public interface ITopicTreeService {

    List<TopicTreeDTO> findParentsTree();

    List<TopicTree> findTopicsByDeviceType(Long id);

    TopicTree update(TopicTree dto);

    List<TopicTreeDTO> findChildrenTreeFromParent(Long parentId);

    List<TopicTreeDTO> findBySerialOrDescription(String deviceName, String serialNumber, String description, Long companyId);

    TopicTree findByPath(String path);

    TopicTree findTopicsById(Long id);

    List<TopicTree> findAllRoot();

}
