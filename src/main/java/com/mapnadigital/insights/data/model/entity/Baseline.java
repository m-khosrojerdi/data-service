package com.mapnadigital.insights.data.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Baseline value of a topic of a device.
 * <p>
 * The entity is owned by this module and maps the table {@code cmn.device_baseline_values}. The topic tree is a
 * relation of the same module; the device belongs to {@code device-service}, so it is only referenced by its
 * identifier ({@code deviceId}) and the column name is kept so the schema does not change.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "device_baseline_values", schema = "cmn")
public class Baseline implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identifier of the device of {@code device-service}; the association was replaced by the identifier so that
     * the module keeps the column (and therefore the schema) and does not depend on the device domain.
     */
    @Column(name = "device_id")
    private Long deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    private TopicTree topicTree;

    @Pattern(regexp = "^(?=.+)(?:(?:1\\s+year|[2-9]\\d*\\s+years)\\s*)?(?:(?:1\\s+month|[2-9]\\d*\\s+months)\\s*)?(?:(?:1\\s+day|[2-9]\\d*\\s+days)\\s*)?(?:(?:1\\s+hour|[2-9]\\d*\\s+hours)\\s*)?(?:(?:1\\s+min|[2-9]\\d*\\s+mins)\\s*)?(?:(?:1\\s+second|[2-9]\\d*\\s+seconds))?$",
            message = "Invalid interval format. Example: '1 day 2 minutes 3 seconds'")
    @Column
    private String interval;

    @Column
    private Double value;

}
