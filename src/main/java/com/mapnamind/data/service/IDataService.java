package com.mapnamind.data.service;

import com.mapnamind.data.dto.DataProjectionDTO;
import com.mapnamind.data.dto.SignalDTO;

import java.util.List;

public interface IDataService {

    List<DataProjectionDTO> aggregate(String startDate, String endDate, String effectiveInterval, SignalDTO signalDTO);
    byte[] export(String startDate, String endDate, List<SignalDTO> signalDTOs, Boolean isPersian);
}
