package com.mapnadigital.insights.insights.data.service;

import com.mapnadigital.insights.insights.common.view.topicTree.TopicTreeView;

import java.util.List;

public interface ITopicTreeService {

    List<TopicTreeView> findParentsTree();

    List<TopicTreeView> findTopicsByDeviceType(Long id);

    TopicTreeView update(TopicTreeView view);

    List<TopicTreeView> findChildrenTreeFromParent(Long parentId);

    List<TopicTreeView> findBySerialOrDescription(String deviceName, String serialNumber, String description, Long companyId);

    TopicTreeView findByPath(String path);

    TopicTreeView findTopicsById(Long id);

    List<TopicTreeView> findAllRoot();

}
