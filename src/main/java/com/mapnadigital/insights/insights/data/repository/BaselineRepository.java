package com.mapnadigital.insights.insights.data.repository;

import com.mapnadigital.insights.insights.common.model.entity.Baseline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaselineRepository extends JpaRepository<Baseline, Long> {

    Optional<Baseline> findByDeviceIdAndTopicTreeIdAndInterval(Long deviceId, Long topicTreeId, String interval);
}
