package com.xcxcxcxcx.persistence.db.mybatis.mapper;

import com.xcxcxcxcx.mini.api.spi.persistence.PersistenceService;
import com.xcxcxcxcx.mini.api.spi.persistence.TopicEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface TopicMapper {

    int createTopic(@Param("topicId") String topicId, @Param("partitionNum") Integer partitionNum);

    int removeTopic(@Param("topicId") String topicId);

    List<TopicEntity> getAllTopic();

}
