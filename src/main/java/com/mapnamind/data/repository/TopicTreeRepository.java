package com.mapnamind.data.repository;

import com.mapnamind.common.model.entity.TopicTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicTreeRepository extends JpaRepository<TopicTree, Long> {

    @Query("SELECT T FROM TopicTree T WHERE topic IN (:TOPICS) AND parent IS NULL")
    List<TopicTree> findAllByTopicAndParentIsNull(@Param("TOPICS") List<String> topics);

    @Query("SELECT T FROM TopicTree T WHERE parent.id IN (:PARENT_IDS) AND path NOT LIKE :ALARM")
    List<TopicTree> findAllByTopicIsDataAndParent(@Param("PARENT_IDS") List<Long> parentIds, @Param("ALARM") String alarm);

    @Query("SELECT T FROM TopicTree T WHERE parent.id IN :PARENT_ID")
    List<TopicTree> findAllByParent(@Param("PARENT_ID") Long parentId);

    List<TopicTree> findAllByParentIsNull();

    List<TopicTree> findAllByParentIsNotNull();

    TopicTree findByDescription(String description);

}
