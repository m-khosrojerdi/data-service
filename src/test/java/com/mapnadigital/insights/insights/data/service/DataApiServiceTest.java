package com.mapnadigital.insights.insights.data.service;

import com.mapnadigital.insights.web.api.ApiService;
/*import com.mapnadigital.common.entity.Data;*/
import com.mapnadigital.insights.insights.data.DataApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class DataApiServiceTest extends DataApplicationTest {

    @Autowired
    private ApiService apiService;

    @Test
    public void history() {
        /*Calendar cal = Calendar.getInstance();
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
        );*/
    }

    @Test
    public void whenAppendStringUsingBufferedWriter_thenOldContentShouldExistToo()
            throws IOException {
        /*String str = "Hillo";
        File file = new File("/home/m-khosrojerdi/Desktop/write/hhh/ddd/eeee");
        if (!file.exists()) {
            file.mkdirs();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath() + "/hello.txt"));
        writer.close();
        */
    }

}
