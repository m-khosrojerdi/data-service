package com.mapnadigital.insights.data.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * One signal of a signal selector query.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Signal", description = "Signal to read: its topic (or serial and parameter), the kind of value "
        + "and the aggregation to apply.")
public class SignalDto implements Serializable {

    @NotBlank
    @Schema(description = "MQTT topic of the signal, `<deviceType>/<serial>/data/<param>`",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "lw_boiler/LW-0000000001042/data/temperature")
    private String topic;

    @Schema(description = "Serial number of the device. Only used by `/data/get-data`, where the topic is built "
            + "from the serial and the parameter.", example = "LW-0000000001042")
    private String serial;

    @Schema(description = "Parameter of the signal. Only used by `/data/get-data`, where the topic is built from "
            + "the serial and the parameter.", example = "temperature")
    private String param;

    @NotNull
    @Pattern(regexp = "0|1", message = "view.data.property.notValidIsProperty")
    @Schema(description = "`0` to read the raw value of the topic, `1` to read a property of the topic",
            requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"0", "1"}, example = "0")
    private String isProperty;

    @Schema(description = "Aggregation interval in the PostgreSQL interval syntax. The effective interval is the "
            + "larger of this value and the period divided by `sampleCount`.", example = "1 hour")
    private String interval;

    @Schema(description = "Aggregation function applied by `data_history.fnc_get_aggr_data` (`avg`, `min`, `max`, "
            + "`sum`, ...).", example = "avg")
    private String aggregation;

    @Schema(description = "Number of samples the caller wants in the period; it caps the resolution of the "
            + "response.", example = "24")
    private Integer sampleCount;
}
