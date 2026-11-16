package com.mapnadigital.insights.data.controller;

import com.google.gson.reflect.TypeToken;
import com.mapnadigital.insights.common.util.DateTimeUtil;
import com.mapnadigital.insights.data.api.ApiClient;
import com.mapnadigital.insights.data.documentation.DataDocumentation;
import com.mapnadigital.insights.data.dto.data.ExportRequestDto;
import com.mapnadigital.insights.data.dto.data.SignalDto;
import com.mapnadigital.insights.data.dto.data.SignalSelectorDto;
import com.mapnadigital.insights.data.dto.data.ValueDto;
import com.mapnadigital.insights.data.dto.device.DeviceDto;
import com.mapnadigital.insights.data.dto.topicTree.TopicTreeDto;
import com.mapnadigital.insights.data.service.impl.BaselineService;
import com.mapnadigital.insights.data.service.impl.DataService;
import com.mapnadigital.insights.web.Base;
import com.mapnadigital.insights.web.annotation.HasAnyRole;
import com.mapnadigital.insights.web.api.ApiService;
import com.mapnadigital.insights.web.dto.ResponseBody;
import com.mapnadigital.insights.web.dto.ResponseEntityTemplate;
import com.mapnadigital.insights.web.enumeration.Role;
import com.mapnadigital.insights.web.util.HttpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static com.mapnadigital.insights.common.util.Constant.*;

/**
 * REST API of the telemetry values.
 * <p>
 * The endpoints are documented with {@code springdoc-openapi}; the documentation lives in
 * {@link DataDocumentation}.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/data")
@RequiredArgsConstructor
public class DataController extends Base implements DataDocumentation {
    private final ApiService apiService;
    private final DataService dataService;
    private final BaselineService baselineService;
    private final ResponseEntityTemplate responseEntityTemplate;

    @Override
    @PostMapping({"/signal-selector", "get-data"})
    @HasAnyRole(Role.CUSTOMER)
    public ResponseBody<?> signalSelector(@Valid @RequestBody SignalSelectorDto view) {

        List<SignalDto> signalViews;
        if (view.getStringSignals() != null && view.getSignals() == null) {
            signalViews = Arrays.stream(view.getStringSignals().split(COMMA)).map(s -> {
                SignalDto signalView = new SignalDto();
                signalView.setTopic(s.split(COLON)[0]);
                signalView.setIsProperty(s.split(COLON)[1]);
                return signalView;
            }).toList();
        } else {
            signalViews = view.getSignals();
        }

        for (SignalDto signalView : signalViews) {
            Duration inputDuration = DateTimeUtil.rondToMinutes(DateTimeUtil.postgresIntervalToDuration(signalView.getInterval()));
            Duration computedDuration = DateTimeUtil.rondToMinutes(Duration.between(DateTimeUtil.toDateTime(view.getStartDate()), DateTimeUtil.toDateTime(view.getEndDate())).dividedBy(signalView.getSampleCount()));
            String effectiveInterval = DateTimeUtil.durationToPostgresInterval(inputDuration.compareTo(computedDuration) > 0 ? inputDuration : computedDuration);
            String topic = HttpRequest.getUri().endsWith("get-data") ? getTopic(signalView.getParam(), signalView.getSerial()) : signalView.getTopic();
            TopicTreeDto topicTree = dataService.getTopicTree(topic);
            view.addValue(ValueDto.builder()
                    .name(signalView.getTopic())
                    .effectiveInterval(effectiveInterval)
                    .data(dataService.aggregate(topic, view.getStartDate(), view.getEndDate(), effectiveInterval, signalView))
                    .unit(topicTree.getUnit())
                    .baselineValue(baselineService.getValue(topic, signalView.getInterval()))
                    .param(signalView.getParam())
                    .serial(signalView.getSerial())
                    .build());
        }
        return responseEntityTemplate.response("controller.data.signalSelector.successful.200", view);
    }

    private String getTopic(String param, String serial) {

        DeviceDto deviceView = apiService.get(ApiClient.DEVICE_FIND_BY_SERIAL,
                List.of(serial), new TypeToken<DeviceDto>() {
                }.getType());

        return String.join(SLASH, deviceView.getDeviceTypeName(), serial, "data", param);
    }

    @Override
    @PostMapping("/signal-selector/export")
    @HasAnyRole(Role.CUSTOMER)
    public ResponseBody<SignalSelectorDto> signalSelectorExport(@Valid @RequestBody SignalSelectorDto view) {

        view.setExportUuid(dataService.export(ExportRequestDto.builder().startDate(view.getStartDate())
                .endDate(view.getEndDate()).signals(view.getSignals())
                .isPersian(LocaleContextHolder.getLocale().equals(FA_LOCALE)).build()));

        return responseEntityTemplate.response("controller.data.export.successful.200", view);
    }
}
