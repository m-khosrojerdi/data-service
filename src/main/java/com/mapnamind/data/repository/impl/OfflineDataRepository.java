package com.mapnamind.data.repository.impl;

import com.mapnamind.common.execption.ServiceException;
import com.mapnamind.data.dto.OfflineDataDTO;
import com.mapnamind.data.repository.IOfflineDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import static com.mapnamind.common.util.Toolbox.DATE_FORMAT;

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
            callableStatement.setString(3, DATE_FORMAT.format(dto.getDate()));// "p_datetime"

            callableStatement.execute();
        } catch (SQLException e) {
            throw new ServiceException("repository.offlineData.save.failed.9001");
        }
    }
}
