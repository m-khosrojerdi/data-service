package com.mapnadigital.insights.data.dto.topicTree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Node of the topic tree.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TopicTree", description = "Node of the signal hierarchy: a device type, a device serial number or "
        + "a `data` node that carries the unit and the description of a signal.")
public class TopicTreeDto implements Serializable {

    @Schema(description = "Identifier of the node", example = "31")
    private Long id;

    @Schema(description = "Identifier the UI uses to address the node. It equals `id` for the stored nodes and is "
            + "synthesised for the serial nodes the tree assembly adds.", example = "31")
    private Long code;

    @Schema(description = "Last segment of the topic of the node", example = "temperature")
    private String topic;

    @Schema(description = "Full topic path of the node", example = "lw_boiler/LW-0000000001042/data/temperature")
    private String path;

    @Schema(description = "Nick name of the device, only set on the serial nodes", example = "Boiler of the plant")
    private String deviceName;

    @Schema(description = "Identifier of the parent node; `null` for a root", example = "30")
    private Long parentId;

    @Schema(description = "`1` when the node is a property of its parent, `0` otherwise", example = "1")
    private Integer isProperty;

    @Schema(description = "Description of the node", example = "Water temperature")
    private String description;

    @Schema(description = "Whether the node is visible in the UI", example = "true")
    private Boolean isVisible;

    @Schema(description = "Unit of the signal, only set on the `data` nodes", example = "C")
    private String unit;

    @Schema(description = "Children of the node; an empty list on a leaf")
    private List<TopicTreeDto> children;

    @Schema(description = "Whether the node has children, so the UI can lazily expand the tree", example = "false")
    private Boolean hasChildren;

    /**
     * Tree assembly metadata. It is not part of the wire contract; the former
     * {@code TopicTreeDTO} exposed the same values internally.
     */
    @JsonIgnore
    @Schema(hidden = true)
    private Integer level;

    @JsonIgnore
    @Schema(hidden = true)
    private Date logTime;

    @JsonIgnore
    @Schema(hidden = true)
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
