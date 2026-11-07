package com.mapnadigital.insights.data.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;

/**
 * Node of the topic tree of the devices.
 * <p>
 * The entity is owned by this module and maps the table {@code cmn.topicTree}. The parent is a relation of the same
 * module, so it stays a {@code @ManyToOne}.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "topicTree", schema = "cmn")
public class TopicTree implements Serializable {

    public TopicTree(TopicTree topicTree, Boolean hasChildren) {
        this.id = topicTree.getId();
        this.topic = topicTree.getTopic();
        this.parent = topicTree.getParent();
        this.isProperty = topicTree.getIsProperty();
        this.level = topicTree.getLevel();
        this.description = topicTree.getDescription();
        this.logTime = topicTree.getLogTime();
        this.path = topicTree.getPath();
        this.priority = topicTree.getPriority();
        this.isVisible = topicTree.getIsVisible();
        this.unit = topicTree.getUnit();
        this.hasChildren = hasChildren;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "topic")
    private String topic;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parentId")
    private TopicTree parent;

    @Column(name = "isProperty")
    private Integer isProperty;

    @Column(name = "level")
    private Integer level;

    @Column(name = "description")
    private String description;

    @Column(name = "logTime")
    private Date logTime;

    @Column(name = "path")
    private String path;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "isVisible")
    private Boolean isVisible;

    @Column(name = "unit")
    private String unit;

    @Transient
    private Boolean hasChildren;
}
