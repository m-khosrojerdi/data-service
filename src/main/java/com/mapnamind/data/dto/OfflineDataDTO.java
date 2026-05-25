package com.mapnamind.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfflineDataDTO implements Serializable {

    private String topic;
    private String payload;
    private String date;

}
