package com.mapnadigital.insights.data.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * One aggregated sample of a signal.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DataValue", description = "One aggregated sample: the timestamp on the x axis and the value on "
        + "the y axis.")
public class DataValueDto implements Serializable {

    @Schema(description = "Timestamp of the sample", example = "2026-11-01 00:00:00.0")
    private String x;

    @Schema(description = "Aggregated value of the sample", example = "20.4")
    private Double y;

}
