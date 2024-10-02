package com.mapnamind.data.service;

import com.mapnamind.common.annotation.Api;
import com.mapnamind.common.annotation.RestService;
import com.mapnamind.common.dto.data.DataDTO;
import com.mapnamind.common.model.Data;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.stream.Stream;

public interface DataService {

    Boolean save(DataDTO dto);

    List<Data> findAllByTopicAndStartAndEndOfDatetime(DataDTO dto);

    List<Data> findAllByTopicAndStartAndEndOfDatetimeAndPageSizeAndPgeNumber(DataDTO dto);

    Integer totalRecord(DataDTO dto);

    String export(DataDTO dto);

    Resource findFileResource(DataDTO dto);

}
