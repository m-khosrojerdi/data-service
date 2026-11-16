package com.mapnadigital.insights.data.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Values of one signal of a signal selector response.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Value", description = "Aggregated values of one signal, with its unit and its baseline.")
public class ValueDto implements Serializable {

    @Schema(description = "Topic of the signal as it was requested",
            example = "lw_boiler/LW-0000000001042/data/temperature")
    private String name;

    @Schema(description = "Serial number of the device, when the signal was requested by serial",
            example = "LW-0000000001042")
    private String serial;

    @Schema(description = "Parameter of the signal, when it was requested by parameter", example = "temperature")
    private String param;

    @Schema(description = "Interval actually used by the aggregation, in the PostgreSQL interval syntax",
            example = "1 hour")
    private String effectiveInterval;

    @Schema(description = "Unit of the signal, read from the topic tree", example = "C")
    private String unit;

    @Schema(description = "Baseline of the signal for the requested interval; `null` when no baseline is stored",
            example = "21.5")
    private Double baselineValue;

    @Schema(description = "Aggregated samples of the period")
    private List<DataValueDto> data;
}
