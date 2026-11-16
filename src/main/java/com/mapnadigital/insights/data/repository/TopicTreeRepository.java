package com.mapnadigital.insights.data.repository;

import com.mapnadigital.insights.data.model.entity.TopicTree;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicTreeRepository extends JpaRepository<TopicTree, Long> {

    @Query("SELECT new TopicTree(T, (SELECT COUNT(c) > 0 FROM TopicTree c WHERE c.parent = T)) FROM TopicTree T " +
            "WHERE topic IN (:TOPICS) AND parent IS NULL AND T.isVisible = true")
    List<TopicTree> findAllByTopicAndParentIsNull(@Param("TOPICS") List<String> topics, Sort sort);

    @Query("SELECT new TopicTree(T, (SELECT COUNT(c) > 0 FROM TopicTree c WHERE c.parent = T)) FROM TopicTree T " +
            "WHERE parent.id IN (:PARENT_IDS) AND path NOT LIKE :ALARM AND T.isVisible = true")
    List<TopicTree> findAllByTopicIsDataAndParent(@Param("PARENT_IDS") List<Long> parentIds, @Param("ALARM") String alarm, Sort sort);

    @Query("SELECT new TopicTree(T, (SELECT COUNT(c) > 0 FROM TopicTree c WHERE c.parent = T)) FROM TopicTree T " +
            "WHERE parent.id IN :PARENT_ID AND T.isVisible = true")
    List<TopicTree> findAllByParent(@Param("PARENT_ID") Long parentId);

    @Query("SELECT new TopicTree(T, (SELECT COUNT(c) > 0 FROM TopicTree c WHERE c.parent = T)) FROM TopicTree T WHERE parent.id IN " +
            "( SELECT TT.id FROM TopicTree TT WHERE parent.topic = :DEVICE_TYPE_NAME AND path NOT LIKE :ALARM )")
    List<TopicTree> findAllByDeviceType(@Param("DEVICE_TYPE_NAME") String deviceTpeName, @Param("ALARM") String alarm);

    @Query("SELECT new TopicTree(T, (SELECT COUNT(c) > 0 FROM TopicTree c WHERE c.parent = T)) FROM TopicTree T " +
            "WHERE (T.parent.topic = :DEVICE_TYPE_NAME OR T.topic = :DEVICE_TYPE_NAME) AND T.path LIKE :HUB AND T.isVisible = true")
    List<TopicTree> findHasHubDeviceTypeTopics(@Param("DEVICE_TYPE_NAME") String deviceTypeName, @Param("HUB") String hubName);

    @Query(value = """
            WITH RECURSIVE subtree AS (
                SELECT * FROM data_history.topic_tree WHERE topic = :TOPIC
                UNION ALL
                SELECT n.* FROM data_history.topic_tree n
                JOIN subtree s ON n.parent_id = s.id
                WHERE n.path NOT LIKE '%/alarm%'
            )
            SELECT * FROM subtree
            """, nativeQuery = true)
    List<TopicTree> findLeafTopics(@Param("TOPIC") String deviceTypeName);

    List<TopicTree> findAllByParentIsNull();

    List<TopicTree> findAllByParentIsNotNull();

    @Query("SELECT new TopicTree(T, (SELECT COUNT(c) > 0 FROM TopicTree c WHERE c.parent = T)) FROM TopicTree T " +
            "WHERE T.description LIKE %:DESCRIPTION% AND T.isVisible = true")
    List<TopicTree> findByDescription(@Param("DESCRIPTION") String description);

    Optional<TopicTree> findByPath(String path);

    /**
     * Reads the root topics whose topic is the name of a device type.
     * <p>
     * The device types belong to {@code device-service}, so the caller resolves their names over REST and passes
     * them here; the query never sub-selects the device type table of the other service.
     *
     * @param deviceTypeNames the names of the device types
     * @return the root topics of the given device types
     */
    @Query("SELECT T FROM TopicTree T WHERE T.parent IS NULL AND T.topic IN :DEVICE_TYPE_NAMES")
    List<TopicTree> findAllRoot(@Param("DEVICE_TYPE_NAMES") List<String> deviceTypeNames);

}
