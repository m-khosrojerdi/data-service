package com.mapnadigital.insights.data.dto.topicTree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicTreeDto implements Serializable {

    private Long id;
    private Long code;
    private String topic;
    private String path;
    private String deviceName;
    private Long parentId;
    private Integer isProperty;
    private String description;
    private Boolean isVisible;
    private String unit;
    private List<TopicTreeDto> children;
    private Boolean hasChildren;

    /**
     * Tree assembly metadata. It is not part of the wire contract; the former
     * {@code TopicTreeDTO} exposed the same values internally.
     */
    @JsonIgnore
    private Integer level;

    @JsonIgnore
    private Date logTime;

    @JsonIgnore
    private Integer priority;

    public List<TopicTreeDto> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<TopicTreeDto> children) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.addAll(children);
    }

    public void addChild(TopicTreeDto child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    /**
     * Shallow copy used while the topic tree is re-parented.
     */
    public TopicTreeDto clone() {
        return TopicTreeDto.builder()
                .id(id)
                .code(code)
                .topic(topic)
                .path(path)
                .deviceName(deviceName)
                .parentId(parentId)
                .isProperty(isProperty)
                .description(description)
                .isVisible(isVisible)
                .unit(unit)
                .children(children == null ? null : new ArrayList<>(children))
                .hasChildren(hasChildren)
                .level(level)
                .logTime(logTime)
                .priority(priority)
                .build();
    }

}
