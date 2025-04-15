package com.mapnamind.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalDTO implements Serializable {

    private String topic;
    private String isProperty;
    private String interval;
    private String aggregation;
    private Integer sampleCount;
}
