package com.mapnadigital.insights.data.api;

import com.google.gson.reflect.TypeToken;
import com.mapnadigital.insights.data.api.dto.DeviceTypeName;
import com.mapnadigital.insights.web.api.ApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Reads device types of {@code device-service} over REST.
 * <p>
 * Device types belong to {@code device-service}, so the names used to resolve the roots of the topic tree are read
 * here with one call instead of a JPA sub-select on the {@code DeviceType} entity.
 *
 * @author Mohammad Khosrojerdi khosrojerdi.mohammad@gmail.com
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceTypeClient {

    private final ApiService apiService;

    /**
     * Resolves the names of every device type.
     *
     * @return the distinct names of the device types; an empty list when device-service cannot be reached
     */
    public List<String> findAllNames() {
        try {
            List<DeviceTypeName> deviceTypes = apiService.get(ApiClient.DEVICE_TYPE_GET_ALL_NAMES,
                    new TypeToken<List<DeviceTypeName>>() {
                    }.getType());
            if (deviceTypes == null) {
                return List.of();
            }
            return deviceTypes.stream()
                    .map(DeviceTypeName::getName)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
        } catch (Exception e) {
            log.warn("could not read the device type names of device-service: {}", e.getMessage());
            return List.of();
        }
    }

}
