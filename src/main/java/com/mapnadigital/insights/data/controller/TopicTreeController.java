package com.mapnadigital.insights.data.controller;

import com.mapnadigital.insights.common.util.Constant;
import com.mapnadigital.insights.common.util.Toolbox;
import com.mapnadigital.insights.data.dto.topicTree.TopicTreeDto;
import com.mapnadigital.insights.data.service.ITopicTreeService;
import com.mapnadigital.insights.data.dto.topicTree.DateDto;
import com.mapnadigital.insights.web.Base;
import com.mapnadigital.insights.web.annotation.HasAnyRole;
import com.mapnadigital.insights.web.enumeration.Role;
import com.mapnadigital.insights.web.dto.ResponseBody;
import com.mapnadigital.insights.web.dto.ResponseEntityTemplate;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.mapnadigital.insights.web.enumeration.Swagger.*;

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
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    public ResponseBody<List<TopicTreeDto>> rootTree() {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                service.findParentsTree()
        );
    }

    @GetMapping("/children-tree/{parentId}")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    public ResponseBody<List<TopicTreeDto>> childrenTree(@PathVariable("parentId") Long parentId) {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                service.findChildrenTreeFromParent(parentId)
        );
    }

    @GetMapping("/search")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    public ResponseBody<List<TopicTreeDto>> search(@RequestParam(name = "deviceName", required = false) String deviceName,
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
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    public ResponseBody<List<TopicTreeDto>> getTopics(@RequestParam Long deviceTypeId) {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                service.findTopicsByDeviceType(deviceTypeId)
        );
    }

    @Hidden
    @GetMapping("/get/{id}")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    public ResponseBody<TopicTreeDto> getTopic(@PathVariable Long id) {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                service.findTopicsById(id)
        );
    }

    @Hidden
    @GetMapping("/get-all-by-ids")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    public ResponseBody<List<TopicTreeDto>> getTopicsByIds(@RequestParam("ids") List<Long> ids) {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                service.findTopicsByIds(ids)
        );
    }

    @GetMapping("/root/get-all")
    @HasAnyRole(Role.ADMIN)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    public ResponseBody<List<TopicTreeDto>> getAllRoot() {
        return responseEntityTemplate.response("controller.topicTree.getAllRoot.success.200",
                service.findAllRoot()
        );
    }

    @PutMapping("/update")
    @HasAnyRole(Role.ADMIN)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    public ResponseBody<TopicTreeDto> update(@RequestBody TopicTreeDto view) {
        return responseEntityTemplate.response("controller.topicTree.updateTopicTree.success.200",
                service.update(view));
    }

    @GetMapping("/date")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    public ResponseBody<DateDto> date() {
        return responseEntityTemplate.response("common.200",
                DateDto.builder()
                        .date(Toolbox.convertDateToString(Constant.UI_DATE_FORMATTING_New, new Date()))
                        .build()
        );
    }

}
