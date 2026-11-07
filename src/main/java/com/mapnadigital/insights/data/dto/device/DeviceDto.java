package com.mapnadigital.insights.data.dto.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDto implements Serializable {

    private Long id;
    private String serialNumber;
    private String password;
    private Long deviceTypeId;
    private String deviceTypeName;
    private Long deviceModelId;
    private String deviceModelName;
    private Long hubId;
    private String hubSerialNumber;
    private String hubDeviceTypeName;
    private String dateOfRegistration;
    private Boolean hasLocation;
    private String type;
    private Long company;

}
