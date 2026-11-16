package com.mapnadigital.insights.data.dto.data;

import com.mapnadigital.insights.common.payload.file.FileManagerResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Signal-selector export request. It is the controller request body and the
 * ActiveMQ message payload, replacing the former {@code ExportRequestDto} DTO.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExportRequest", description = "Internal payload of the asynchronous signal-selector export. It is "
        + "queued on ActiveMQ and never accepted from a REST client.")
public class ExportRequestDto implements Serializable {

    @Schema(description = "User that requested the export; the result is pushed to this user")
    private String username;

    @Schema(description = "Start of the exported period", example = "2026-11-01 00:00:00")
    private String startDate;

    @Schema(description = "End of the exported period", example = "2026-11-02 00:00:00")
    private String endDate;

    @Schema(description = "Signals to export")
    private List<SignalDto> signals;

    @Schema(description = "Whether the dates of the file are written in the Jalali calendar")
    private Boolean isPersian;

    @Schema(description = "Identifier of the export, echoed to the client")
    private String uuid;

    @Schema(description = "Uploaded file, filled in by the worker")
    private FileManagerResponse file;

    /**
     * JWT of the user that requested the export. It is captured in the request thread and
     * replayed by the asynchronous consumer when it publishes the result through
     * {@code notification-service}, so the internal publishing API stays authenticated.
     */
    @Schema(description = "JWT of the requesting user, replayed by the worker", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String token;
}
