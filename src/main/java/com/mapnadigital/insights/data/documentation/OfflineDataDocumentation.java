package com.mapnadigital.insights.data.documentation;

import com.mapnadigital.insights.data.dto.offlineData.OfflineDataDto;
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
 * Swagger/OpenAPI documentation of {@code OfflineDataController}.
 *
 * <p>The documentation lives on this interface so that the controller only handles the requests. Springdoc resolves
 * the annotations declared here through the interface hierarchy of the controller.</p>
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Tag(name = ApiDocumentation.OFFLINE_DATA_TAG, description = ApiDocumentation.OFFLINE_DATA_TAG_DESCRIPTION)
public interface OfflineDataDocumentation {

    @Operation(
            summary = "Publish a sample collected while the client was offline",
            description = "Forwards the sample to the `data_history.fnc_save_data` database function, which stores it "
                    + "exactly like a live MQTT sample, so it takes part in the aggregations and the exports. The "
                    + "topic must be an existing signal topic and the date must be a valid date time. Requires one "
                    + "of the ADMIN, CUSTOMER, MANUFACTURE or AGENCY roles. " + ApiDocumentation.ENVELOPE_NOTE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sample stored; the envelope carries no `body`. A "
                    + "failed write is reported with the business code 9001",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseBody.class),
                            examples = @ExampleObject(name = "Stored", value = """
                                    {
                                      "code": 200,
                                      "message": "The requested offline data was successfully saved."
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = ApiDocumentation.VALIDATION_400, content = @Content),
            @ApiResponse(responseCode = "401", description = ApiDocumentation.UNAUTHORIZED_401, content = @Content),
            @ApiResponse(responseCode = "403", description = ApiDocumentation.FORBIDDEN_403, content = @Content),
            @ApiResponse(responseCode = "500", description = ApiDocumentation.INTERNAL_ERROR_500, content = @Content)
    })
    @RequestBody(
            required = true,
            description = "Sample to store. `topic`, `payload` and `date` are mandatory; `payload` is the "
                    + "raw JSON document of the sample.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = OfflineDataDto.class),
                    examples = @ExampleObject(name = "Offline sample",
                            value = ApiDocumentation.OFFLINE_DATA_REQUEST_EXAMPLE)))
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE)})
    ResponseBody<?> publish(OfflineDataDto view);
}
