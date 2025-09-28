package com.mapnamind.data.service;

import com.mapnamind.data.dto.TopicTreeDTO;

import java.util.List;

public interface ITopicTreeService {

    List<TopicTreeDTO> findParentsTree();

    List<TopicTreeDTO> findChildrenTreeFromParent(Long parentId);

    List<TopicTreeDTO> findBySerialOrDescription(String deviceName, String serialNumber, String description, Long companyId);

    TopicTreeDTO findByPath(String path);

}
