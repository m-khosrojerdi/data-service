package com.mapnadigital.insights.insights.data.controller;

import com.mapnadigital.insights.insights.common.util.Constant;
import com.mapnadigital.insights.insights.common.util.Toolbox;
import com.mapnadigital.insights.insights.common.view.topicTree.TopicTreeView;
import com.mapnadigital.insights.insights.data.service.ITopicTreeService;
import com.mapnadigital.insights.insights.data.view.DateView;
import com.mapnadigital.insights.web.Base;
import com.mapnadigital.insights.web.annotation.HasAnyRole;
import com.mapnadigital.insights.web.enumeration.Role;
import com.mapnadigital.insights.web.view.ResponseBody;
import com.mapnadigital.insights.web.view.ResponseEntityTemplate;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.mapnadigital.insights.web.enumeration.Swagger.ACCESS_TOKEN;
import static com.mapnadigital.insights.web.enumeration.Swagger.LANGUAGE;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/topic-tree")
@RequiredArgsConstructor
public class TopicTreeController extends Base {

    private final ITopicTreeService service;
    private final ResponseEntityTemplate responseEntityTemplate;

    @GetMapping("/root-tree")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<List<TopicTreeView>> rootTree() {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                service.findParentsTree()
        );
    }

    @GetMapping("/children-tree/{parentId}")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<List<TopicTreeView>> childrenTree(@PathVariable("parentId") Long parentId) {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                service.findChildrenTreeFromParent(parentId)
        );
    }

    @GetMapping("/search")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<List<TopicTreeView>> search(@RequestParam(name = "deviceName", required = false) String deviceName,
                                                    @RequestParam(name = "serialNumber", required = false) String serialNumber,
                                                    @RequestParam(name = "description", required = false) String description,
                                                    @RequestParam(name = "company", required = false) Long company) {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                (
                        service.findBySerialOrDescription(deviceName, serialNumber, description, company)
                )
        );
    }

    @GetMapping("/get")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER})
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<List<TopicTreeView>> getTopics(@RequestParam Long deviceTypeId) {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                service.findTopicsByDeviceType(deviceTypeId)
        );
    }

    @Hidden
    @GetMapping("/get/{id}")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<TopicTreeView> getTopic(@PathVariable Long id) {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                service.findTopicsById(id)
        );
    }

    @GetMapping("/root/get-all")
    @HasAnyRole(Role.ADMIN)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<List<TopicTreeView>> getAllRoot() {
        return responseEntityTemplate.response("controller.topicTree.getAllRoot.success.200",
                service.findAllRoot()
        );
    }

    @PutMapping("/update")
    @HasAnyRole(Role.ADMIN)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)
    })
    public ResponseBody<TopicTreeView> update(@RequestBody TopicTreeView view) {
        return responseEntityTemplate.response("controller.topicTree.updateTopicTree.success.200",
                service.update(view));
    }

    @GetMapping("/date")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<DateView> date() {
        return responseEntityTemplate.response("common.200",
                DateView.builder()
                        .date(Toolbox.convertDateToString(Constant.UI_DATE_FORMATTING_New, new Date()))
                        .build()
        );
    }

}
