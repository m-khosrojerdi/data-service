package com.mapnadigital.insights.data;

import com.mapnadigital.insights.web.EnterpriseApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@EntityScan(basePackages = "com.mapnadigital.insights.data.model.entity")
public class DataApplication extends EnterpriseApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataApplication.class, args);
    }

}
