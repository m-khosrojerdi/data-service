package com.mapnadigital.insights.insights.data.dto;

import com.mapnadigital.insights.insights.common.payload.file.FileManagerResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportRequest implements Serializable {

    private String username;
    private String startDate;
    private String endDate;
    private List<SignalDTO> signalDTOs;
    private Boolean isPersian;

    private String uuid;
    private FileManagerResponse file;
}
