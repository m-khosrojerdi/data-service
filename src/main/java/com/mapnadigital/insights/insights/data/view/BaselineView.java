package com.mapnadigital.insights.insights.data.view;

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
    private String topic;
    private String interval;
    private Double value;
}
