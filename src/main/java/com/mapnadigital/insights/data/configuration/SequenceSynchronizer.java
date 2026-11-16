package com.mapnadigital.insights.data.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Aligns the identifier sequences of the module with the rows already stored in the tables.
 * <p>
 * The sequences live in {@code data_history} and are shared by the JPA inserts (through
 * {@code @SequenceGenerator}) and by the direct SQL inserts (through the {@code nextval} column default). When the
 * tables already contain rows - because they were restored from a dump, because a previous run inserted them or
 * because they were written by another process - a sequence that is behind the data hands out identifiers that are
 * already used, and the insert fails with a duplicate key. This runner therefore sets every sequence to
 * {@code max(id) + 1} of its table once the application context is up, which is after Hibernate has created or
 * updated the tables.
 * <p>
 * The synchronisation is idempotent and never deletes or rewrites a row: it only moves the sequence forward (or
 * back to the first free identifier when the table is empty), so it cannot introduce data loss. A table that does
 * not exist yet - for example when {@code spring.jpa.hibernate.ddl-auto} is {@code none} and the schema is managed
 * by a migration - is skipped with a warning instead of failing the start-up.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SequenceSynchronizer implements ApplicationRunner {

    /**
     * Sequence/table pairs owned by this module. The sequence is the one declared by the {@code @SequenceGenerator}
     * of the entity and referenced by the {@code nextval} default of its {@code id} column.
     */
    private static final List<SequenceTable> SEQUENCES = List.of(
            new SequenceTable("data_history.topic_tree_id_seq", "data_history.topic_tree"),
            new SequenceTable("data_history.device_baseline_values_id_seq", "data_history.device_baseline_values"),
            new SequenceTable("data_history.data_id_seq", "data_history.data")
    );

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        SEQUENCES.forEach(this::synchronize);
    }

    /**
     * Sets the sequence to the first identifier that is not used by the table.
     *
     * @param sequenceTable the sequence and the table it feeds
     */
    private void synchronize(SequenceTable sequenceTable) {
        if (!relationExists(sequenceTable.table())) {
            log.warn("table {} does not exist, sequence {} is left untouched",
                    sequenceTable.table(), sequenceTable.sequence());
            return;
        }

        Long nextValue = jdbcTemplate.queryForObject(
                "select coalesce(max(id), 0) + 1 from " + sequenceTable.table(), Long.class);
        jdbcTemplate.queryForObject(
                "select setval('" + sequenceTable.sequence() + "', ?, false)", Long.class, nextValue);

        log.info("sequence {} synchronized with {}: the next identifier is {}",
                sequenceTable.sequence(), sequenceTable.table(), nextValue);
    }

    /**
     * Checks whether a schema-qualified relation exists, so a missing table is reported instead of failing the
     * start-up with a SQL error.
     *
     * @param relation the schema-qualified relation name
     * @return {@code true} when the relation exists
     */
    private boolean relationExists(String relation) {
        Boolean exists = jdbcTemplate.queryForObject("select to_regclass(?) is not null", Boolean.class, relation);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * A sequence and the table whose {@code id} column it feeds.
     *
     * @param sequence the schema-qualified sequence name
     * @param table    the schema-qualified table name
     */
    private record SequenceTable(String sequence, String table) {
    }
}
