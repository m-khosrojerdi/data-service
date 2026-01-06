package com.mapnamind.data.repository;

import com.mapnamind.common.model.entity.TopicTree;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicTreeRepository extends JpaRepository<TopicTree, Long> {

    @Query("SELECT T FROM TopicTree T WHERE topic IN (:TOPICS) AND parent IS NULL AND T.isVisible = true")
    List<TopicTree> findAllByTopicAndParentIsNull(@Param("TOPICS") List<String> topics, Sort sort);

    @Query("SELECT T FROM TopicTree T WHERE parent.id IN (:PARENT_IDS) AND path NOT LIKE :ALARM AND T.isVisible = true")
    List<TopicTree> findAllByTopicIsDataAndParent(@Param("PARENT_IDS") List<Long> parentIds, @Param("ALARM") String alarm, Sort sort);

    @Query("SELECT T FROM TopicTree T WHERE parent.id IN :PARENT_ID AND T.isVisible = true")
    List<TopicTree> findAllByParent(@Param("PARENT_ID") Long parentId);

    @Query("SELECT T FROM TopicTree T WHERE parent.id IN " +
            "( SELECT TT.id FROM TopicTree TT WHERE parent.topic = :DEVICE_TYPE_NAME AND path NOT LIKE :ALARM )")
    List<TopicTree> findAllByDeviceType(@Param("DEVICE_TYPE_NAME") String deviceTpeName, @Param("ALARM") String alarm);

    @Query("SELECT T FROM TopicTree T WHERE (T.parent.topic = :DEVICE_TYPE_NAME OR T.topic = :DEVICE_TYPE_NAME) " +
            "AND T.path LIKE :HUB AND T.isVisible = true")
    List<TopicTree> findHasHubDeviceTypeTopics(@Param("DEVICE_TYPE_NAME") String deviceTypeName, @Param("HUB") String hubName);

    @Query(value = """
            WITH RECURSIVE subtree AS (
                SELECT * FROM topic_tree WHERE topic = :TOPIC
                UNION ALL
                SELECT n.* FROM topic_tree n
                JOIN subtree s ON n.parent_id = s.id
                WHERE n.path NOT LIKE '%/alarm%'
            )
            SELECT * FROM subtree
            """, nativeQuery = true)
    List<TopicTree> findLeafTopics(@Param("TOPIC") String deviceTypeName);

    List<TopicTree> findAllByParentIsNull();

    List<TopicTree> findAllByParentIsNotNull();

    @Query("SELECT T FROM TopicTree T WHERE T.description LIKE %:DESCRIPTION% AND T.isVisible = true")
    List<TopicTree> findByDescription(@Param("DESCRIPTION") String description);

    Optional<TopicTree> findByPath(String path);
}
