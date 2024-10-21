package com.mapnamind.data.controller;

import com.google.gson.Gson;
import com.mapnamind.data.service.impl.DataService;
import com.mapnamind.data.view.SignalSelectorView;
import com.mapnamind.web.Base;
import com.mapnamind.web.enumeration.AuthorizeType;
import com.mapnamind.web.view.ResponseBody;
import com.mapnamind.web.view.ResponseEntityTemplate;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.mapnamind.web.enumeration.Swagger.ACCESS_TOKEN;
import static com.mapnamind.web.enumeration.Swagger.LANGUAGE;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/data")
@RequiredArgsConstructor
public class DataController extends Base {
    private final DataService dataService;
    private final ResponseEntityTemplate responseEntityTemplate;
    private final Gson gson;

    @PostMapping("/signalSelector")
    @PreAuthorize(AuthorizeType.CUSTOMER)
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    public ResponseBody<?> signalSelector(@RequestBody SignalSelectorView view) {
//        ValueView valueView;

        /*for (SignalView signal : view.getSignals()) {
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
            List<Data> dataList = dataService.findAllByTopicAndLogTime(signal.getTopic(), view.getStartDate(),
                    view.getEndDate(), signal.getIsProperty());

            if (dataList != null) {
                List<DataValue> dataValues = dataList.stream().map(data -> map(DataMapper.class).sourceToTarget(data)).toList();
                ValueView valueView = new ValueView();
                valueView.setName(signal.getTopic());
                valueView.setData(dataValues);
                view.addValue(valueView);
            }
        }*/
        String json = "{\n" +
                "    \"signals\": [\n" +
                "        {\n" +
                "            \"topic\": \"sou/6055f900be60/data/plug\",\n" +
                "            \"isProperty\": \"voltage\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"values\": [\n" +
                "        {\n" +
                "            \"name\": \"sou/6055f900be60/data/plug\",\n" +
                "            \"data\": [\n" +
                "                {\n" +
                "                    \"x\": 1728901352855,\n" +
                "                    \"y\": 250.54\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901352855,\n" +
                "                    \"y\": 250.54\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901352855,\n" +
                "                    \"y\": 250.54\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901361639,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901361705,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901361716,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901364861,\n" +
                "                    \"y\": 250.54\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901364861,\n" +
                "                    \"y\": 250.54\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901364862,\n" +
                "                    \"y\": 250.54\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901368864,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901368864,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901368865,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901372867,\n" +
                "                    \"y\": 250.54\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901372867,\n" +
                "                    \"y\": 250.54\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901372867,\n" +
                "                    \"y\": 250.54\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901376900,\n" +
                "                    \"y\": 246.3\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901376966,\n" +
                "                    \"y\": 246.3\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901376981,\n" +
                "                    \"y\": 246.3\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901381912,\n" +
                "                    \"y\": 249.46\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901381941,\n" +
                "                    \"y\": 249.46\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901381979,\n" +
                "                    \"y\": 249.46\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901384870,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901384871,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901384871,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901388871,\n" +
                "                    \"y\": 250.54\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901388871,\n" +
                "                    \"y\": 250.54\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901388872,\n" +
                "                    \"y\": 250.54\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901392877,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901392877,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901392877,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901397330,\n" +
                "                    \"y\": 248.93\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901397466,\n" +
                "                    \"y\": 248.93\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901397474,\n" +
                "                    \"y\": 248.93\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901400881,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901400881,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901400882,\n" +
                "                    \"y\": 247.87\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901404883,\n" +
                "                    \"y\": 252.16\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901404883,\n" +
                "                    \"y\": 252.16\n" +
                "                },\n" +
                "                {\n" +
                "                    \"x\": 1728901404883,\n" +
                "                    \"y\": 252.16\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"startDate\": \"2024/10/14-10:22:27\",\n" +
                "    \"endDate\": \"2024/10/14-10:23:27\"\n" +
                "}\n" +
                "\n";
        return responseEntityTemplate.response("controller.data.signalSelector.successful.200", gson.fromJson(json, SignalSelectorView.class));
    }

}
