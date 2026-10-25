package com.mapnadigital.insights.insights.data;

import com.mapnadigital.insights.insights.common.test.GenericTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(DataApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class DataApplicationTest extends GenericTest {

}
