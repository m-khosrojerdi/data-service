package com.mapnamind.data.controller;

import com.mapnamind.data.mapper.DataMapper;
import com.mapnamind.data.mapper.SignalMapper;
import com.mapnamind.data.service.impl.DataService;
import com.mapnamind.data.view.SignalSelectorView;
import com.mapnamind.data.view.SignalView;
import com.mapnamind.data.view.ValueView;
import com.mapnamind.web.Base;
import com.mapnamind.web.enumeration.AuthorizeType;
import com.mapnamind.web.view.ResponseBody;
import com.mapnamind.web.view.ResponseEntityTemplate;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static com.mapnamind.common.util.Constant.COLON;
import static com.mapnamind.common.util.Constant.COMMA;
import static com.mapnamind.web.enumeration.Swagger.ACCESS_TOKEN;
import static com.mapnamind.web.enumeration.Swagger.LANGUAGE;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/data")
@RequiredArgsConstructor
public class DataController extends Base {
    private final DataService dataService;
    private final ResponseEntityTemplate responseEntityTemplate;

    @PostMapping("/signalSelector")
    @PreAuthorize(AuthorizeType.CUSTOMER)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<?> signalSelector(@Valid @RequestBody SignalSelectorView view) {

        List<SignalView> signalViews;
        if (view.getStringSignals() != null && view.getSignals() == null) {
            signalViews = Arrays.stream(view.getStringSignals().split(COMMA)).map(s -> {
                SignalView signalView = new SignalView();
                signalView.setTopic(s.split(COLON)[0]);
                signalView.setIsProperty(s.split(COLON)[1]);
                return signalView;
            }).toList();
        } else {
            signalViews = view.getSignals();
        }

        for (SignalView signalView : signalViews) {
            view.addValue(ValueView.builder()
                    .name(signalView.getTopic())
                    .data(dataService.findAllByTopicAndLogTime(view.getStartDate(), view.getEndDate(), map(SignalMapper.class).targetToSource(signalView))
                            .stream().map(data -> map(DataMapper.class).sourceToTarget(data)).toList())
                    .build());
        }
        return responseEntityTemplate.response("controller.data.signalSelector.successful.200", view);
    }

    @Hidden
    @PostMapping("/signalSelector/export")
    @PreAuthorize(AuthorizeType.CUSTOMER)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<?> signalSelectorExport(@Valid @RequestBody SignalSelectorView view) {
        return null;
    }
}
