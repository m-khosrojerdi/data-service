package com.mapnamind.data.view;

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
public class ValueView implements Serializable {

    private String name;
    private String effectiveInterval;
    private String unit;
    private Double baselineValue;
    private List<DataValue> data;
}
