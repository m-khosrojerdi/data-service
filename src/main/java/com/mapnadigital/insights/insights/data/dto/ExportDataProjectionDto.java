package com.mapnadigital.insights.insights.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Export-only projection (date and time are split for the CSV/XLSX writer).
 * It replaces the former {@code ExportDataProjectionDto}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportDataProjectionDto {

    private Double value;
    private String date;
    private String time;
}
