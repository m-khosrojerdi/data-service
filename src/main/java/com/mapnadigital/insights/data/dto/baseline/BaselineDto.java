package com.mapnadigital.insights.data.dto.baseline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaselineDto implements Serializable {

    private Long id;
    private String topic;
    private String interval;
    private Double value;

}
