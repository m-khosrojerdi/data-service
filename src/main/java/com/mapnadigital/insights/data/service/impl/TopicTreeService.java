package com.mapnadigital.insights.data.service.impl;

import com.google.gson.reflect.TypeToken;
import com.mapnadigital.insights.common.execption.ServiceException;
import com.mapnadigital.insights.data.api.ApiClient;
import com.mapnadigital.insights.data.api.DeviceTypeClient;
import com.mapnadigital.insights.data.dto.company.CompanyDto;
import com.mapnadigital.insights.data.dto.device.DeviceDto;
import com.mapnadigital.insights.data.dto.device.DeviceTypeDto;
import com.mapnadigital.insights.data.dto.topicTree.TopicTreeDto;
import com.mapnadigital.insights.data.dto.userDevice.UserAssetDto;
import com.mapnadigital.insights.data.mapper.TopicTreeMapper;
import com.mapnadigital.insights.data.model.entity.TopicTree;
import com.mapnadigital.insights.data.repository.TopicTreeRepository;
import com.mapnadigital.insights.data.service.ITopicTreeService;
import com.mapnadigital.insights.web.api.ApiService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.mapnadigital.insights.common.util.Constant.SLASH;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TopicTreeService implements ITopicTreeService {

    private static final Type SERIAL_NUMBERS_TYPE = new TypeToken<List<DeviceDto>>() {
    }.getType();
    private static final Type USER_ASSET_VIEWS = new TypeToken<List<UserAssetDto>>() {
    }.getType();
    private final TopicTreeRepository repository;
    private final ApiService apiService;
    private final DeviceTypeClient deviceTypeClient;

    @Override
    public List<TopicTreeDto> findParentsTree() {
        List<DeviceDto> devices = apiService.get(ApiClient.USER_GET_ALL_SERIAL_NUMBER_BY_ACCESS_LEVEL, SERIAL_NUMBERS_TYPE);
        if (devices.isEmpty()) {
            throw new ServiceException("service.topicTree.common.emptyDevices.9004");
        }
        return findTopicTrees(devices, getDevicesName(devices.stream().map(DeviceDto::getId).toList(), null));
    }

    @Override
    public List<TopicTreeDto> findTopicsByDeviceType(Long deviceTypeId) {

        DeviceTypeDto deviceType = apiService.get(ApiClient.DEVICE_TYPE_GET_BY_ID,
                List.of(deviceTypeId.toString()),
                new TypeToken<DeviceTypeDto>() {
                }.getType());
        if (deviceType == null) {
            throw new ServiceException("service.topicTree.common.deviceType.9008");
        }

        List<TopicTree> topics;
        if (deviceType.getHasHub()) {
            DeviceTypeDto hubDeviceType = apiService.get(ApiClient.DEVICE_TYPE_GET_BY_ID,
                    List.of(deviceType.getHubId().toString()),
                    new TypeToken<DeviceTypeDto>() {
                    }.getType());
            topics = repository.findHasHubDeviceTypeTopics(deviceType.getName(), hubDeviceType.getName() + "/data%");
        } else {
            topics = repository.findLeafTopics(deviceType.getName());
        }
        return Mappers.getMapper(TopicTreeMapper.class).sourcesToTargets(topics);
    }

    @Override
    public TopicTreeDto update(TopicTreeDto view) {

        if (view.getId() == null) {
            throw new ServiceException("service.topicTree.update.idIsNull.9007");
        }
        var entity = repository.findById(view.getId()).orElseThrow(
                () -> new ServiceException("service.topicTree.update.notFound.9008")
        );
        if (!entity.getTopic().equals(view.getTopic())) {
            throw new ServiceException("service.topicTree.update.notFoundTopic.9009");
        }
        entity.setDescription(view.getDescription());
        if (!view.getIsVisible().equals(entity.getIsVisible())) {
            var children = repository.findAllByParent(entity.getId());
            children.forEach(child -> child.setIsVisible(view.getIsVisible()));
            repository.saveAll(children);
            entity.setIsVisible(view.getIsVisible());
        }
        entity.setUnit(view.getUnit());
        try {
            return Mappers.getMapper(TopicTreeMapper.class).sourceToTarget(repository.save(entity));
        } catch (Exception e) {
            throw new ServiceException("service.topicTree.update.databaseError.9012");
        }
    }

    @Override
    public List<TopicTreeDto> findChildrenTreeFromParent(Long parentId) {
        var entities = repository.findAllByParent(parentId);
        return Mappers.getMapper(TopicTreeMapper.class).sourcesToTargets(entities)
                .stream().peek(dto -> dto.setCode(dto.getId())).toList();
    }

    @Override
    public List<TopicTreeDto> findBySerialOrDescription(String deviceName, String serialNumber, String description,
                                                        Long companyId) {
        List<CompanyDto> companies;
        if (companyId != null) {
            companies = apiService.get(ApiClient.USER_GET_COMPANY, new TypeToken<List<CompanyDto>>() {
            }.getType());
            if (companies.stream().noneMatch(company -> company.getId().equals(companyId))) {
                throw new ServiceException("service.topicTree.common.notExistsMembersInThisCompany.9006");
            }
        }
        List<DeviceDto> devices = apiService.get(ApiClient.USER_GET_ALL_SERIAL_NUMBER_BY_ACCESS_LEVEL, SERIAL_NUMBERS_TYPE);
        if (devices == null || devices.isEmpty()) {
            throw new ServiceException("service.topicTree.common.emptyDevices.9004");
        }
        if (companyId != null) {
            devices = devices.stream().filter(device -> device.getCompany() == companyId).toList();
            if (devices.isEmpty()) {
                return List.of();
            }
        }
        if (serialNumber == null && deviceName == null && description == null) {
            return findTopicTrees(devices, getDevicesName(devices.stream().map(DeviceDto::getId).toList(), null));
        } else if (serialNumber != null && !serialNumber.isEmpty()) {
            var searchDevices = devices.stream().filter(device ->
                    device.getSerialNumber().contains(serialNumber)
            ).toList();
            if (searchDevices.isEmpty()) {
                throw new ServiceException("service.topicTree.findBySerialOrDescription.serialNumberNotExists.9005");
            }
            return findTopicTrees(searchDevices, getDevicesName(devices.stream().map(DeviceDto::getId).toList(), null));

        } else if (deviceName != null && !deviceName.isEmpty()) {
            var getAllDeviceNames = getDevicesName(devices.stream().map(DeviceDto::getId).toList(), deviceName);
            var searchDevices = devices.stream().filter(device ->
                    getAllDeviceNames.containsKey(device.getSerialNumber())
            ).toList();

            return findTopicTrees(searchDevices, getAllDeviceNames);
        } else if (description != null && !description.isEmpty()) {
            var entities = repository.findByDescription(description);
            var deviceTypeNames = devices.stream().map(DeviceDto::getDeviceTypeName).toList();
            var finalTopicTrees = new ArrayList<TopicTree>();
            for (var entity : entities) {
                if (deviceTypeNames.contains(entity.getPath().split(SLASH)[0])) {
                    finalTopicTrees.add(entity);
                    findAllParentsOfThisTopic(entity.getParent(), finalTopicTrees); // Use finalTopicTrees instead of entities
                }

            }
            var dto = generateParentTree(
                    Mappers.getMapper(TopicTreeMapper.class).sourcesToTargets(finalTopicTrees), devices,
                    getDevicesName(devices.stream().map(DeviceDto::getId).toList(), null)
            );
            devices.clear();
            return dto;
        }
        return List.of();
    }

    private void findAllParentsOfThisTopic(TopicTree parent, List<TopicTree> rootTopicTrees) {
        var entity = repository.findById(parent.getId());
        if (entity.isEmpty()) {
            return;
        }
        if (!rootTopicTrees.stream().map(TopicTree::getId).toList().contains(entity.get().getId())) {
            rootTopicTrees.add(entity.get());
        }
        if (parent.getParent() != null) {
            findAllParentsOfThisTopic(parent.getParent(), rootTopicTrees);
        }
    }

    private List<TopicTreeDto> findTopicTrees(List<DeviceDto> devices, Map<String, String> nameDevices) {
        var deviceTypeNames = devices.stream()
                .map(device -> device.getHubDeviceTypeName() != null ?
                        device.getHubDeviceTypeName() :
                        device.getDeviceTypeName()
                )
                .collect(Collectors.toSet());
        var sort = Sort.by("description").ascending();
        var rootTopicTrees = repository.findAllByTopicAndParentIsNull(deviceTypeNames.stream().toList(), sort);
        rootTopicTrees.addAll(
                //find all TopicTrees that have `data` values
                repository.findAllByTopicIsDataAndParent(rootTopicTrees.stream().map(TopicTree::getId).toList(), "%alarm%", sort));
        return generateParentTree(Mappers.getMapper(TopicTreeMapper.class).sourcesToTargets(rootTopicTrees), devices, nameDevices);
    }

    private List<TopicTreeDto> generateParentTree(List<TopicTreeDto> dtos, List<DeviceDto> deviceViews, Map<String, String> nameDevices) {
        // Step 1: Create a map of ID to TreeNode (optimized to avoid parsing String -> Long multiple times)
        var nodes = dtos.stream()
                .collect(Collectors.toMap(
                                TopicTreeDto::getId,
                                row -> row
                        )
                );
        dtos.clear();

        var topicTreesThatAreData = nodes.values().stream()
                .filter(node -> "data".equals(node.getTopic()))
                .toList();

        var countId = nodes.keySet().stream()
                .max(Long::compareTo)
                .orElse(1000L) + 1;

        for (var device : deviceViews) {
            var id = countId++;
            var cloneId = countId++;

            // Initialize serial TopicTree node
            var serialNode = TopicTreeDto.builder()
                    .id(id)
                    .topic(device.getSerialNumber())
                    .deviceName(nameDevices.get(device.getSerialNumber()))
                    .isProperty(0)
                    .hasChildren(true)
                    .build();

            // Find the corresponding data node using a more efficient method
            var foundDataNode = topicTreesThatAreData.stream()
                    .filter(node -> node.getPath().contains(device.getDeviceTypeName() + SLASH) ||
                            node.getPath().contains(device.getHubDeviceTypeName() + SLASH))
                    .findFirst();

            foundDataNode.ifPresent(dataNode -> {
                var cloneDataNode = dataNode.clone();
                var rootId = cloneDataNode.getParentId();

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

        var topicTrees = new ArrayList<TopicTreeDto>();
        for (var row : nodes.values()) {
            var parentId = row.getParentId();

            if (parentId == null) {
                topicTrees.add(row); // Directly add to root if topic matches
            } else {
                // Link parent to child (handle null safety)
                Optional.ofNullable(nodes.get(parentId))
                        .ifPresent(parentNode -> parentNode.addChild(row));
            }
        }
        topicTrees.sort(Comparator.comparing(d -> d.getDescription() != null ? d.getDescription() : d.getTopic()));
        return topicTrees;
    }

    private Map<String, String> getDevicesName(List<Long> deviceIds, String deviceName) {
        List<UserAssetDto> userAssetDTOS = apiService.get(ApiClient.USER_DEVICE_FIND_ALL_BY_USER_AND_DEVICE_IDS,
                deviceIds.stream().map(String::valueOf).toList(), USER_ASSET_VIEWS
        );
        return deviceName != null ? userAssetDTOS.stream()
                .filter(dto -> contains(dto.getNickName(), deviceName) || contains(dto.getDeviceTypeName(), deviceName))
                .collect(Collectors.toMap(UserAssetDto::getSerialNumber, UserAssetDto::getNickName))
                :
                userAssetDTOS.stream().collect(Collectors.toMap(UserAssetDto::getSerialNumber, UserAssetDto::getNickName));
    }

    private boolean contains(String str1, String str2) {
        return str1.toLowerCase().contains(str2.toLowerCase()) || str1.toLowerCase().contains(str2) ||
                str1.contains(str2.toLowerCase());
    }

    @Override
    public TopicTreeDto findByPath(String path) {
        return Mappers.getMapper(TopicTreeMapper.class).sourceToTarget(
                repository.findByPath(path).orElseThrow(() -> new ServiceException("service.topicTree.notFound.9010"))
        );
    }

    @Override
    public TopicTreeDto findTopicsById(Long id) {
        return Mappers.getMapper(TopicTreeMapper.class).sourceToTarget(
                repository.findById(id).orElseThrow(() -> new ServiceException("service.topicTree.notFound.9010")));
    }

    @Override
    public List<TopicTreeDto> findTopicsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return Mappers.getMapper(TopicTreeMapper.class).sourcesToTargets(repository.findAllById(ids));
    }

    @Override
    public List<TopicTreeDto> findAllRoot() {
        List<String> deviceTypeNames = deviceTypeClient.findAllNames();
        if (deviceTypeNames.isEmpty()) {
            return List.of();
        }
        return Mappers.getMapper(TopicTreeMapper.class).sourcesToTargets(repository.findAllRoot(deviceTypeNames));
    }
}
