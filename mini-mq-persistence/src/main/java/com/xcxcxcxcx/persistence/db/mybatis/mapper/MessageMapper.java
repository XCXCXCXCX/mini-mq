package com.xcxcxcxcx.persistence.db.mybatis.mapper;


import com.xcxcxcxcx.mini.api.connector.message.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface MessageMapper {

    void batchPush(@Param("messageList") List<Message> messageList);

    void batchAckPush(@Param("ids") List<Long> ids);

    void batchRejectPush(@Param("ids") List<Long> ids);

    List<Long> queryIdById(@Param("ids")List<Long> ids,
                           @Param("status") Integer status);

    List<Message> queryById(@Param("ids")List<Long> ids,
                            @Param("status")Integer status);

    void cleanExpired();

    void deleteByTopicId(@Param("topicId")String topicId);
}
