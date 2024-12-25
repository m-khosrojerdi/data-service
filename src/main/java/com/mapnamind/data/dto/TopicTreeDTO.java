package com.mapnamind.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicTreeDTO implements Serializable {

    private Long id;
    private String topic;
    private Long parentId;
    private Integer isProperty;
    private Integer level;
    private String description;
    private Date logTime;
    private String path;
    private Integer priority;
    private List<TopicTreeDTO> children;

    public List<TopicTreeDTO> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public void addChild(TopicTreeDTO child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    /*@Override
    public String toString() {
        return "TreeNode{id=" + id + ", topic='" + topic + '\'' + ", children=" + children + '}';
    }*/

}
