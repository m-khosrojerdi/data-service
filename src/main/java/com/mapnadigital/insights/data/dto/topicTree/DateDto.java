package com.mapnadigital.insights.data.dto.topicTree;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Current date of the server, used by the UI to initialise its date pickers.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Date", description = "Current date of the server in the UI date format.")
public class DateDto implements Serializable {

    @Schema(description = "Current date of the server", example = "2026-11-08")
    private String date;

}
