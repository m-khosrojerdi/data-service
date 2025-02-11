package com.mapnamind.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataProjectionDTO {
    Double value;
    Double valueChanges;
    Timestamp dateTime;
}
