package com.mapnamind.data.service.impl;

import com.mapnamind.common.dto.data.DataDTO;
import com.mapnamind.common.model.Data;
import com.mapnamind.common.util.Toolbox;
import com.mapnamind.data.repository.DataRepository;
import com.mapnamind.data.service.DataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.mapnamind.common.util.Constant.DATE_FORMATTING;
import static com.mapnamind.common.util.Constant.UI_DATE_FORMATTING;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataServiceImpl implements DataService {

    private static final String CSV_PATH_KEY = "report.export.data.csv.path";
    private final Environment env;
    private final DataRepository repository;
    private File csvDirectory = null;

    @Override
    public List<Data> findAllByTopicAndStartAndEndOfDatetimeAndPageSizeAndPgeNumber(DataDTO dto) {
        if (dto.getIsProperty().equals("0") || dto.getIsProperty().equals("0.0")) {
            dto.setIsProperty(null);
        } else if (dto.getIsProperty().equals("1") || dto.getIsProperty().equals("1.0")) {
            Pattern pattern = Pattern.compile("\"\\w+\"");
            Matcher matcher = pattern.matcher(dto.getTopic());
            if (matcher.find()) {
                dto.setIsProperty(matcher.group().replaceAll("\"", ""));
                dto.setTopic(dto.getTopic().replace("/{" + matcher.group() + "}", ""));
            }
        }
        List<Data> values = repository.findAllByTopicAndStartAndEndOfDatetimeWithEpoch(dto.getTopic(),
                Toolbox.convertStringToDate(UI_DATE_FORMATTING, dto.getStartDate()),
                Toolbox.convertStringToDate(UI_DATE_FORMATTING, dto.getEndDate()),
                dto.getIsProperty(), dto.getPageSize(), dto.getPageNumber());
        if (values != null && !values.isEmpty()) {
            for (Data value : values) {
                value.setDatetime(Toolbox.convertDateFormat(DATE_FORMATTING, UI_DATE_FORMATTING, value.getDatetime()));
            }
        }
        return values;
    }

    @Override
    public Boolean save(DataDTO dto) {
        return repository.save(
                Data.builder()
                        .topic(dto.getTopic())
                        .value(dto.getValue())
                        .datetime(dto.getDatetime())
                        .build()
        );
    }

    @Override
    public List<Data> findAllByTopicAndStartAndEndOfDatetime(DataDTO dto) {
        return repository.findAllByTopicAndStartAndEndOfDatetimeWithEpoch(dto.getTopic(),
                Toolbox.convertStringToDate(UI_DATE_FORMATTING, dto.getStartDate()),
                Toolbox.convertStringToDate(UI_DATE_FORMATTING, dto.getEndDate()),
                dto.getIsProperty());
    }

    @Override
    public Integer totalRecord(DataDTO dto) {
        if (dto.getIsProperty().equals("0") || dto.getIsProperty().equals("0.0")) {
            dto.setIsProperty(null);
        } else if (dto.getIsProperty().equals("1") || dto.getIsProperty().equals("1.0")) {
            Pattern pattern = Pattern.compile("\"\\w+\"");
            Matcher matcher = pattern.matcher(dto.getTopic());
            if (matcher.find()) {
                dto.setIsProperty(matcher.group().replaceAll("\"", ""));
                dto.setTopic(dto.getTopic().replace("/{" + matcher.group() + "}", ""));
            }
        }
        return repository.totalRecord(dto.getTopic(),
                Toolbox.convertStringToDate(UI_DATE_FORMATTING, dto.getStartDate()),
                Toolbox.convertStringToDate(UI_DATE_FORMATTING, dto.getEndDate()),
                dto.getIsProperty());
    }

    @Override
    public String export(DataDTO dto) {
        List<Data> values = repository.findAllByTopicAndStartAndEndOfDatetimeWithoutEpoch(dto.getTopic(),
                Toolbox.convertStringToDate(UI_DATE_FORMATTING, dto.getStartDate()),
                Toolbox.convertStringToDate(UI_DATE_FORMATTING, dto.getEndDate()),
                dto.getIsProperty());
        if (values != null && !values.isEmpty()) {
            if (csvDirectory == null) {
                csvDirectory = Toolbox.mkdir(env.getProperty(CSV_PATH_KEY));
            }
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + ".csv";
            try {
                FileWriter writer = new FileWriter(env.getProperty(CSV_PATH_KEY) + fileName);
                writer.append("Date");
                writer.append(',');
                writer.append("Value");
                writer.append('\n');
                for (Data data : values) {
                    writer.append(data.getDatetime());
                    writer.append(',');
                    writer.append(data.getValue().toString());
                    writer.append('\n');
                }

                writer.flush();
                writer.close();
                return fileName;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public Resource findFileResource(DataDTO dto) {
        try {
            File file = new File(env.getProperty(CSV_PATH_KEY) + dto.getFileName());
            Resource resource = new UrlResource(file.toURI());
            if (resource.exists()) {
                return resource;
            }
        } catch (MalformedURLException e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }
}
