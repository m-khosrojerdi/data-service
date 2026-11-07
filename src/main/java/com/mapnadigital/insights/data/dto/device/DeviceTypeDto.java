package com.mapnadigital.insights.data.dto.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mapnadigital.insights.data.enumeration.DeviceTypeMobility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTypeDto implements Serializable {

    private Long id;
    private String name;
    @JsonProperty("fName")
    private String fName;
    private String icon;
    private DeviceTypeMobility type;
    private String description;
    private Long hubId;
    private Boolean hasHub;

    /**
     * Number of devices of this type inside an asset; only populated by the
     * dashboard counters.
     */
    private Long count;

    @JsonIgnore
    private List<Long> ids;

    @JsonIgnore
    private Long assetId;

    @JsonIgnore
    private Integer safetyLock;
}
