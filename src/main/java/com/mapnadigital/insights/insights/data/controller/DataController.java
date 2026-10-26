package com.mapnadigital.insights.insights.data.controller;

import com.google.gson.reflect.TypeToken;
import com.mapnadigital.insights.insights.common.model.entity.TopicTree;
import com.mapnadigital.insights.insights.common.util.DateTimeUtil;
import com.mapnadigital.insights.insights.common.view.device.DeviceView;
import com.mapnadigital.insights.insights.data.dto.ExportRequest;
import com.mapnadigital.insights.insights.data.mapper.DataMapper;
import com.mapnadigital.insights.insights.data.mapper.SignalMapper;
import com.mapnadigital.insights.insights.data.service.impl.BaselineService;
import com.mapnadigital.insights.insights.data.service.impl.DataService;
import com.mapnadigital.insights.insights.data.view.SignalSelectorView;
import com.mapnadigital.insights.insights.data.view.SignalView;
import com.mapnadigital.insights.insights.data.view.ValueView;
import com.mapnadigital.insights.web.Base;
import com.mapnadigital.insights.web.annotation.HasAnyRole;
import com.mapnadigital.insights.web.api.ApiService;
import com.mapnadigital.insights.web.enumeration.Role;
import com.mapnadigital.insights.web.enumeration.ServiceName;
import com.mapnadigital.insights.web.util.HttpRequest;
import com.mapnadigital.insights.web.view.ResponseBody;
import com.mapnadigital.insights.web.view.ResponseEntityTemplate;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mapnadigital.insights.insights.common.util.Constant.*;
import static com.mapnadigital.insights.web.enumeration.Swagger.ACCESS_TOKEN;
import static com.mapnadigital.insights.web.enumeration.Swagger.LANGUAGE;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/data")
@RequiredArgsConstructor
public class DataController extends Base {
    private final ApiService apiService;
    private final DataService dataService;
    private final BaselineService baselineService;
    private final ResponseEntityTemplate responseEntityTemplate;

    @PostMapping({"/signal-selector", "get-data"})
    @HasAnyRole(Role.CUSTOMER)
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
            Duration inputDuration = DateTimeUtil.rondToMinutes(DateTimeUtil.postgresIntervalToDuration(signalView.getInterval()));
            Duration computedDuration = DateTimeUtil.rondToMinutes(Duration.between(DateTimeUtil.toDateTime(view.getStartDate()), DateTimeUtil.toDateTime(view.getEndDate())).dividedBy(signalView.getSampleCount()));
            String effectiveInterval = DateTimeUtil.durationToPostgresInterval(inputDuration.compareTo(computedDuration) > 0 ? inputDuration : computedDuration);
            String topic = HttpRequest.getUri().endsWith("get-data") ? getTopic(signalView.getParam(), signalView.getSerial()) : signalView.getTopic();
            TopicTree topicTree = dataService.getTopicTree(topic);
            view.addValue(ValueView.builder()
                .name(signalView.getTopic())
                .effectiveInterval(effectiveInterval)
                .data(dataService.aggregate(topic, view.getStartDate(), view.getEndDate(), effectiveInterval, map(SignalMapper.class).targetToSource(signalView))
                    .stream().map(data -> map(DataMapper.class).sourceToTarget(data)).toList())
                .unit(topicTree.getUnit())
                .baselineValue(baselineService.getValue(topic, signalView.getInterval()))
                .param(signalView.getParam())
                .serial(signalView.getSerial())
                .build());
        }
        return responseEntityTemplate.response("controller.data.signalSelector.successful.200", view);
    }

    private String getTopic(String param, String serial) {

        DeviceView deviceView = apiService.call(ServiceName.DEVICE_FIND_BY_SERIAL, HttpMethod.GET, null,
            List.of(serial), null,
            new TypeToken<DeviceView>() {
            }.getType());

        return Stream.of(deviceView.getDeviceTypeName(), serial, "data", param).collect(Collectors.joining("/"));
    }

    @PostMapping("/signal-selector/export")
    @HasAnyRole(Role.CUSTOMER)
    @SecurityRequirements(value = {
        @SecurityRequirement(name = ACCESS_TOKEN),
        @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<SignalSelectorView> signalSelectorExport(@Valid @RequestBody SignalSelectorView view) {

        view.setExportUuid(dataService.export(ExportRequest.builder().startDate(view.getStartDate())
            .endDate(view.getEndDate()).signalDTOs(map(SignalMapper.class).targetsToSources(view.getSignals()))
            .isPersian(LocaleContextHolder.getLocale().equals(FA_LOCALE)).build()));

        return responseEntityTemplate.response("controller.data.export.successful.200", view);
    }
}
