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
public class BaselineDTO implements Serializable {
    private Long id;
    private Long userDeviceId;
    private Long topicTreeId;
    private Double value;
}
