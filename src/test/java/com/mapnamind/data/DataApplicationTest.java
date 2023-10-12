package com.mapnamind.data;

import com.mapnamind.common.test.GenericTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@AutoConfigureMockMvc
@SpringJUnitConfig(DataApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class DataApplicationTest extends GenericTest {

}
