package com.mapnadigital.insights.data.dto.data;

import com.mapnadigital.insights.common.annotation.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Request and response contract of the signal selector.
 * <p>
 * The same type is the request body of {@code POST /data/signal-selector}, {@code POST /data/get-data} and
 * {@code POST /data/signal-selector/export}, and the response body of the two read endpoints (echoed with the
 * {@code values} list filled in) and of the export endpoint (echoed with the {@code exportUuid} filled in).
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "SignalSelector", description = "Period, signals and (on the response) values of a signal selector "
        + "query.")
public class SignalSelectorDto implements Serializable {

    @Schema(description = "Compact alternative to `signals`: a comma separated list of `topic:isProperty` pairs. "
            + "Ignored when `signals` is present.", example = "lw_boiler/LW-0000000001042/data/temperature:0")
    private String stringSignals;

    @Schema(description = "Signals to read. Mandatory unless `stringSignals` is sent.")
    private List<SignalDto> signals;

    @Schema(description = "Values of the requested signals. Read-only: it is filled in by the server and ignored "
            + "on the request.", accessMode = Schema.AccessMode.READ_ONLY)
    private List<ValueDto> values;

    @Date
    @Schema(description = "Start of the period, in the UI date time format", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2026-11-01 00:00:00")
    private String startDate;

    @Date
    @Schema(description = "End of the period, in the UI date time format", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2026-11-02 00:00:00")
    private String endDate;

    @Schema(description = "Identifier of the asynchronous export. Read-only: it is filled in by the export "
            + "endpoint and delivered to the client through the `data.export` WebSocket message.",
            accessMode = Schema.AccessMode.READ_ONLY,
            example = "3f1c9a52-6a1e-4a0e-9d0f-2b7c8d9e0f11")
    private String exportUuid;

    public void addValue(ValueDto value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        this.values.add(value);
    }

}
