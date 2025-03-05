package com.mapnamind.data.service;

import com.mapnamind.data.dto.DataProjectionDTO;
import com.mapnamind.data.dto.SignalDTO;

import java.util.List;

public interface IDataService {

    List<DataProjectionDTO> findAllByTopicAndLogTime(String startDate, String endDate, SignalDTO signalDTO);
    byte[] export(String startDate, String endDate, List<SignalDTO> signalDTOs);
}
