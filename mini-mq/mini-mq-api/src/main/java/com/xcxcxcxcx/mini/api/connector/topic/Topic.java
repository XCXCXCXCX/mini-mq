package com.xcxcxcxcx.mini.api.connector.topic;

import com.xcxcxcxcx.mini.api.client.Consumer;
import com.xcxcxcxcx.mini.api.client.Partner;
import com.xcxcxcxcx.mini.api.client.Producer;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.spi.router.Router;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * 消息的逻辑分区
 * @author XCXCXCXCX
 * @Since 1.0
 */
public interface Topic {

    /**
     * 在mini-mq有唯一的topic id
     * @return
     */
    String getId();

    /**
     * 当前拥有分区数
     * @return
     */
    int countCurrentPartition();

    /**
     * 增加分区
     * @return
     */
    int addPartitionAndCount();

    /**
     * 减少分区
     * @return
     */
    int removePartitionAndCount();

    /**
     * 每个分区有一个路由器
     * 用于对生产的消息分发到不同partition
     * @return
     */
    Router getRouter();

    /**
     * 该topic的生产者
     * @return
     */
    Map<String, Producer> getProducers();

    /**
     * 该topic的消费者
     * @return
     */
    Map<String, Consumer> getConsumers();

    /**
     * 根据角色配置加入到topic中
     * @param partner {@link Partner}
     * @param roleProperties
     */
    void join(Partner partner, Properties roleProperties);

    /**
     * 发送消息
     * @return
     */
    void sendMessage(List<Message> messages);

    void sendMessage(List<Message> messages, String key);

    /**
     *
     * 获取消息
     * @return
     */
    List<Message> getMessage();

    List<Message> getMessage(String key);
}
