package com.mapnadigital.insights.data.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Read model of a device type name received from {@code device-service}.
 * <p>
 * It is a plain payload with no persistence meaning: the topic tree only needs the names of the device types to
 * resolve its roots, so the identifier and the descriptive fields of the device type are not carried.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTypeName implements Serializable {

    /**
     * Name of the device type.
     */
    private String name;

}
