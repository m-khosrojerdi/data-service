package com.mapnamind.data.controller;

import com.mapnamind.common.api.ApiService;
import com.mapnamind.common.dto.data.DataDTO;
import com.mapnamind.common.enumeration.ServiceName;
import com.mapnamind.common.entity.Data;
import com.mapnamind.data.view.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/data")
@RequiredArgsConstructor
public class DataController {
    private final ApiService apiService;
    private final ModelMapper modelMapper;
    private static final String DOWNLOAD_URL = "/download/";

    @PostMapping("/signalSelector")
    public SignalSelectorView signalSelector(@RequestBody SignalSelectorView view) {
        for (SignalView signal : view.getSignals()) {
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
            List<Data> dataList = apiService.callCollection(ServiceName.DATA_FIND_ALL_BY_TOPIC_AND_START_AND_END_OF_DATE_TIME,
                    DataDTO.builder()
                            .topic(signal.getTopic())
                            .startDate(view.getStartDate())
                            .endDate(view.getEndDate())
                            .isProperty(signal.getIsProperty())
                            .build(),
                    Data[].class
            );
            if (dataList != null) {
                List<DataValue> dataValues = dataList.stream().map(data -> modelMapper.typeMap(Data.class, DataValue.class)
                        .addMapping(Data::getDatetime, DataValue::setX)
                        .addMapping(Data::getValue, DataValue::setY)
                        .map(data)).toList();
                ValueView valueView = new ValueView();
                valueView.setName(signal.getTopic());
                valueView.setData(dataValues);
                view.addValue(valueView);
            }
        }
        return view;
    }

    @PostMapping("/history")
    public DataView history(@RequestBody DataHistoryView view) {
        if (view.getPageSize() <= 0 && view.getPageNumber() <= 0) {
            return null;
        }
        List<Data> values = apiService.callCollection(ServiceName.DATA_FIND_ALL_BY_TOPIC_AND_START_AND_END_OF_DATE_TIME_AND_PAGE_SIZE_AND_PAGE_NUMBER,
                DataDTO.builder()
                        .topic(view.getTopic())
                        .startDate(view.getStartDate())
                        .endDate(view.getEndDate())
                        .isProperty(view.getIsProperty())
                        .pageSize(view.getPageSize())
                        .pageNumber(view.getPageNumber())
                        .build(),
                Data[].class
        );
        Integer total = apiService.call(ServiceName.DATA_TOTAL_RECORD,
                DataDTO.builder()
                        .topic(view.getTopic())
                        .startDate(view.getStartDate())
                        .endDate(view.getEndDate())
                        .isProperty(view.getIsProperty())
                        .build(),
                Integer.class
        );
        DataView response = new DataView();
        response.setValues(values);
        response.setTotal(total);
        return response;
    }

    @PostMapping("/export")
    public FileDownloadView export(@RequestBody SignalSelectorView view) {
        FileDownloadView fileDownloads = new FileDownloadView();
        String fileName = null;
        for (SignalView signal : view.getSignals()) {
            String mindTopic = signal.getTopic();
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
            fileName = apiService.call(ServiceName.DATA_EXPORT,
                    DataDTO.builder()
                            .topic(signal.getTopic())
                            .startDate(view.getStartDate())
                            .endDate(view.getEndDate())
                            .isProperty(signal.getIsProperty())
                            .build(),
                    String.class
            );
            if (fileName != null) {
                FileDownloadView.DownloadView downloadView = new FileDownloadView.DownloadView();
                downloadView.setTopic(mindTopic);
                downloadView.setDownloadPath(ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/data" + DOWNLOAD_URL)
                        .path(fileName)
                        .toUriString());
                fileDownloads.setDownloads(downloadView);
            }
        }
        return fileDownloads;
    }

    @GetMapping(DOWNLOAD_URL + "{fileName}")
    public ResponseEntity<Resource> download(@PathVariable("fileName") String fileName) {
        Resource resource = apiService.call(ServiceName.DATA_FIND_FILE_RESOURCE,
                DataDTO.builder().fileName(fileName).build(), Resource.class);
        if (resource != null) {
            try {
                String path = resource.getFile().getAbsolutePath();
                return ResponseEntity.ok()
                        .contentType(contentType(path))
                        .body(resource);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public MediaType contentType(String path) {
        String contentType;
        contentType = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest().getServletContext().getMimeType(path);
        if (contentType == null) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        return MediaType.parseMediaType(contentType);
    }

}
