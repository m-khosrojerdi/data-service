package com.mapnamind.data.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataHistoryView implements Serializable {

    private String topic;
    private String startDate;
    private String endDate;
    private String isProperty;
    private Integer pageSize;
    private Integer pageNumber;

}
