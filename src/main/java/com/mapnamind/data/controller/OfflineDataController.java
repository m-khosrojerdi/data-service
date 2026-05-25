package com.mapnamind.data.controller;

import com.mapnamind.data.mapper.OfflineDataMapper;
import com.mapnamind.data.service.IOfflineDataService;
import com.mapnamind.data.view.OfflineDataView;
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
@RequestMapping("/offline-data")
@RequiredArgsConstructor
public class OfflineDataController extends Base {

    private final IOfflineDataService service;
    private final ResponseEntityTemplate responseEntityTemplate;

    @GetMapping("/publish")
    @PreAuthorize(AuthorizeType.ADMIN_CUSTOMER_MANUFACTURE_AGENCY)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<?> publish(@Valid @RequestBody OfflineDataView view) {
        service.save(map(OfflineDataMapper.class).targetToSource(view));
        return responseEntityTemplate.response("controller.offlineData.publish.successful.200");
    }

}
