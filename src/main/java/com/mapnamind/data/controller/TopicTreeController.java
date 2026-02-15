package com.mapnamind.data.controller;

import com.mapnamind.common.util.Constant;
import com.mapnamind.common.util.Toolbox;
import com.mapnamind.data.dto.TopicTreeDTO;
import com.mapnamind.data.mapper.TopicTreeMapper;
import com.mapnamind.data.service.ITopicTreeService;
import com.mapnamind.data.view.DateView;
import com.mapnamind.data.view.TopicTreeView;
import com.mapnamind.web.Base;
import com.mapnamind.web.enumeration.AuthorizeType;
import com.mapnamind.web.view.ResponseBody;
import com.mapnamind.web.view.ResponseEntityTemplate;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.mapnamind.web.enumeration.Swagger.ACCESS_TOKEN;
import static com.mapnamind.web.enumeration.Swagger.LANGUAGE;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/topic-tree")
@RequiredArgsConstructor
public class TopicTreeController extends Base {

    private final ITopicTreeService service;
    private final ResponseEntityTemplate responseEntityTemplate;

    @GetMapping("/root-tree")
    @PreAuthorize(AuthorizeType.ADMIN_CUSTOMER_MANUFACTURE_AGENCY)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<List<TopicTreeView>> rootTree() {
        List<TopicTreeDTO> dto = service.findParentsTree();
        List<TopicTreeView> view = map(TopicTreeMapper.class).sourcesToTargets(dto);
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200", view);
    }

    @GetMapping("/children-tree/{parentId}")
    @PreAuthorize(AuthorizeType.ADMIN_CUSTOMER_MANUFACTURE_AGENCY)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<List<TopicTreeView>> childrenTree(@PathVariable("parentId") Long parentId) {
        List<TopicTreeDTO> dto = service.findChildrenTreeFromParent(parentId);
        List<TopicTreeView> view = map(TopicTreeMapper.class).sourcesToTargets(dto);
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200", view);
    }

    @GetMapping("/search")
    @PreAuthorize(AuthorizeType.ADMIN_CUSTOMER_MANUFACTURE_AGENCY)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<List<TopicTreeView>> search(@RequestParam(name = "deviceName", required = false) String deviceName,
                                                    @RequestParam(name = "serialNumber", required = false) String serialNumber,
                                                    @RequestParam(name = "description", required = false) String description,
                                                    @RequestParam(name = "company", required = false) Long company) {
        List<TopicTreeDTO> dto = service.findBySerialOrDescription(deviceName, serialNumber, description, company);
        List<TopicTreeView> view = map(TopicTreeMapper.class).sourcesToTargets(dto);
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200", view);
    }

    @GetMapping("/get")
    @PreAuthorize(AuthorizeType.ADMIN_CUSTOMER)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<List<TopicTreeView>> getTopics(@RequestParam Long deviceTypeId) {
        List<TopicTreeDTO> dto = service.findTopicsByDeviceType(deviceTypeId);
        List<TopicTreeView> view = map(TopicTreeMapper.class).sourcesToTargets(dto);
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200", view);
    }

    @PutMapping("/update")
    @PreAuthorize(AuthorizeType.ADMIN)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)
    })
    public ResponseBody<TopicTreeView> update(@RequestBody TopicTreeView view) {
        TopicTreeDTO dto = service.updateTopic(map(TopicTreeMapper.class).targetToSource(view));
        TopicTreeView updatedView = map(TopicTreeMapper.class).sourceToTarget(dto);
        return responseEntityTemplate.response("controller.topicTree.updateTopicTree.success.200", updatedView);

    }

    @GetMapping("/date")
    @PreAuthorize(AuthorizeType.ADMIN_CUSTOMER_MANUFACTURE_AGENCY)
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
