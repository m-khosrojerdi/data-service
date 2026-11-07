package com.mapnadigital.insights.data.controller;

import com.mapnadigital.insights.data.dto.baseline.BaselineDto;
import com.mapnadigital.insights.data.service.impl.BaselineService;
import com.mapnadigital.insights.web.Base;
import com.mapnadigital.insights.web.annotation.HasAnyRole;
import com.mapnadigital.insights.web.enumeration.Role;
import com.mapnadigital.insights.web.dto.ResponseBody;
import com.mapnadigital.insights.web.dto.ResponseEntityTemplate;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.mapnadigital.insights.web.enumeration.Swagger.ACCESS_TOKEN;
import static com.mapnadigital.insights.web.enumeration.Swagger.LANGUAGE;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/baseline")
@RequiredArgsConstructor
public class BaselineController extends Base {
    private final BaselineService baselineService;
    private final ResponseEntityTemplate responseEntityTemplate;


    @PostMapping
    @HasAnyRole(Role.CUSTOMER)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<BaselineDto> get(@Valid @RequestBody BaselineDto view) {
        return responseEntityTemplate.response("controller.baseline.get.successful.200",
                baselineService.get(view.getTopic(), view.getInterval()));
    }

    @PutMapping
    @HasAnyRole(Role.CUSTOMER)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<BaselineDto> updateOrDelete(@Valid @RequestBody BaselineDto view) {

        BaselineDto result = view.getValue() == null
                ? BaselineDto.builder().build()
                : baselineService.save(view);
        if (view.getValue() == null) {
            baselineService.delete(view);
        }

        return responseEntityTemplate.response("controller.baseline.put.successful.200", result);
    }
}
