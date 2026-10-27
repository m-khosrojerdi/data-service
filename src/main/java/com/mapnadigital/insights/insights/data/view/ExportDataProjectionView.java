package com.mapnadigital.insights.insights.data.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Export-only projection (date and time are split for the CSV/XLSX writer).
 * It replaces the former {@code ExportDataProjectionView}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportDataProjectionView {

    private Double value;
    private String date;
    private String time;
}
