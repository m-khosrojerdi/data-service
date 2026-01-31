package com.mapnamind.data.controller;

import com.mapnamind.data.dto.BaselineDTO;
import com.mapnamind.data.mapper.BaselineMapper;
import com.mapnamind.data.service.impl.BaselineService;
import com.mapnamind.data.view.BaselineView;
import com.mapnamind.web.Base;
import com.mapnamind.web.enumeration.AuthorizeType;
import com.mapnamind.web.view.ResponseBody;
import com.mapnamind.web.view.ResponseEntityTemplate;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.mapnamind.web.enumeration.Swagger.ACCESS_TOKEN;
import static com.mapnamind.web.enumeration.Swagger.LANGUAGE;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/baseline")
@RequiredArgsConstructor
public class BaselineController extends Base {
    private final BaselineService baselineService;
    private final ResponseEntityTemplate responseEntityTemplate;


    @PostMapping
    @PreAuthorize((AuthorizeType.CUSTOMER))
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<BaselineView> get(@Valid @RequestBody BaselineView view) {
        return responseEntityTemplate.response("controller.baseline.get.successful.200",
                map(BaselineMapper.class).sourceToTarget(baselineService.get(view.getTopic(), view.getInterval())));
    }

    @PutMapping
    @PreAuthorize(AuthorizeType.CUSTOMER)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<BaselineView> updateOrDelete(@Valid @RequestBody BaselineView view) {

        BaselineDTO dto = switch (view.getValue() == null ? "delete" : "save") {
            case "delete" -> {
                baselineService.delete(map(BaselineMapper.class).targetToSource(view));
                yield BaselineDTO.builder().build();
            }
            default -> baselineService.save(map(BaselineMapper.class).targetToSource(view));
        };

        return responseEntityTemplate.response("controller.baseline.put.successful.200",
                map(BaselineMapper.class).sourceToTarget(dto));
    }
}
