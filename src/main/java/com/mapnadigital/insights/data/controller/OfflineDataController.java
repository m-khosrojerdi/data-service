package com.mapnadigital.insights.data.controller;

import com.mapnadigital.insights.data.documentation.OfflineDataDocumentation;
import com.mapnadigital.insights.data.service.IOfflineDataService;
import com.mapnadigital.insights.data.dto.offlineData.OfflineDataDto;
import com.mapnadigital.insights.web.Base;
import com.mapnadigital.insights.web.annotation.HasAnyRole;
import com.mapnadigital.insights.web.enumeration.Role;
import com.mapnadigital.insights.web.dto.ResponseBody;
import com.mapnadigital.insights.web.dto.ResponseEntityTemplate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * REST API of the offline data ingestion.
 * <p>
 * The endpoints are documented with {@code springdoc-openapi}; the documentation lives in
 * {@link OfflineDataDocumentation}.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/offline-data")
@RequiredArgsConstructor
public class OfflineDataController extends Base implements OfflineDataDocumentation {

    private final IOfflineDataService service;
    private final ResponseEntityTemplate responseEntityTemplate;

    @Override
    @PostMapping("/publish")
    @HasAnyRole({Role.ADMIN, Role.CUSTOMER, Role.MANUFACTURE, Role.AGENCY})
    public ResponseBody<?> publish(@Valid @RequestBody OfflineDataDto view) {
        service.save(view);
        return responseEntityTemplate.response("controller.offlineData.publish.successful.200");
    }

}
