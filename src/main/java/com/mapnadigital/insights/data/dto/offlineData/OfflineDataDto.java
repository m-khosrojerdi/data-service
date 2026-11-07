package com.mapnadigital.insights.data.dto.offlineData;

import com.mapnadigital.insights.common.annotation.Date;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfflineDataDto implements Serializable {

    @NotBlank(message = "view.offlineData.topic.notBlank")
    private String topic;

    @NotBlank(message = "view.offlineData.payload.notBlank")
    private String payload;

    @Date(message = "view.offlineData.date.notBlank")
    private String date;

}
