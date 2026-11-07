package com.mapnadigital.insights.data;

import com.mapnadigital.insights.common.test.GenericTest;
import com.mapnadigital.insights.data.DataApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(DataApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class DataApplicationTest extends GenericTest {

}
