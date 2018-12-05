package com.xcxcxcxcx.mini.common.handler;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.topic.Topic;
import com.xcxcxcxcx.mini.common.message.entity.Pull;
import com.xcxcxcxcx.mini.common.message.entity.PullResult;
import com.xcxcxcxcx.mini.common.message.wrapper.PullPacketWrapper;
import com.xcxcxcxcx.mini.common.topic.BrokerContext;

import java.util.List;

/**
 *
 * pull消息处理器
 * @author XCXCXCXCX
 * @since 1.0
 */
public class PullHandler extends BaseHandler{

    @Override
    public Object doHandle(Packet packet, Connection connection) {

        //1.触发异步预读
        // 局部性原理：默认认为当前读到数据附近的数据也会被读到。

        //2.从topic直接取消息
        Pull pull = new PullPacketWrapper(connection, packet).get();
        String topicId = pull.topicId;
        String key = pull.key;

        Topic topic = BrokerContext.getTopicById(topicId);
        List<Message> messages = null;
        if(key == null){
            messages = topic.getMessage();
        }else{
            messages = topic.getMessage(key);
        }
        PullResult pullResult = new PullResult();
        pullResult.messages = messages;

        return pullResult;
    }
}
