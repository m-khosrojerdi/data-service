package com.mapnamind.data.service.impl;

import com.google.gson.reflect.TypeToken;
import com.mapnamind.common.model.entity.TopicTree;
import com.mapnamind.common.view.device.DeviceView;
import com.mapnamind.data.dto.TopicTreeDTO;
import com.mapnamind.data.mapper.TopicTreeDTOMapper;
import com.mapnamind.data.repository.TopicTreeRepository;
import com.mapnamind.data.service.ITopicTreeService;
import com.mapnamind.web.api.ApiService;
import com.mapnamind.web.enumeration.ServiceName;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TopicTreeService implements ITopicTreeService {

    private static final Type SERIAL_NUMBERS_TYPE = new TypeToken<List<DeviceView>>() {
    }.getType();
    private final TopicTreeRepository repository;
    private final ApiService apiService;

    public List<TopicTreeDTO> findChildrenTreeFromParent(Long parentId) {
        List<TopicTree> entities = repository.findAllByParent(parentId);
        return Mappers.getMapper(TopicTreeDTOMapper.class).sourcesToTargets(entities);
    }

    @Override
    public List<TopicTreeDTO> findParentsTree() {
        List<DeviceView> devices = apiService.call(ServiceName.USER_GET_ALL_SERIAL_NUMBER_BY_ACCESS_LEVEL, null, SERIAL_NUMBERS_TYPE);

        Set<String> deviceTypeNames = devices.stream()
                .map(DeviceView::getDeviceTypeName)
                .collect(Collectors.toSet());
        List<TopicTree> topicTrees = repository.findAllByTopicAndParentIsNull(deviceTypeNames.stream().toList());
        topicTrees.addAll(
                repository.findAllByTopicIsDataAndParent(
                        topicTrees.stream().map(TopicTree::getId).toList(), "%alarm%")
        );

        List<TopicTreeDTO> dto = new ArrayList<>();
        if (!devices.isEmpty()) {
            dto = generateParentTree(Mappers.getMapper(TopicTreeDTOMapper.class).sourcesToTargets(topicTrees), devices);
            if (!devices.isEmpty()) {
                devices.clear();
            }
        }
        return dto;
    }

    private List<TopicTreeDTO> generateParentTree(List<TopicTreeDTO> data, List<DeviceView> deviceViews) {
        // Step 1: Create a map of ID to TreeNode (optimized to avoid parsing String -> Long multiple times)
        Map<Long, TopicTreeDTO> nodeMap = data.stream()
                .collect(Collectors.toMap(
                        TopicTreeDTO::getId,
                        row -> row
                ));

        List<TopicTreeDTO> root = new ArrayList<>();
        for (TopicTreeDTO row : data) {
            Long id = row.getId();
            Long parentId = row.getParentId();

            if (parentId == null) {
                root.add(row); // Directly add to root if topic matches
            } else {
                // Link parent to child (handle null safety)
                Optional.ofNullable(nodeMap.get(parentId))
                        .ifPresent(parentNode -> parentNode.addChild(row));
            }
        }
        data.clear();

        // Step 4: Handle serial number and device-view specific logic
        List<TopicTreeDTO> serialParentNode = nodeMap.values().stream()
                .filter(node -> "data".equals(node.getTopic()))
                .toList();

        long startingSerialId = nodeMap.keySet().stream()
                .max(Long::compareTo)
                .orElse(1000L) + 1;

        for (DeviceView deviceView : deviceViews) {
            Long id = startingSerialId++;
            TopicTreeDTO serialNode = TopicTreeDTO.builder()
                    .id(id)
                    .topic(deviceView.getSerialNumber())
                    .isProperty(0)
                    .build();

            // Find the corresponding data node using a more efficient method
            Optional<TopicTreeDTO> dataNodeOpt = serialParentNode.stream()
                    .filter(node -> node.getPath().contains(deviceView.getDeviceTypeName() + "/"))
                    .findFirst();

            dataNodeOpt.ifPresent(dataNode -> {
                dataNode.setParentId(id);  // Attach parent ID to data node

                // Attach the serial node as a child to the data node
                serialNode.addChild(dataNode);

                // Update root nodes to reflect new serial node in place of old data node
                root.forEach(rootNode -> rootNode.setChildren(
                        rootNode.getChildren().stream()
                                .map(child -> {
                                    if (child.getId().equals(dataNode.getId())) {
                                        // Set the parentId of the serialNode to rootNode's ID
                                        serialNode.setParentId(rootNode.getId());
                                        return serialNode; // Replace the dataNode with serialNode
                                    }
                                    return child;
                                })
                                .collect(Collectors.toList())
                ));
            });
        }

        return root;
    }

    private List<TopicTree> findChildrenFromParent(Long parentId) {
        try {
            List<TopicTree> children = repository.findAllByParent(parentId);
            if (!children.isEmpty()) {
                for (TopicTree topicTree : children) {
                    List<TopicTree> topicTrees = findChildrenFromParent(topicTree.getId());
                    if (!topicTrees.isEmpty()) {
                        children.addAll(topicTrees);
                    }
                    topicTrees.clear();
                }
            }
            return children;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List<TopicTreeDTO> generateChildrenTree(List<TopicTreeDTO> data) {
        Map<Long, TopicTreeDTO> nodeMap = data.stream()
                .collect(Collectors.toMap(
                        TopicTreeDTO::getId,
                        row -> row
                ));

        List<TopicTreeDTO> root = new ArrayList<>();
        for (TopicTreeDTO row : data) {
            Long parentId = row.getParentId();
            // Link parent to child (handle null safety)
            Optional.ofNullable(nodeMap.get(parentId))
                    .ifPresent(parentNode -> parentNode.addChild(row));
        }
        data.clear();
        return root;
    }

}
