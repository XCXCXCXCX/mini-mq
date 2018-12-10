package com.xcxcxcxcx.persistence.db.mybatis.mapper;

import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.spi.persistence.MessageStatusEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface MessageStatusMapper {

    void batchInsert(@Param("messageStatusEntities") List<MessageStatusEntity> messageStatusEntities);

    void batchUpdate(@Param("ids") List<Long> ids,
                     @Param("consumerGroupId") String consumerGroupId,
                     @Param("maxPulledTimes") Integer maxPulledTimes,
                     @Param("expired") Long expired);

    /**
     * 用于查询被成功确认的消息
     * @param ids
     * @param consumerGroupId
     * @return
     */
    List<Long> queryByIdsAndGroup(@Param("ids") List<Long> ids,
                                  @Param("consumerGroupId") String consumerGroupId,
                                  @Param("status") Integer status);

    /**
     * 查询未被group pull过的消息ID
     * @param topicId
     * @param consumerGroupId
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Long> queryAbsent(@Param("topicId") String topicId,
                           @Param("consumerGroupId") String consumerGroupId,
                           @Param("pageNum") Integer pageNum,
                           @Param("pageSize") Integer pageSize);

    /**
     * 查询被group pull过的消息ID
     * @param topicId
     * @param consumerGroupId
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Message> queryNotAbsent(@Param("topicId") String topicId,
                                 @Param("consumerGroupId") String consumerGroupId,
                                 @Param("pageNum") Integer pageNum,
                                 @Param("pageSize") Integer pageSize);

    void batchAckPull(@Param("ids") List<Long> ids,
                      @Param("consumerGroupId") String consumerGroupId);

    void batchRejectPull(@Param("ids") List<Long> ids,
                         @Param("consumerGroupId") String consumerGroupId);
}
