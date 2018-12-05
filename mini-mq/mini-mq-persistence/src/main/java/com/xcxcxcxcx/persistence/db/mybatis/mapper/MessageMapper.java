package com.xcxcxcxcx.persistence.db.mybatis.mapper;

import com.xcxcxcxcx.mini.api.connector.message.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface MessageMapper {

    void insertSelective(Message message);

    void batchDelete(@Param("messageIds") List<Long> messageIds,
                     @Param("topicId") String topicId,
                     @Param("status") Integer status,
                     @Param("pulledTimes") Integer pulledTimes,
                     @Param("expired") Long expired);

    void updateSelective(Message message);

    List<Message> query(@Param("message") Message message, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);
}
