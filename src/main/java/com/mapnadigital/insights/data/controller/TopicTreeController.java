package com.mapnadigital.insights.data.controller;

import com.mapnadigital.insights.common.util.Constant;
import com.mapnadigital.insights.common.util.Toolbox;
import com.mapnadigital.insights.data.documentation.TopicTreeDocumentation;
import com.mapnadigital.insights.data.dto.topicTree.TopicTreeDto;
import com.mapnadigital.insights.data.service.ITopicTreeService;
import com.mapnadigital.insights.data.dto.topicTree.DateDto;
import com.mapnadigital.insights.web.Base;
import com.mapnadigital.insights.web.annotation.HasAnyRole;
import com.mapnadigital.insights.web.enumeration.Role;
import com.mapnadigital.insights.web.dto.ResponseBody;
import com.mapnadigital.insights.web.dto.ResponseEntityTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * REST API of the topic tree.
 * <p>
 * The endpoints are documented with {@code springdoc-openapi}; the documentation lives in
 * {@link TopicTreeDocumentation}.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/topic-tree")
@RequiredArgsConstructor
public class TopicTreeController extends Base implements TopicTreeDocumentation {

    private final ITopicTreeService service;
    private final ResponseEntityTemplate responseEntityTemplate;

    @Override
    @GetMapping("/root-tree")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    public ResponseBody<List<TopicTreeDto>> rootTree() {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                service.findParentsTree()
        );
    }

    @Override
    @GetMapping("/children-tree/{parentId}")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    public ResponseBody<List<TopicTreeDto>> childrenTree(@PathVariable("parentId") Long parentId) {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                service.findChildrenTreeFromParent(parentId)
        );
    }

    @Override
    @GetMapping("/search")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    public ResponseBody<List<TopicTreeDto>> search(
            @RequestParam(name = "deviceName", required = false) String deviceName,
            @RequestParam(name = "serialNumber", required = false) String serialNumber,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "company", required = false) Long company) {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                (
                        service.findBySerialOrDescription(deviceName, serialNumber, description, company)
                )
        );
    }

    @Override
    @GetMapping("/get")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER})
    public ResponseBody<List<TopicTreeDto>> getTopics(@RequestParam Long deviceTypeId) {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                service.findTopicsByDeviceType(deviceTypeId)
        );
    }

    @Override
    @GetMapping("/get/{id}")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    public ResponseBody<TopicTreeDto> getTopic(@PathVariable Long id) {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                service.findTopicsById(id)
        );
    }

    @Override
    @GetMapping("/get-all-by-ids")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    public ResponseBody<List<TopicTreeDto>> getTopicsByIds(@RequestParam("ids") List<Long> ids) {
        return responseEntityTemplate.response("controller.topicTree.getTopicTree.success.200",
                service.findTopicsByIds(ids)
        );
    }

    @Override
    @GetMapping("/root/get-all")
    @HasAnyRole(Role.ADMIN)
    public ResponseBody<List<TopicTreeDto>> getAllRoot() {
        return responseEntityTemplate.response("controller.topicTree.getAllRoot.success.200",
                service.findAllRoot()
        );
    }

    @Override
    @PutMapping("/update")
    @HasAnyRole(Role.ADMIN)
    public ResponseBody<TopicTreeDto> update(@RequestBody TopicTreeDto view) {
        return responseEntityTemplate.response("controller.topicTree.updateTopicTree.success.200",
                service.update(view));
    }

    @Override
    @GetMapping("/date")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    public ResponseBody<DateDto> date() {
        return responseEntityTemplate.response("common.200",
                DateDto.builder()
                        .date(Toolbox.convertDateToString(Constant.UI_DATE_FORMATTING_New, new Date()))
                        .build()
        );
    }

}
