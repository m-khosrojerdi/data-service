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

    @Query(value = "select dateandtime as datetime, aggr_value as value\n" +
            "from cmn.fnc_get_aggr_data(:TOPIC,\n" +
            "                           cast(:START_DATE as timestamp),\n" +
            "                           cast(:END_DATE as timestamp),\n" +
            "                           :AGGREGATION,\n" +
            "                           :INTERVAL,\n" +
            "                           :PROPERTY)", nativeQuery = true)
    List<DataProjection> aggregate(@Param("TOPIC") String topic, @Param("START_DATE") String startDate,
                                   @Param("END_DATE") String endDate, @Param("PROPERTY") String Property,
                                   @Param("INTERVAL") String interval, @Param("AGGREGATION") String aggregation);

}
