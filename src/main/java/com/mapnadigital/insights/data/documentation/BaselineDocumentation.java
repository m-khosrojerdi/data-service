package com.mapnadigital.insights.data.documentation;

import com.mapnadigital.insights.data.dto.baseline.BaselineDto;
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

import static com.mapnadigital.insights.web.enumeration.Swagger.ACCESS_TOKEN;
import static com.mapnadigital.insights.web.enumeration.Swagger.LANGUAGE;

/**
 * Swagger/OpenAPI documentation of {@code BaselineController}.
 *
 * <p>The documentation lives on this interface so that the controller only handles the requests. Springdoc resolves
 * the annotations declared here through the interface hierarchy of the controller.</p>
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Tag(name = ApiDocumentation.BASELINE_TAG, description = ApiDocumentation.BASELINE_TAG_DESCRIPTION)
public interface BaselineDocumentation {

    @Operation(
            summary = "Read the baseline of a signal",
            description = "Returns the baseline stored for the device, the topic tree node and the interval of the "
                    + "requested topic. The device is resolved from the second segment of the topic over REST "
                    + "(`device-service`). A topic without a stored baseline is reported with the business code "
                    + "9011. Requires the CUSTOMER role. " + ApiDocumentation.ENVELOPE_NOTE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Baseline returned; `body` is the stored baseline",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaselineDto.class),
                            examples = @ExampleObject(name = "Stored baseline",
                                    value = ApiDocumentation.BASELINE_RESPONSE_EXAMPLE))),
            @ApiResponse(responseCode = "400", description = ApiDocumentation.VALIDATION_400, content = @Content),
            @ApiResponse(responseCode = "401", description = ApiDocumentation.UNAUTHORIZED_401, content = @Content),
            @ApiResponse(responseCode = "403", description = ApiDocumentation.FORBIDDEN_403, content = @Content),
            @ApiResponse(responseCode = "500", description = ApiDocumentation.INTERNAL_ERROR_500, content = @Content)
    })
    @RequestBody(
            required = true,
            description = "Topic and interval of the baseline to read. `topic` and `interval` are "
                    + "mandatory; `value` is ignored.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BaselineDto.class),
                    examples = @ExampleObject(name = "Baseline lookup",
                            value = ApiDocumentation.BASELINE_DELETE_REQUEST_EXAMPLE)))
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    ResponseBody<BaselineDto> get(BaselineDto view);

    @Operation(
            summary = "Create, update or delete the baseline of a signal",
            description = "Upserts the baseline of the requested topic when `value` is present and deletes it when "
                    + "`value` is `null`, so the same endpoint covers the three write operations of the UI. The "
                    + "baseline is looked up by device, topic tree node and interval; a new row is inserted when it "
                    + "does not exist yet. Requires the CUSTOMER role. " + ApiDocumentation.ENVELOPE_NOTE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Baseline saved or deleted; `body` is the saved "
                    + "baseline, or an empty object when it was deleted",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BaselineDto.class),
                            examples = @ExampleObject(name = "Saved baseline",
                                    value = ApiDocumentation.BASELINE_RESPONSE_EXAMPLE))),
            @ApiResponse(responseCode = "400", description = ApiDocumentation.VALIDATION_400, content = @Content),
            @ApiResponse(responseCode = "401", description = ApiDocumentation.UNAUTHORIZED_401, content = @Content),
            @ApiResponse(responseCode = "403", description = ApiDocumentation.FORBIDDEN_403, content = @Content),
            @ApiResponse(responseCode = "500", description = ApiDocumentation.INTERNAL_ERROR_500, content = @Content)
    })
    @RequestBody(
            required = true,
            description = "Baseline to write. `topic` and `interval` are mandatory; `value` is mandatory "
                    + "to save the baseline and must be omitted (or `null`) to delete it.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BaselineDto.class),
                    examples = {
                            @ExampleObject(name = "Save baseline",
                                    value = ApiDocumentation.BASELINE_REQUEST_EXAMPLE),
                            @ExampleObject(name = "Delete baseline",
                                    value = ApiDocumentation.BASELINE_DELETE_REQUEST_EXAMPLE)
                    }))
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    ResponseBody<BaselineDto> updateOrDelete(BaselineDto view);
}
