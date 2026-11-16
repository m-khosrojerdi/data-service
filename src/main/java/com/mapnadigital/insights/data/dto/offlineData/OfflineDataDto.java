package com.mapnadigital.insights.data.dto.offlineData;

import com.mapnadigital.insights.common.annotation.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Sample collected by a client while it was offline.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "OfflineData", description = "Sample collected offline and published back into the platform.")
public class OfflineDataDto implements Serializable {

    @NotBlank(message = "view.offlineData.topic.notBlank")
    @Schema(description = "MQTT topic of the signal the sample belongs to",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "lw_boiler/LW-0000000001042/data/temperature")
    private String topic;

    @NotBlank(message = "view.offlineData.payload.notBlank")
    @Schema(description = "Raw JSON document of the sample, stored as received",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "{\"value\": 20.4}")
    private String payload;

    @Date(message = "view.offlineData.date.notBlank")
    @Schema(description = "Date time of the sample, in the UI date time format",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-11-01 08:30:00")
    private String date;

}
