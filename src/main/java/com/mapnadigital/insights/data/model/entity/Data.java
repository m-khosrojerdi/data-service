package com.mapnadigital.insights.data.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Raw value of a topic.
 * <p>
 * The entity is owned by this module and maps the default table derived from the class name. It has no relation to
 * another module.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Data implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "value")
    private String value;

    @Column(name = "dateTime")
    private Timestamp dateTime;

    @Column(name = "topic")
    private String topic;

}
