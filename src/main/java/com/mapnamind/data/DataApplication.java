package com.mapnamind.data;

import com.mapnamind.common.file.FileManagerConfig;
import com.mapnamind.web.EnterpriseApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@RequiredArgsConstructor
public class DataApplication extends EnterpriseApplication {

    private final Environment env;

    public static void main(String[] args) {
        SpringApplication.run(DataApplication.class, args);
    }

    @Bean
    public FileManagerConfig fileManagerConfig() {
        return FileManagerConfig.builder()
                .baseUrl(env.getProperty("file-manager.url"))
                .uploadUrl(env.getProperty("file-manager.service.upload"))
                .downloadUrl(env.getProperty("file-manager.service.download"))
                .appName(env.getProperty("spring.application.name"))
                .build();
    }


}
