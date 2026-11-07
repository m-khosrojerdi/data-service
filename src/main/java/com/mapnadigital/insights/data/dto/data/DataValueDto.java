package com.mapnadigital.insights.data.dto.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataValueDto implements Serializable {

    private String x;
    private Double y;

}
