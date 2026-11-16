package com.mapnadigital.insights.data.controller;

import com.mapnadigital.insights.data.documentation.BaselineDocumentation;
import com.mapnadigital.insights.data.dto.baseline.BaselineDto;
import com.mapnadigital.insights.data.service.impl.BaselineService;
import com.mapnadigital.insights.web.Base;
import com.mapnadigital.insights.web.annotation.HasAnyRole;
import com.mapnadigital.insights.web.enumeration.Role;
import com.mapnadigital.insights.web.dto.ResponseBody;
import com.mapnadigital.insights.web.dto.ResponseEntityTemplate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * REST API of the signal baselines.
 * <p>
 * The endpoints are documented with {@code springdoc-openapi}; the documentation lives in
 * {@link BaselineDocumentation}.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/baseline")
@RequiredArgsConstructor
public class BaselineController extends Base implements BaselineDocumentation {
    private final BaselineService baselineService;
    private final ResponseEntityTemplate responseEntityTemplate;


    @Override
    @PostMapping
    @HasAnyRole(Role.CUSTOMER)
    public ResponseBody<BaselineDto> get(@Valid @RequestBody BaselineDto view) {
        return responseEntityTemplate.response("controller.baseline.get.successful.200",
                baselineService.get(view.getTopic(), view.getInterval()));
    }

    @Override
    @PutMapping
    @HasAnyRole(Role.CUSTOMER)
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
