package com.mapnamind.data.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaselineView implements Serializable {
    private Long id;
    private Long userDeviceId;
    private Long topicTreeId;
    private String interval;
    private Double value;
}
