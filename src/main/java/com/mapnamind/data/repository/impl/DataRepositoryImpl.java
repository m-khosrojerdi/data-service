package com.mapnamind.data.repository.impl;

import com.mapnamind.common.model.Data;
import com.mapnamind.common.util.Toolbox;
import com.mapnamind.data.repository.DataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Repository
public class DataRepositoryImpl implements DataRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcCall addData;

    @Autowired
    public void setDataSource() {
        this.addData = new SimpleJdbcCall(jdbcTemplate).withSchemaName("cmn").withFunctionName("fnc_save_data");
    }

    @Override
    public Boolean save(Data entity) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_topic", entity.getTopic())
                .addValue("p_value", entity.getValue())
                .addValue("p_datetime", entity.getDatetime());
        try {
            String value = addData.executeFunction(String.class, in);

            if (value.equalsIgnoreCase("success")) {
                return true;
            }
        } catch (Exception e) {
//            log.warn(e.getMessage(), e);
        }
        return false;
    }

    public List<Data> findAllByTopicAndStartAndEndOfDatetimeWithEpoch(String topic, Date startDate, Date endDate, String isProperty) {
        try {
            isProperty = isProperty == null || isProperty.equals("") ? "''" : "'" + isProperty + "'";
            String sql = "select extract(epoch from datetime) * 1000 datetime, value from cmn.fnc_get_topic_data('" + Toolbox.DATE_FORMAT.format(startDate) +
                    "', '" + Toolbox.DATE_FORMAT.format(endDate) + "', '" + topic + "', " + isProperty + ") " +
                    "where value is not null order by datetime asc;";
            return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Data.class));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Data> findAllByTopicAndStartAndEndOfDatetimeWithoutEpoch(String topic, Date startDate, Date endDate, String isProperty) {
        try {
            isProperty = isProperty == null || isProperty.equals("") ? "''" : "'" + isProperty + "'";
            String sql = "select * from cmn.fnc_get_topic_data('" + Toolbox.DATE_FORMAT.format(startDate) +
                    "', '" + Toolbox.DATE_FORMAT.format(endDate) + "', '" + topic + "', '1'::DOUBLE PRECISION, " + isProperty + ") " +
                    "where value is not null order by datetime asc;";
            return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Data.class));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Data> findAllByTopicAndStartAndEndOfDatetimeWithEpoch(String topic, Date startDate, Date endDate, String isProperty, Integer pageSize, Integer pageNumber) {
        try {
            isProperty = isProperty == null || isProperty.equals("") ? "''" : "'" + isProperty + "'";
            int offset = (pageNumber - 1) * pageSize;
            String sql = "select * from cmn.fnc_get_topic_data('" + Toolbox.DATE_FORMAT.format(startDate) +
                    "', '" + Toolbox.DATE_FORMAT.format(endDate) + "', '" + topic + "', '1'::DOUBLE PRECISION, " +
                    isProperty + ", '" + pageSize + "', '" + offset + "') where value is not null order by datetime asc;";
            return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Data.class));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer totalRecord(String topic, Date startDate, Date endDate, String isProperty) {
        try {
            isProperty = isProperty == null || isProperty.equals("") ? "''" : "'" + isProperty + "'";
            String sql = "select count(*) from cmn.fnc_get_topic_data('" + Toolbox.DATE_FORMAT.format(startDate) +
                    "', '" + Toolbox.DATE_FORMAT.format(endDate) + "', '" + topic + "', '1'::DOUBLE PRECISION, " +
                    isProperty + ") where value is not null;";
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
