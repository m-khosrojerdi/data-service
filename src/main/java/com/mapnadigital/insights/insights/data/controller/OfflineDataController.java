package com.mapnadigital.insights.insights.data.controller;

import com.mapnadigital.insights.insights.data.service.IOfflineDataService;
import com.mapnadigital.insights.insights.data.view.OfflineDataView;
import com.mapnadigital.insights.web.Base;
import com.mapnadigital.insights.web.annotation.HasAnyRole;
import com.mapnadigital.insights.web.enumeration.Role;
import com.mapnadigital.insights.web.view.ResponseBody;
import com.mapnadigital.insights.web.view.ResponseEntityTemplate;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.mapnadigital.insights.web.enumeration.Swagger.ACCESS_TOKEN;
import static com.mapnadigital.insights.web.enumeration.Swagger.LANGUAGE;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/offline-data")
@RequiredArgsConstructor
public class OfflineDataController extends Base {

    private final IOfflineDataService service;
    private final ResponseEntityTemplate responseEntityTemplate;

    @PostMapping("/publish")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<?> publish(@Valid @RequestBody OfflineDataView view) {
        service.save(view);
        return responseEntityTemplate.response("controller.offlineData.publish.successful.200");
    }

}
