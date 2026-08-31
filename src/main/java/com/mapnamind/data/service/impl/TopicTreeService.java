package com.mapnamind.data.service.impl;

import com.google.gson.reflect.TypeToken;
import com.mapnamind.common.dto.device.DeviceDTO;
import com.mapnamind.common.dto.userAsset.UserAssetDTO;
import com.mapnamind.common.execption.ServiceException;
import com.mapnamind.common.model.entity.TopicTree;
import com.mapnamind.common.view.company.CompanyView;
import com.mapnamind.common.view.device.DeviceView;
import com.mapnamind.common.view.deviceType.DeviceTypeView;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.mapnamind.common.util.Constant.SLASH;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TopicTreeService implements ITopicTreeService {

    private static final Type SERIAL_NUMBERS_TYPE = new TypeToken<List<DeviceView>>() {
    }.getType();
    private static final Type USER_ASSET_DTOs = new TypeToken<List<UserAssetDTO>>() {
    }.getType();
    private final TopicTreeRepository repository;
    private final ApiService apiService;

    @Override
    public List<TopicTreeDTO> findParentsTree() {
        List<DeviceView> devices = apiService.callCollection(ServiceName.USER_GET_ALL_SERIAL_NUMBER_BY_ACCESS_LEVEL, null, SERIAL_NUMBERS_TYPE);
        if (devices.isEmpty()) {
            throw new ServiceException("service.topicTree.common.emptyDevices.9004");
        }
        return findTopicTrees(devices, getDevicesName(devices.stream().map(DeviceView::getId).toList(), null));
    }

    @Override
    public List<TopicTree> findTopicsByDeviceType(Long deviceTypeId) {

        DeviceTypeView deviceType = apiService.call(ServiceName.DEVICE_TYPE_FIND_BY_ID, null,
                List.of(deviceTypeId.toString()), null,
                new TypeToken<DeviceTypeView>() {
                }.getType());
        if (deviceType == null) {
            throw new ServiceException("service.topicTree.common.deviceType.9008");
        }

        List<TopicTree> topics;
        if (deviceType.getHasHub()) {
            DeviceTypeView hubDeviceType = apiService.call(ServiceName.DEVICE_TYPE_FIND_BY_ID, null,
                    List.of(deviceType.getHubId().toString()), null,
                    new TypeToken<DeviceTypeView>() {
                    }.getType());
            topics = repository.findHasHubDeviceTypeTopics(deviceType.getName(), hubDeviceType.getName() + "/data%");
        } else {
            topics = repository.findLeafTopics(deviceType.getName());
        }
        return topics;
    }

    @Override
    public TopicTree update(TopicTree topicTree) {

        if (topicTree.getId() == null) {
            throw new ServiceException("service.topicTree.update.idIsNull.9007");
        }
        var entity = repository.findById(topicTree.getId()).orElseThrow(
                () -> new ServiceException("service.topicTree.update.notFound.9008")
        );
        if (!entity.getTopic().equals(topicTree.getTopic())) {
            throw new ServiceException("service.topicTree.update.notFoundTopic.9009");
        }
        entity.setDescription(topicTree.getDescription());
        if (!topicTree.getIsVisible().equals(entity.getIsVisible())) {
            var children = repository.findAllByParent(entity.getId());
            children.forEach(child -> child.setIsVisible(topicTree.getIsVisible()));
            repository.saveAll(children);
            entity.setIsVisible(topicTree.getIsVisible());
        }
        entity.setUnit(topicTree.getUnit());
        try {
            return repository.save(entity);
        } catch (Exception e) {
            throw new ServiceException("service.topicTree.update.databaseError.9012");
        }
    }

    @Override
    public List<TopicTreeDTO> findChildrenTreeFromParent(Long parentId) {
        var entities = repository.findAllByParent(parentId);
        return Mappers.getMapper(TopicTreeDTOMapper.class).sourcesToTargets(entities)
                .stream().peek(dto -> dto.setCode(dto.getId())).toList();
    }

    @Override
    public List<TopicTreeDTO> findBySerialOrDescription(String deviceName, String serialNumber, String description,
                                                        Long companyId) {
        List<CompanyView> companies;
        if (companyId != null) {
            companies = apiService.callCollection(ServiceName.USER_GET_COMPANY, null,
                    new TypeToken<List<CompanyView>>() {
                    }.getType());
            if (companies.stream().noneMatch(company -> company.getId().equals(companyId))) {
                throw new ServiceException("service.topicTree.common.notExistsMembersInThisCompany.9006");
            }
        }
        List<DeviceView> devices = apiService.call(ServiceName.USER_GET_ALL_SERIAL_NUMBER_BY_ACCESS_LEVEL,
                null, SERIAL_NUMBERS_TYPE);
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
            return findTopicTrees(devices, getDevicesName(devices.stream().map(DeviceView::getId).toList(), null));
        } else if (serialNumber != null && !serialNumber.isEmpty()) {
            var searchDevices = devices.stream().filter(device ->
                    device.getSerialNumber().contains(serialNumber)
            ).toList();
            if (searchDevices.isEmpty()) {
                throw new ServiceException("service.topicTree.findBySerialOrDescription.serialNumberNotExists.9005");
            }
            return findTopicTrees(searchDevices, getDevicesName(devices.stream().map(DeviceView::getId).toList(), null));

        } else if (deviceName != null && !deviceName.isEmpty()) {
            var getAllDeviceNames = getDevicesName(devices.stream().map(DeviceView::getId).toList(), deviceName);
            var searchDevices = devices.stream().filter(device ->
                    getAllDeviceNames.containsKey(device.getSerialNumber())
            ).toList();

            return findTopicTrees(searchDevices, getAllDeviceNames);
        } else if (description != null && !description.isEmpty()) {
            var entities = repository.findByDescription(description);
            var deviceTypeNames = devices.stream().map(DeviceView::getDeviceTypeName).toList();
            var finalTopicTrees = new ArrayList<TopicTree>();
            for (var entity : entities) {
                if (deviceTypeNames.contains(entity.getPath().split(SLASH)[0])) {
                    finalTopicTrees.add(entity);
                    findAllParentsOfThisTopic(entity.getParent(), finalTopicTrees); // Use finalTopicTrees instead of entities
                }

            }
            var dto = generateParentTree(
                    Mappers.getMapper(TopicTreeDTOMapper.class).sourcesToTargets(finalTopicTrees), devices,
                    getDevicesName(devices.stream().map(DeviceView::getId).toList(), null)
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

    private List<TopicTreeDTO> findTopicTrees(List<DeviceView> devices, Map<String, String> nameDevices) {
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
        return generateParentTree(Mappers.getMapper(TopicTreeDTOMapper.class).sourcesToTargets(rootTopicTrees), devices, nameDevices);
    }

    private List<TopicTreeDTO> generateParentTree(List<TopicTreeDTO> dtos, List<DeviceView> deviceViews, Map<String, String> nameDevices) {
        // Step 1: Create a map of ID to TreeNode (optimized to avoid parsing String -> Long multiple times)
        var nodes = dtos.stream()
                .collect(Collectors.toMap(
                                TopicTreeDTO::getId,
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
            var serialNode = TopicTreeDTO.builder()
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

        var topicTrees = new ArrayList<TopicTreeDTO>();
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
        List<UserAssetDTO> userAssetDTOS = apiService.callCollection(ServiceName.USER_DEVICE_FIND_ALL_BY_USER_AND_DEVICE_IDS,
                DeviceDTO.builder().ids(deviceIds).build(), USER_ASSET_DTOs
        );
        return deviceName != null ? userAssetDTOS.stream()
                .filter(dto -> contains(dto.getNickName(), deviceName) || contains(dto.getDeviceTypeName(), deviceName))
                .collect(Collectors.toMap(UserAssetDTO::getSerialNumber, UserAssetDTO::getNickName))
                :
                userAssetDTOS.stream().collect(Collectors.toMap(UserAssetDTO::getSerialNumber, UserAssetDTO::getNickName));
    }

    private boolean contains(String str1, String str2) {
        return str1.toLowerCase().contains(str2.toLowerCase()) || str1.toLowerCase().contains(str2) ||
                str1.contains(str2.toLowerCase());
    }

    @Override
    public TopicTree findByPath(String path) {
        return repository.findByPath(path).orElseThrow(() -> new ServiceException("service.topicTree.notFound.9010"));
    }

    @Override
    public TopicTree findTopicsById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ServiceException("service.topicTree.notFound.9010"));
    }

    @Override
    public List<TopicTree> findAllRoot() {
        return repository.findAllRoot();
    }
}
