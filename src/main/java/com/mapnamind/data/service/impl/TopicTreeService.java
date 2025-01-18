package com.mapnamind.data.service.impl;

import com.google.gson.reflect.TypeToken;
import com.mapnamind.common.execption.ServiceException;
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

    @Override
    public List<TopicTreeDTO> findParentsTree() {
        List<DeviceView> devices = apiService.call(ServiceName.USER_GET_ALL_SERIAL_NUMBER_BY_ACCESS_LEVEL, null, SERIAL_NUMBERS_TYPE);
        if (devices.isEmpty()) {
            throw new ServiceException("service.topicTree.common.emptyDevices.9004");
        }
        return findTopicTrees(devices);
    }

    @Override
    public List<TopicTreeDTO> findChildrenTreeFromParent(Long parentId) {
        List<TopicTree> entities = repository.findAllByParent(parentId);
        return Mappers.getMapper(TopicTreeDTOMapper.class).sourcesToTargets(entities)
                .stream().peek(dto -> dto.setCode(dto.getId())).toList();
    }

    @Override
    public List<TopicTreeDTO> findBySerialOrDescription(String serialNumber, String description) {
        List<DeviceView> devices = apiService.call(ServiceName.USER_GET_ALL_SERIAL_NUMBER_BY_ACCESS_LEVEL, null, SERIAL_NUMBERS_TYPE);
        if (devices == null || devices.isEmpty()) {
            throw new ServiceException("service.topicTree.common.emptyDevices.9004");
        }
        if (serialNumber != null) {
            Optional<DeviceView> device = devices.stream().filter(d -> d.getSerialNumber().equals(serialNumber)).findFirst();
            if (device.isEmpty()) {
                throw new ServiceException("service.topicTree.findBySerialOrDescription.serialNumberNotExists.9005");
            }
            return findTopicTrees(Collections.singletonList(device.get()));

        } else if (description != null) {
            TopicTree entity = repository.findByDescription(description);
            List<TopicTree> rootTopicTrees = Collections.singletonList(entity);
            findAllParentsOfThisTopic(entity.getParent(), rootTopicTrees);
            List<TopicTreeDTO> dto = generateParentTree(Mappers.getMapper(TopicTreeDTOMapper.class).sourcesToTargets(rootTopicTrees), devices);
            devices.clear();
            return dto;
        }
        return List.of();
    }

    private void findAllParentsOfThisTopic(TopicTree parent, List<TopicTree> rootTopicTrees) {
        Optional<TopicTree> entity = repository.findById(parent.getId());
        if (entity.isEmpty()) {
            return;
        }
        rootTopicTrees.add(entity.get());
        if (parent.getParent() != null) {
            findAllParentsOfThisTopic(parent.getParent(), rootTopicTrees);
        }
    }

    private List<TopicTreeDTO> findTopicTrees(List<DeviceView> devices) {
        Set<String> deviceTypeNames = devices.stream()
                .map(device -> device.getHubDeviceTypeName() != null ?
                        device.getHubDeviceTypeName() :
                        device.getDeviceTypeName()
                )
                .collect(Collectors.toSet());
        List<TopicTree> rootTopicTrees = repository.findAllByTopicAndParentIsNull(deviceTypeNames.stream().toList());
        rootTopicTrees.addAll(
                //find all TopicTrees that have `data` values
                repository.findAllByTopicIsDataAndParent(
                        rootTopicTrees.stream().map(TopicTree::getId).toList(), "%alarm%")
        );
        return generateParentTree(Mappers.getMapper(TopicTreeDTOMapper.class).sourcesToTargets(rootTopicTrees), devices);
    }

    private List<TopicTreeDTO> generateParentTree(List<TopicTreeDTO> dtos, List<DeviceView> deviceViews) {
        // Step 1: Create a map of ID to TreeNode (optimized to avoid parsing String -> Long multiple times)
        Map<Long, TopicTreeDTO> nodes = dtos.stream()
                .collect(Collectors.toMap(
                                TopicTreeDTO::getId,
                                row -> row
                        )
                );
        dtos.clear();

        List<TopicTreeDTO> topicTreesThatAreData = nodes.values().stream()
                .filter(node -> "data".equals(node.getTopic()))
                .toList();

        long countId = nodes.keySet().stream()
                .max(Long::compareTo)
                .orElse(1000L) + 1;

        for (DeviceView device : deviceViews) {
            Long id = countId++;
            Long cloneId = countId++;

            // Initialize serial TopicTree node
            TopicTreeDTO serialNode = TopicTreeDTO.builder()
                    .id(id)
                    .topic(device.getSerialNumber())
                    .isProperty(0)
                    .build();

            // Find the corresponding data node using a more efficient method
            Optional<TopicTreeDTO> foundDataNode = topicTreesThatAreData.stream()
                    .filter(node -> node.getPath().contains(device.getDeviceTypeName() + "/") ||
                            node.getPath().contains(device.getHubDeviceTypeName() + "/"))
                    .findFirst();

            foundDataNode.ifPresent(dataNode -> {
                TopicTreeDTO cloneDataNode = dataNode.clone();
                Long rootId = cloneDataNode.getParentId();

                cloneDataNode.setCode(cloneDataNode.getId());
                cloneDataNode.setId(cloneId);
                cloneDataNode.setParentId(id);

                // Attach the serial node as a child to the data node
                serialNode.setParentId(rootId);

                nodes.put(cloneDataNode.getId(), cloneDataNode);
                nodes.put(serialNode.getId(), serialNode);
            });
        }
        topicTreesThatAreData.forEach(node -> nodes.remove(node.getId()));

        List<TopicTreeDTO> topicTrees = new ArrayList<>();
        for (TopicTreeDTO row : nodes.values()) {
            Long parentId = row.getParentId();

            if (parentId == null) {
                topicTrees.add(row); // Directly add to root if topic matches
            } else {
                // Link parent to child (handle null safety)
                Optional.ofNullable(nodes.get(parentId))
                        .ifPresent(parentNode -> parentNode.addChild(row));
            }
        }

        return topicTrees;
    }

}
