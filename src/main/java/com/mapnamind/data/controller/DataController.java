package com.mapnamind.data.controller;

import com.mapnamind.data.dto.DataProjectionDTO;
import com.mapnamind.data.mapper.DataMapper;
import com.mapnamind.data.service.impl.DataService;
import com.mapnamind.data.view.DataValue;
import com.mapnamind.data.view.SignalSelectorView;
import com.mapnamind.data.view.SignalView;
import com.mapnamind.data.view.ValueView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (view.getSignals() != null && !view.getStringSignals().isEmpty()) {
            signalViews = Arrays.stream(view.getStringSignals().split(",")).map(s -> {
                SignalView signalView = new SignalView();
                signalView.setTopic(s.split(":")[0]);
                signalView.setIsProperty(s.split(":")[1]);
                return signalView;
            }).toList();
        } else {
            signalViews = view.getSignals();
        }

        for (SignalView signal : signalViews) {
            if (signal.getIsProperty().equals("0")) {
                signal.setIsProperty(null);
            } else if (signal.getIsProperty().equals("1")) {
                Pattern pattern = Pattern.compile("\"\\w+\"");
                Matcher matcher = pattern.matcher(signal.getTopic());
                if (matcher.find()) {
                    signal.setIsProperty(matcher.group().replaceAll("\"", ""));
                    signal.setTopic(signal.getTopic().replace("/{" + matcher.group() + "}", ""));
                }
            }
            List<DataProjectionDTO> dataList = dataService.findAllByTopicAndLogTime(signal.getTopic(), view.getStartDate(),
                    view.getEndDate(), signal.getIsProperty());

            if (dataList != null && !dataList.isEmpty()) {
                List<DataValue> dataValues = dataList.stream().map(data -> map(DataMapper.class).sourceToTarget(data)).toList();
                view.addValue(ValueView.builder().name(signal.getTopic()).data(dataValues).build());
            } else {
                view.addValue(ValueView.builder().name(signal.getTopic()).data(List.of()).build());
            }
        }
        return responseEntityTemplate.response("controller.data.signalSelector.successful.200", view);
    }

}
