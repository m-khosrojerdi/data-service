package com.mapnamind.data.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataValue implements Serializable {

    private String x;
    private Double y;
    private Double c;

}
