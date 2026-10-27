package com.mapnadigital.insights.insights.data.view;

import com.mapnadigital.insights.insights.common.payload.file.FileManagerResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Signal-selector export request. It is the controller request body and the
 * ActiveMQ message payload, replacing the former {@code ExportRequestView} DTO.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportRequestView implements Serializable {

    private String username;
    private String startDate;
    private String endDate;
    private List<SignalView> signals;
    private Boolean isPersian;
    private String uuid;
    private FileManagerResponse file;
}
