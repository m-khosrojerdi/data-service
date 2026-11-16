package com.mapnadigital.insights.data.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "ExportDataProjection", description = "One row of the exported file: the value and the date and "
        + "time of the sample, split for the XLSX writer.")
public class ExportDataProjectionDto {

    @Schema(description = "Value of the sample", example = "20.4")
    private Double value;

    @Schema(description = "Date of the sample", example = "2026-11-01")
    private String date;

    @Schema(description = "Time of the sample", example = "00:00:00")
    private String time;
}
