package com.mapnadigital.insights.insights.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicTreeDTO implements Serializable, Cloneable {

    private Long id;
    private Long code; // code property is original id property
    private String topic;
    private String deviceName;
    private Long parentId;
    private Integer isProperty;
    private Integer level;
    private String description;
    private Date logTime;
    private String path;
    private Integer priority;
    private Boolean isVisible;
    private String unit;
    private List<TopicTreeDTO> children;
    private Boolean hasChildren;

    public List<TopicTreeDTO> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<TopicTreeDTO> children) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.addAll(children);
    }

    public void addChild(TopicTreeDTO child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    @Override
    public TopicTreeDTO clone() {
        try {
            return (TopicTreeDTO) super.clone();
        } catch (CloneNotSupportedException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
