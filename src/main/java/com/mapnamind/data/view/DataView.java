package com.mapnamind.data.view;

import com.mapnamind.common.model.entity.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class DataView implements Serializable {

    private Integer total;
    private List<Data> values;

}
