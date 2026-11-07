package com.mapnadigital.insights.data.dto.data;

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
public class ValueDto implements Serializable {

    private String name;
    private String serial;
    private String param;
    private String effectiveInterval;
    private String unit;
    private Double baselineValue;
    private List<DataValueDto> data;
}
