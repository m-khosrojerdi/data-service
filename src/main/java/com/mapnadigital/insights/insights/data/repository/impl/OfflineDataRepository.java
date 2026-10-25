package com.mapnadigital.insights.insights.data.repository.impl;

import com.mapnadigital.insights.insights.common.execption.ServiceException;
import com.mapnadigital.insights.insights.common.util.DateTimeUtil;
import com.mapnadigital.insights.insights.data.dto.OfflineDataDTO;
import com.mapnadigital.insights.insights.data.repository.IOfflineDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;

import static com.mapnadigital.insights.insights.common.util.Toolbox.DATE_FORMAT;

@Repository
@RequiredArgsConstructor
public class OfflineDataRepository implements IOfflineDataRepository {

    private final DataSource dataSource;

    @Override
    public void save(OfflineDataDTO dto) {
        try {
            Connection connection = dataSource.getConnection();
            String sql = "{ CALL cmn.fnc_save_data(?::text, ?::text, ?::timestamp with time zone) }";
            CallableStatement callableStatement = connection.prepareCall(sql);
            callableStatement.setString(1, dto.getTopic());// "p_topic"
            callableStatement.setString(2, dto.getPayload());// "p_value"
            callableStatement.setString(3, DATE_FORMAT.format(Date.from(DateTimeUtil.toDateTime(dto.getDate())
                            .atZone(ZoneId.systemDefault()).toInstant()))
            );// "p_datetime"

            callableStatement.execute();
        } catch (SQLException e) {
            throw new ServiceException("repository.offlineData.save.failed.9001");
        }
    }
}
