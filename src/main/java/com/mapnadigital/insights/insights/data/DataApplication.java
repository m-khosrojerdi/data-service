package com.mapnadigital.insights.insights.data;

import com.mapnadigital.insights.web.EnterpriseApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public class DataApplication extends EnterpriseApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataApplication.class, args);
    }

}
