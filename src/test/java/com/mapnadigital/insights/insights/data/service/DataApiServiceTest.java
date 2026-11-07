package com.mapnadigital.insights.data.service;

import com.mapnadigital.insights.web.api.ApiService;
/*import com.mapnadigital.common.entity.Data;*/
import com.mapnadigital.insights.data.DataApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class DataApiServiceTest extends DataApplicationTest {

    @Autowired
    private ApiService apiService;

    @Test
    public void history() {

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
