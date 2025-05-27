package com.mapnamind.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataProjectionDTO {
    private Double value;
    private Double valueChanges;
    private String dateTime;
}
