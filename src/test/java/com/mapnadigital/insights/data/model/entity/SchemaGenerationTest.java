package com.mapnadigital.insights.data.model.entity;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the entity mappings of this module generate the complete {@code data_history} schema.
 * <p>
 * The DDL is exported for the PostgreSQL dialect without a database connection, so the test documents and guards
 * the tables, columns, primary keys, foreign keys, unique constraints, indexes and sequences the module owns. It
 * also guards the order of the statements: every sequence must be created before the table whose {@code id}
 * column default references it, otherwise PostgreSQL rejects the {@code CREATE TABLE}.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
class SchemaGenerationTest {

    @Test
    void schemaIsGeneratedUnderTheDataHistorySchema(@TempDir Path tempDir) throws IOException {
        Path script = tempDir.resolve("schema.sql");

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .build();

        try {
            Metadata metadata = new MetadataSources(registry)
                    .addAnnotatedClass(TopicTree.class)
                    .addAnnotatedClass(Baseline.class)
                    .addAnnotatedClass(Data.class)
                    .buildMetadata();

            SchemaManagementToolCoordinator.process(metadata, registry, Map.of(
                    "jakarta.persistence.schema-generation.scripts.action", "create",
                    "jakarta.persistence.schema-generation.scripts.create-target", script.toString(),
                    "hibernate.hbm2ddl.delimiter", ";",
                    "hibernate.hbm2ddl.create_namespaces", "true"
            ), null);

            String ddl = Files.readString(script);

            assertThat(ddl)
                    .contains("create schema data_history")
                    .contains("create sequence data_history.topic_tree_id_seq start with 1 increment by 1")
                    .contains("create sequence data_history.device_baseline_values_id_seq start with 1 increment by 1")
                    .contains("create sequence data_history.data_id_seq start with 1 increment by 1")
                    .contains("create table data_history.topic_tree")
                    .contains("create table data_history.device_baseline_values")
                    .contains("create table data_history.data")
                    .contains("default nextval('data_history.topic_tree_id_seq')")
                    .contains("default nextval('data_history.device_baseline_values_id_seq')")
                    .contains("default nextval('data_history.data_id_seq')")
                    .contains("primary key (id)")
                    .contains("constraint uk_topic_tree_topic_parent unique (topic, parent_id)")
                    .contains("constraint uk_device_baseline_values_device_topic_interval unique "
                            + "(device_id, topic_tree_id, interval)")
                    .contains("create index idx_topic_tree_parent on data_history.topic_tree (parent_id)")
                    .contains("create index idx_topic_tree_path on data_history.topic_tree (path)")
                    .contains("create index idx_device_baseline_values_device on data_history.device_baseline_values "
                            + "(device_id)")
                    .contains("create index idx_device_baseline_values_topic_tree on data_history.device_baseline_values "
                            + "(topic_tree_id)")
                    .contains("create index idx_data_topic on data_history.data (topic)")
                    .contains("create index idx_data_date_time on data_history.data (date_time)")
                    .contains("constraint fk_topic_tree_parent foreign key (parent_id) references "
                            + "data_history.topic_tree")
                    .contains("constraint fk_device_baseline_values_topic_tree foreign key (topic_tree_id) "
                            + "references data_history.topic_tree")
                    .contains("parent_id bigint")
                    .contains("topic_tree_id bigint not null")
                    .contains("device_id bigint not null")
                    .contains("log_time timestamp(6) not null")
                    .contains("date_time timestamp(6)")
                    .doesNotContain("device.topic_tree_id_seq")
                    .doesNotContain("device.device_baseline_values_id_seq")
                    .doesNotContain("device.data_id_seq");

            assertThat(ddl.indexOf("create sequence data_history.topic_tree_id_seq"))
                    .isLessThan(ddl.indexOf("create table data_history.topic_tree"));
            assertThat(ddl.indexOf("create sequence data_history.device_baseline_values_id_seq"))
                    .isLessThan(ddl.indexOf("create table data_history.device_baseline_values"));
            assertThat(ddl.indexOf("create sequence data_history.data_id_seq"))
                    .isLessThan(ddl.indexOf("create table data_history.data"));
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
