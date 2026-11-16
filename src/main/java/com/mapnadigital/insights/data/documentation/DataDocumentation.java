package com.mapnadigital.insights.data.documentation;

import com.mapnadigital.insights.data.dto.data.SignalSelectorDto;
import com.mapnadigital.insights.web.dto.ResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;

import static com.mapnadigital.insights.web.enumeration.Constant.REALM;
import static com.mapnadigital.insights.web.enumeration.Swagger.ACCESS_TOKEN;
import static com.mapnadigital.insights.web.enumeration.Swagger.LANGUAGE;

/**
 * Swagger/OpenAPI documentation of {@code DataController}.
 *
 * <p>The documentation lives on this interface so that the controller only handles the requests. Springdoc resolves
 * the annotations declared here through the interface hierarchy of the controller.</p>
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Tag(name = ApiDocumentation.DATA_TAG, description = ApiDocumentation.DATA_TAG_DESCRIPTION)
public interface DataDocumentation {

    @Operation(
            summary = "Read the aggregated values of the selected signals",
            description = "Returns one `ValueDto` per requested signal with the aggregated samples of the requested "
                    + "period, the unit of the signal and its baseline value. The effective interval is the larger "
                    + "of the interval of the signal and the period divided by `sampleCount`, rounded to minutes, so "
                    + "the response never contains more points than the caller asked for. A signal is addressed "
                    + "either by `topic` or, on `/data/get-data`, by `serial` and `param` (the device type is "
                    + "resolved over REST from `device-service`). When `stringSignals` is sent instead of `signals`, "
                    + "it is parsed as a comma separated list of `topic:isProperty` pairs. Requires the CUSTOMER "
                    + "role. " + ApiDocumentation.ENVELOPE_NOTE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Values of the requested signals returned; `body` is "
                    + "the request echoed with the `values` list filled in",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SignalSelectorDto.class),
                            examples = @ExampleObject(name = "Aggregated values",
                                    value = ApiDocumentation.SIGNAL_SELECTOR_RESPONSE_EXAMPLE))),
            @ApiResponse(responseCode = "400", description = ApiDocumentation.VALIDATION_400, content = @Content),
            @ApiResponse(responseCode = "401", description = ApiDocumentation.UNAUTHORIZED_401, content = @Content),
            @ApiResponse(responseCode = "403", description = ApiDocumentation.FORBIDDEN_403, content = @Content),
            @ApiResponse(responseCode = "500", description = ApiDocumentation.INTERNAL_ERROR_500, content = @Content)
    })
    @RequestBody(
            required = true,
            description = "Period and signals to read. `startDate` and `endDate` are mandatory; every "
                    + "signal needs `topic` (or `serial` + `param` on `/data/get-data`) and `isProperty` "
                    + "(`0` for a raw value, `1` for a property). `interval`, `aggregation` and "
                    + "`sampleCount` are optional.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SignalSelectorDto.class),
                    examples = {
                            @ExampleObject(name = "Signals by topic",
                                    value = ApiDocumentation.SIGNAL_SELECTOR_REQUEST_EXAMPLE),
                            @ExampleObject(name = "Signals by compact list",
                                    value = ApiDocumentation.SIGNAL_SELECTOR_STRING_REQUEST_EXAMPLE)
                    }))
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    ResponseBody<?> signalSelector(SignalSelectorDto view);

    @Operation(
            summary = "Export the selected signals asynchronously",
            description = "Queues an export of the selected signals on the `signal-selector-export` ActiveMQ queue "
                    + "and returns the identifier of the export immediately. The worker reads the values, writes an "
                    + "XLSX file (one sheet per signal, zipped) and publishes the result to the requesting user "
                    + "through the WebSocket infrastructure of `notification-service` with the message type "
                    + "`data.export` (`data.export.error` on failure). The file is written in the language of the "
                    + "`Accept-Language` header. Requires the CUSTOMER role. " + ApiDocumentation.ENVELOPE_NOTE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Export queued; `body.exportUuid` identifies the "
                    + "asynchronous job",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SignalSelectorDto.class),
                            examples = @ExampleObject(name = "Export queued",
                                    value = ApiDocumentation.SIGNAL_SELECTOR_EXPORT_RESPONSE_EXAMPLE))),
            @ApiResponse(responseCode = "400", description = ApiDocumentation.VALIDATION_400, content = @Content),
            @ApiResponse(responseCode = "401", description = ApiDocumentation.UNAUTHORIZED_401, content = @Content),
            @ApiResponse(responseCode = "403", description = ApiDocumentation.FORBIDDEN_403, content = @Content),
            @ApiResponse(responseCode = "500", description = ApiDocumentation.INTERNAL_ERROR_500, content = @Content)
    })
    @RequestBody(
            required = true,
            description = "Period and signals to export. `startDate`, `endDate` and `signals` are "
                    + "mandatory; every signal needs `topic`, `isProperty`, `interval` and `aggregation`.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SignalSelectorDto.class),
                    examples = @ExampleObject(name = "Export request",
                            value = ApiDocumentation.SIGNAL_SELECTOR_REQUEST_EXAMPLE)))
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    ResponseBody<SignalSelectorDto> signalSelectorExport(SignalSelectorDto view);
}
