package com.mapnadigital.insights.data.documentation;

import com.mapnadigital.insights.data.dto.topicTree.DateDto;
import com.mapnadigital.insights.data.dto.topicTree.TopicTreeDto;
import com.mapnadigital.insights.web.dto.ResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

import java.util.List;

import static com.mapnadigital.insights.web.enumeration.Constant.REALM;
import static com.mapnadigital.insights.web.enumeration.Swagger.ACCESS_TOKEN;
import static com.mapnadigital.insights.web.enumeration.Swagger.LANGUAGE;

/**
 * Swagger/OpenAPI documentation of {@code TopicTreeController}.
 *
 * <p>The documentation lives on this interface so that the controller only handles the requests. Springdoc resolves
 * the annotations declared here through the interface hierarchy of the controller.</p>
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Tag(name = ApiDocumentation.TOPIC_TREE_TAG, description = ApiDocumentation.TOPIC_TREE_TAG_DESCRIPTION)
public interface TopicTreeDocumentation {

    @Operation(
            summary = "Read the root of the topic tree of the caller",
            description = "Returns the root nodes (the device types) of the devices granted to the caller, each one "
                    + "with its `data` children and the serial nodes of the devices. The devices are resolved over "
                    + "REST from `user-service` (`user-get-all-serial-by-access-level`), so the tree only contains "
                    + "what the caller may see; a caller without devices is reported with the business code 9004. "
                    + "Requires one of the ADMIN, CUSTOMER, MANUFACTURE or AGENCY roles. "
                    + ApiDocumentation.ENVELOPE_NOTE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Root nodes returned; `body` is the list of roots "
                    + "with their children",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TopicTreeDto.class),
                            examples = @ExampleObject(name = "Root tree",
                                    value = ApiDocumentation.TOPIC_TREE_LIST_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = ApiDocumentation.UNAUTHORIZED_401, content = @Content),
            @ApiResponse(responseCode = "403", description = ApiDocumentation.FORBIDDEN_403, content = @Content),
            @ApiResponse(responseCode = "500", description = ApiDocumentation.INTERNAL_ERROR_500, content = @Content)
    })
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    ResponseBody<List<TopicTreeDto>> rootTree();

    @Operation(
            summary = "Read the children of a topic tree node",
            description = "Returns the direct children of the node referenced by `parentId`, each one flagged with "
                    + "`hasChildren` so the UI can lazily expand the tree. Only visible nodes are returned. Requires "
                    + "one of the ADMIN, CUSTOMER, MANUFACTURE or AGENCY roles. " + ApiDocumentation.ENVELOPE_NOTE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Children returned; `body` is the list of children",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TopicTreeDto.class),
                            examples = @ExampleObject(name = "Children of a node",
                                    value = ApiDocumentation.TOPIC_TREE_LIST_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = ApiDocumentation.UNAUTHORIZED_401, content = @Content),
            @ApiResponse(responseCode = "403", description = ApiDocumentation.FORBIDDEN_403, content = @Content),
            @ApiResponse(responseCode = "500", description = ApiDocumentation.INTERNAL_ERROR_500, content = @Content)
    })
    @Parameters({
            @Parameter(name = "parentId", in = ParameterIn.PATH,
                    description = "Identifier of the topic tree node whose children are read",
                    required = true, example = "12")
    })
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    ResponseBody<List<TopicTreeDto>> childrenTree(Long parentId);

    @Operation(
            summary = "Search the topic tree of the caller",
            description = "Returns the tree of the devices granted to the caller filtered by the given criteria. "
                    + "`serialNumber` matches a part of the serial number, `deviceName` matches a part of the nick "
                    + "name or of the device type name, `description` matches a part of the description of a node and "
                    + "`company` restricts the result to the devices of that company (the caller must be a member of "
                    + "it, otherwise the business code 9006 is returned). Without any criterion the whole tree of the "
                    + "caller is returned. Requires one of the ADMIN, CUSTOMER, MANUFACTURE or AGENCY roles. "
                    + ApiDocumentation.ENVELOPE_NOTE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matching nodes returned; `body` is the list of roots "
                    + "of the matching sub-trees",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TopicTreeDto.class),
                            examples = @ExampleObject(name = "Search result",
                                    value = ApiDocumentation.TOPIC_TREE_LIST_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = ApiDocumentation.UNAUTHORIZED_401, content = @Content),
            @ApiResponse(responseCode = "403", description = ApiDocumentation.FORBIDDEN_403, content = @Content),
            @ApiResponse(responseCode = "500", description = ApiDocumentation.INTERNAL_ERROR_500, content = @Content)
    })
    @Parameters({
            @Parameter(name = "deviceName", in = ParameterIn.QUERY,
                    description = "Part of the nick name or of the device type name", example = "boiler"),
            @Parameter(name = "serialNumber", in = ParameterIn.QUERY,
                    description = "Part of the serial number of the device", example = "LW-0000000001042"),
            @Parameter(name = "description", in = ParameterIn.QUERY,
                    description = "Part of the description of a topic tree node", example = "temperature"),
            @Parameter(name = "company", in = ParameterIn.QUERY,
                    description = "Identifier of the company the devices must belong to", example = "7")
    })
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    ResponseBody<List<TopicTreeDto>> search(String deviceName, String serialNumber, String description, Long company);

    @Operation(
            summary = "Read the signal topics of a device type",
            description = "Returns the leaf topics of the given device type. When the device type has a hub, the "
                    + "topics of the hub are returned as well (`findHasHubDeviceTypeTopics`), otherwise the whole "
                    + "sub-tree of the device type is read (`findLeafTopics`). The device type is resolved over REST "
                    + "from `device-service`; an unknown device type is reported with the business code 9008. "
                    + "Requires one of the ADMIN or CUSTOMER roles. " + ApiDocumentation.ENVELOPE_NOTE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topics returned; `body` is the list of leaf topics",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TopicTreeDto.class),
                            examples = @ExampleObject(name = "Topics of a device type",
                                    value = ApiDocumentation.TOPIC_TREE_LIST_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = ApiDocumentation.UNAUTHORIZED_401, content = @Content),
            @ApiResponse(responseCode = "403", description = ApiDocumentation.FORBIDDEN_403, content = @Content),
            @ApiResponse(responseCode = "500", description = ApiDocumentation.INTERNAL_ERROR_500, content = @Content)
    })
    @Parameters({
            @Parameter(name = "deviceTypeId", in = ParameterIn.QUERY,
                    description = "Identifier of the device type whose topics are read",
                    required = true, example = "12")
    })
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    ResponseBody<List<TopicTreeDto>> getTopics(Long deviceTypeId);

    @Operation(
            summary = "Read a single topic tree node",
            description = "Returns the node referenced by `id`. This is an internal service-to-service endpoint: "
                    + "`alarm-service` stores only the identifier of the signal (`AlarmDefinition.signalId`) and "
                    + "resolves its topic, unit and description here instead of joining `data_history.topic_tree`. An "
                    + "unknown identifier is reported with the business code 9010. Requires one of the ADMIN, "
                    + "CUSTOMER, MANUFACTURE or AGENCY roles. " + ApiDocumentation.ENVELOPE_NOTE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Node returned; `body` is the topic tree node",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TopicTreeDto.class),
                            examples = @ExampleObject(name = "Topic tree node",
                                    value = ApiDocumentation.TOPIC_TREE_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = ApiDocumentation.UNAUTHORIZED_401, content = @Content),
            @ApiResponse(responseCode = "403", description = ApiDocumentation.FORBIDDEN_403, content = @Content),
            @ApiResponse(responseCode = "500", description = ApiDocumentation.INTERNAL_ERROR_500, content = @Content)
    })
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH,
                    description = "Identifier of the topic tree node", required = true, example = "31")
    })
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    ResponseBody<TopicTreeDto> getTopic(Long id);

    @Operation(
            summary = "Read several topic tree nodes at once",
            description = "Returns the nodes of the given identifiers in one call. This is an internal "
                    + "service-to-service endpoint used by `alarm-service` to resolve a page of signal identifiers "
                    + "without a JPA relation to `data_history.topic_tree`; unknown identifiers are skipped. Requires one "
                    + "of the ADMIN, CUSTOMER, MANUFACTURE or AGENCY roles. " + ApiDocumentation.ENVELOPE_NOTE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nodes returned; `body` is the list of the nodes that "
                    + "exist",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TopicTreeDto.class),
                            examples = @ExampleObject(name = "Topic tree nodes",
                                    value = ApiDocumentation.TOPIC_TREE_LIST_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = ApiDocumentation.UNAUTHORIZED_401, content = @Content),
            @ApiResponse(responseCode = "403", description = ApiDocumentation.FORBIDDEN_403, content = @Content),
            @ApiResponse(responseCode = "500", description = ApiDocumentation.INTERNAL_ERROR_500, content = @Content)
    })
    @Parameters({
            @Parameter(name = "ids", in = ParameterIn.QUERY,
                    description = "Identifiers of the topic tree nodes, repeated or comma separated",
                    required = true, example = "31,32")
    })
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    ResponseBody<List<TopicTreeDto>> getTopicsByIds(List<Long> ids);

    @Operation(
            summary = "Read every root of the topic tree",
            description = "Returns the root nodes whose topic is the name of a device type, without applying the "
                    + "visibility rules of the caller. The device type names are read over REST from "
                    + "`device-service` (`device-type-get-all-names`), so the module never joins the device type "
                    + "table. Requires the ADMIN role. " + ApiDocumentation.ENVELOPE_NOTE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roots returned; `body` is the list of root nodes",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TopicTreeDto.class),
                            examples = @ExampleObject(name = "All roots",
                                    value = ApiDocumentation.TOPIC_TREE_LIST_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = ApiDocumentation.UNAUTHORIZED_401, content = @Content),
            @ApiResponse(responseCode = "403", description = ApiDocumentation.FORBIDDEN_403, content = @Content),
            @ApiResponse(responseCode = "500", description = ApiDocumentation.INTERNAL_ERROR_500, content = @Content)
    })
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    ResponseBody<List<TopicTreeDto>> getAllRoot();

    @Operation(
            summary = "Update the metadata of a topic tree node",
            description = "Updates the description, the unit and the visibility of the node referenced by `id`. "
                    + "`id` and `topic` are mandatory and the topic must match the stored one (business codes 9007, "
                    + "9008 and 9009); changing `isVisible` is cascaded to the direct children of the node. Requires "
                    + "the ADMIN role. " + ApiDocumentation.ENVELOPE_NOTE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Node updated; `body` is the updated node",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TopicTreeDto.class),
                            examples = @ExampleObject(name = "Updated node",
                                    value = ApiDocumentation.TOPIC_TREE_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = ApiDocumentation.UNAUTHORIZED_401, content = @Content),
            @ApiResponse(responseCode = "403", description = ApiDocumentation.FORBIDDEN_403, content = @Content),
            @ApiResponse(responseCode = "500", description = ApiDocumentation.INTERNAL_ERROR_500, content = @Content)
    })
    @RequestBody(
            required = true,
            description = "Node to update. `id` and `topic` are mandatory; `description`, `unit` and "
                    + "`isVisible` are optional.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TopicTreeDto.class),
                    examples = @ExampleObject(name = "Node update",
                            value = ApiDocumentation.TOPIC_TREE_UPDATE_REQUEST_EXAMPLE)))
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    ResponseBody<TopicTreeDto> update(TopicTreeDto view);

    @Operation(
            summary = "Read the current date of the server",
            description = "Returns the current date of the server formatted with the UI date pattern "
                    + "(`yyyy-MM-dd`). The UI uses it to initialise the date pickers of the signal selector. "
                    + "Requires one of the ADMIN, CUSTOMER, MANUFACTURE or AGENCY roles. "
                    + ApiDocumentation.ENVELOPE_NOTE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current date returned",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DateDto.class),
                            examples = @ExampleObject(name = "Current date",
                                    value = ApiDocumentation.DATE_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = ApiDocumentation.UNAUTHORIZED_401, content = @Content),
            @ApiResponse(responseCode = "403", description = ApiDocumentation.FORBIDDEN_403, content = @Content),
            @ApiResponse(responseCode = "500", description = ApiDocumentation.INTERNAL_ERROR_500, content = @Content)
    })
    @SecurityRequirements(value = {
            @SecurityRequirement(name = ACCESS_TOKEN),
            @SecurityRequirement(name = LANGUAGE),
            @SecurityRequirement(name = REALM)
    })
    ResponseBody<DateDto> date();
}
