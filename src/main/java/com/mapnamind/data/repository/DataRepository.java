package com.mapnamind.data.repository;

import com.mapnamind.common.model.entity.Data;
import com.mapnamind.common.model.view.DataProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataRepository extends JpaRepository<Data, Long> {

    @Query(value = "with flagged_data as (\n" +
            "    select\n" +
            "        datetime,\n" +
            "        cast(value as double precision) as value,\n" +
            "        cast(lag(value) over (order by datetime) as double precision) as prev_value,\n" +
            "        cast(lead(value) over (order by datetime) as double precision) as next_value,\n" +
            "        cast(extract(epoch from datetime) as double precision) as dt,\n" +
            "        cast(extract(epoch from LAG(datetime) OVER (order by datetime)) as double precision) as previous_dt,\n" +
            "        min(datetime) over() as min_time,\n" +
            "        max(datetime) over() as max_time\n" +
            "    from cmn.fnc_get_topic_data(cast(:START_DATE as timestamp),\n" +
            "                                cast(:END_DATE as timestamp),\n" +
            "                                :TOPIC,\n" +
            "                                cast('jsonb' as text),\n" +
            "                                :PROPERTY)\n" +
            "),\n" +
            "    sampled_data as (\n" +
            "        (select\n" +
            "            datetime,\n" +
            "            value\n" +
            "        from flagged_data\n" +
            "        where value != prev_value or value != next_value\n" +
            "        order by abs((value - prev_value) / (dt - previous_dt)) desc\n" +
            "        limit :SAMPLE_COUNT)\n" +
            "        union\n" +
            "        select datetime, value from flagged_data where datetime in (min_time, max_time)\n" +
            "    )\n" +
            "select datetime,\n" +
            "       value,\n" +
            "       coalesce(value - lag(value) over (order by datetime), 0) as valueChanges\n" +
            "from sampled_data\n" +
            "order by datetime", nativeQuery = true)
    List<DataProjection> findAllByTopicAndLogTime(@Param("TOPIC") String topic, @Param("START_DATE") String startDate,
                                                  @Param("END_DATE") String endDate, @Param("PROPERTY") String Property,
                                                  @Param("SAMPLE_COUNT") Integer sampleCount);

    @Query(value = "select dateandtime as datetime, aggr_value as value\n" +
            "from cmn.fnc_get_aggr_data(:TOPIC,\n" +
            "                           cast(:START_DATE as timestamp),\n" +
            "                           cast(:END_DATE as timestamp),\n" +
            "                           'delta',\n" +
            "                           :INTERVAL,\n" +
            "                           :PROPERTY)", nativeQuery = true)
    List<DataProjection> delta(@Param("TOPIC") String topic, @Param("START_DATE") String startDate,
                               @Param("END_DATE") String endDate, @Param("PROPERTY") String Property,
                               @Param("INTERVAL") String interval);

}
