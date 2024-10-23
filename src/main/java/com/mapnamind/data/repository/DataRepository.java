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

    @Query(value = "select  datetime,value from cmn.fnc_get_topic_data(cast(:START_DATE as TIMESTAMP), cast(:END_DATE as TIMESTAMP), :TOPIC,  cast('jsonb' as text), :PROPERTY) " +
            "WHERE value is not null ORDER BY datetime ASC", nativeQuery = true)
    List<DataProjection> findAllByTopicAndLogTime(@Param("TOPIC") String topic, @Param("START_DATE") String startDate,
                                                  @Param("END_DATE") String endDate, @Param("PROPERTY") String Property);

}
