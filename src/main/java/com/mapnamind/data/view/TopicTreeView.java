package com.mapnamind.data.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicTreeView implements Serializable {

    private Long id;
    private Long code;
    private String topic;
    private Long parentId;
    private Integer isProperty;
    private String description;
    private List<TopicTreeView> children;

}
