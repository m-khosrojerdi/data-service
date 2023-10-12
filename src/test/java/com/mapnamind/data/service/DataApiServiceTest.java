package com.mapnamind.data.service;

import com.mapnamind.common.api.ApiService;
import com.mapnamind.common.dto.data.DataDTO;
import com.mapnamind.common.enumeration.ServiceName;
import com.mapnamind.common.model.Data;
import com.mapnamind.data.DataApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class DataApiServiceTest extends DataApplicationTest {

    @Autowired
    private ApiService apiService;

    @Test
    public void history() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -120);
        print(apiService.callCollection(ServiceName.DATA_FIND_ALL_BY_TOPIC_AND_START_AND_END_OF_DATE_TIME_AND_PAGE_SIZE_AND_PAGE_NUMBER,
                        DataDTO.builder()
                                .topic("ev/data/spn/{\"119_4\"}")
                                .startDate(cal.getTime().toString())
                                .endDate(Instant.now().toString())
                                .isProperty("1")
                                .pageSize(5)
                                .pageNumber(2)
                                .build(),
                        Data[].class
                )
        );
    }

    @Test
    public void whenAppendStringUsingBufferedWriter_thenOldContentShouldExistToo()
            throws IOException {
        String str = "Hillo";
        File file = new File("/home/m-khosrojerdi/Desktop/write/hhh/ddd/eeee");
        if (!file.exists()) {
            file.mkdirs();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath() + "/hello.txt"));
        writer.close();
    }

}
