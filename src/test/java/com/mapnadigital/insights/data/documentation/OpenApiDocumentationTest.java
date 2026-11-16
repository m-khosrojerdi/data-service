package com.mapnadigital.insights.data.documentation;

import com.mapnadigital.insights.common.configuration.JwtConfig;
import com.mapnadigital.insights.data.controller.DataController;
import com.mapnadigital.insights.data.service.IOfflineDataService;
import com.mapnadigital.insights.data.service.ITopicTreeService;
import com.mapnadigital.insights.data.service.impl.BaselineService;
import com.mapnadigital.insights.data.service.impl.DataService;
import com.mapnadigital.insights.web.api.ApiService;
import com.mapnadigital.insights.web.configuration.SwaggerConfig;
import com.mapnadigital.insights.web.dto.ResponseEntityTemplate;
import org.junit.jupiter.api.Test;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies that {@code /v3/api-docs} is generated for every data controller.
 * <p>
 * The test boots a plain MVC context with the four controllers and the springdoc configuration; the services are
 * mocked, so no database, config server or broker is needed.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
class OpenApiDocumentationTest {

    @Test
    void apiDocsAreGeneratedForEveryController() throws Exception {
        try (AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new MockServletContext());
            context.register(TestConfiguration.class,
                    SwaggerConfig.class,
                    SpringDocConfiguration.class,
                    SpringDocWebMvcConfiguration.class);
            context.refresh();

            MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

            mockMvc.perform(get("/v3/api-docs"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.info").exists())
                    .andExpect(jsonPath("$.paths['/data/signal-selector']").exists())
                    .andExpect(jsonPath("$.paths['/data/get-data']").exists())
                    .andExpect(jsonPath("$.paths['/data/signal-selector/export']").exists())
                    .andExpect(jsonPath("$.paths['/baseline']").exists())
                    .andExpect(jsonPath("$.paths['/offline-data/publish']").exists())
                    .andExpect(jsonPath("$.paths['/topic-tree/root-tree']").exists())
                    .andExpect(jsonPath("$.paths['/topic-tree/children-tree/{parentId}']").exists())
                    .andExpect(jsonPath("$.paths['/topic-tree/search']").exists())
                    .andExpect(jsonPath("$.paths['/topic-tree/get']").exists())
                    .andExpect(jsonPath("$.paths['/topic-tree/get/{id}']").exists())
                    .andExpect(jsonPath("$.paths['/topic-tree/get-all-by-ids']").exists())
                    .andExpect(jsonPath("$.paths['/topic-tree/root/get-all']").exists())
                    .andExpect(jsonPath("$.paths['/topic-tree/update']").exists())
                    .andExpect(jsonPath("$.paths['/topic-tree/date']").exists())
                    // The documentation is declared on the documentation interfaces and must be resolved by springdoc.
                    .andExpect(jsonPath("$.paths['/data/signal-selector'].post.summary")
                            .value("Read the aggregated values of the selected signals"))
                    .andExpect(jsonPath("$.paths['/data/signal-selector'].post.tags[0]").value("Telemetry data"))
                    .andExpect(jsonPath("$.paths['/data/signal-selector'].post.security").isArray())
                    .andExpect(jsonPath("$.paths['/data/signal-selector'].post.requestBody.description").exists())
                    .andExpect(jsonPath("$.paths['/data/signal-selector'].post.requestBody"
                            + ".content['application/json'].examples['Signals by topic']").exists())
                    .andExpect(jsonPath("$.paths['/data/signal-selector'].post.responses['200'].description")
                            .exists())
                    .andExpect(jsonPath("$.paths['/baseline'].post.summary").value("Read the baseline of a signal"))
                    .andExpect(jsonPath("$.paths['/baseline'].post.requestBody.required").value(true))
                    .andExpect(jsonPath("$.paths['/offline-data/publish'].post.summary")
                            .value("Publish a sample collected while the client was offline"))
                    .andExpect(jsonPath("$.paths['/topic-tree/search'].get.parameters[0].name").value("deviceName"))
                    .andExpect(jsonPath("$.paths['/topic-tree/search'].get.parameters[0].description")
                            .value("Part of the nick name or of the device type name"))
                    .andExpect(jsonPath("$.paths['/topic-tree/children-tree/{parentId}'].get.parameters[0].in")
                            .value("path"))
                    .andExpect(jsonPath("$.paths['/topic-tree/get-all-by-ids'].get.parameters[0].example")
                            .value("31,32"))
                    .andExpect(jsonPath("$.paths['/topic-tree/update'].put.requestBody.description").exists())
                    .andExpect(jsonPath("$.paths['/topic-tree/date'].get.responses['200']"
                            + ".content['application/json'].schema['$ref']").value("#/components/schemas/Date"))
                    .andExpect(jsonPath("$.components.securitySchemes['JWT Token']").exists())
                    .andExpect(jsonPath("$.components.securitySchemes['Language']").exists())
                    .andExpect(jsonPath("$.components.securitySchemes['sys']").exists())
                    .andExpect(jsonPath("$.tags").isArray());
        }
    }

    @Configuration
    @EnableWebMvc
    @EnableConfigurationProperties(SpringDocConfigProperties.class)
    @ComponentScan(basePackageClasses = DataController.class)
    static class TestConfiguration {

        @Bean
        ApiService apiService() {
            return mock(ApiService.class);
        }

        @Bean
        DataService dataService() {
            return mock(DataService.class);
        }

        @Bean
        BaselineService baselineService() {
            return mock(BaselineService.class);
        }

        @Bean
        ITopicTreeService topicTreeService() {
            return mock(ITopicTreeService.class);
        }

        @Bean
        IOfflineDataService offlineDataService() {
            return mock(IOfflineDataService.class);
        }

        @Bean
        ResponseEntityTemplate responseEntityTemplate(MessageSource messageSource) {
            return new ResponseEntityTemplate(messageSource);
        }

        @Bean
        JwtConfig jwtConfig() {
            return new JwtConfig(
                    new JwtConfig.Name("sys-key", "sys-key-ref", "Sec-Websocket-Protocol", "sys-captcha",
                            "tcaptcha", "totp", "sys-msg"),
                    new JwtConfig.ExpirationMs(3600000L, 7776000000L, 120000L, 15L));
        }
    }
}

