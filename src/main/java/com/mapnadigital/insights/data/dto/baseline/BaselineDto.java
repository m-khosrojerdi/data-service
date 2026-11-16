package com.mapnadigital.insights.data.dto.baseline;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Request and response contract of the signal baselines.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Baseline", description = "Baseline value of a signal, identified by its topic and its interval.")
public class BaselineDto implements Serializable {

    @Schema(description = "Identifier of the stored baseline. Read-only: it is filled in by the server.",
            accessMode = Schema.AccessMode.READ_ONLY, example = "17")
    private Long id;

    @Schema(description = "MQTT topic of the signal, `<deviceType>/<serial>/data/<param>`",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "lw_boiler/LW-0000000001042/data/temperature")
    private String topic;

    @Schema(description = "Interval the baseline applies to, in the PostgreSQL interval syntax",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "1 hour")
    private String interval;

    @Schema(description = "Baseline value. Mandatory to save the baseline; omit it (or send `null`) to delete "
            + "the baseline.", example = "21.5")
    private Double value;

}
