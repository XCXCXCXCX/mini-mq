package com.xcxcxcxcx.mini.common.message.handler;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.AutoPullToMemoryTask;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.PullResult;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.api.persistence.PersistenceMapper;
import com.xcxcxcxcx.mini.api.connector.message.entity.Pull;
import com.xcxcxcxcx.mini.common.message.wrapper.PullPacketWrapper;
import com.xcxcxcxcx.mini.common.topic.BrokerContext;
import com.xcxcxcxcx.mini.tools.log.LogUtils;
import com.xcxcxcxcx.mini.tools.thread.ThreadManager;
import com.xcxcxcxcx.persistence.db.persistence.DbFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * pull消息处理器
 *
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PullHandler extends BaseHandler {

    private PersistenceMapper persistenceMapper = DbFactory.getMapper();

    private AtomicBoolean pullRequest = new AtomicBoolean(false);


    @Override
    public Object doHandle(Packet packet, Connection connection) {

        Pull pull = new PullPacketWrapper(connection, packet).get();
        String topicId = connection.getSessionContext().getTopicId();
        String groupId = connection.getSessionContext().getId();
        String key = pull.key;
        int num = pull.num;

        //1.触发异步预读
        // 局部性原理：默认认为当前读到数据附近的数据也会被读到。
        /**
         * 发起预读的异步请求，将消息预读到topic
         *
         * 1.存在当前topic
         * 2.当前消费组中的该topic目前存在消费者
         * 3.当前消费组中的该topic当前存在在内存中的消息少于2 * num
         * @return
         */
        if(prePullCondition(topicId, groupId, num)){
            ThreadManager.newThread(new AutoPullToMemoryTask() {
                @Override
                public Boolean autoPullCondition() {
                    return pullRequest.compareAndSet(false, true);
                }

                @Override
                public void doPullToMemory() {
                    List<Message> messages =
                            persistenceMapper.
                                    prePullIfAbsent(topicId, groupId, key,1, 2 * num);
                    if(key == null){
                        BrokerContext.sendMessage(topicId, messages);
                    }else{
                        BrokerContext.sendMessage(key, topicId, messages);
                    }
                    pullRequest.compareAndSet(true, false);
                }

                @Override
                public void run() {
                    if (autoPullCondition()) {
                        doPullToMemory();
                    }
                }
            }, "pullHandler-asyn-prePull");
        }

        //2.从topic直接取消息
        List<Message> messages = null;
        if (key == null) {
            messages = BrokerContext.getMessage(groupId, topicId);
        } else {
            messages = BrokerContext.getMessage(groupId, topicId, key);
        }
        PullResult pullResult = new PullResult();
        pullResult.messages = messages;

        return new Packet(Command.PULL_RESPONSE, pullResult);
    }

    /**
     * 1.存在当前topic
     * 2.当前消费组中的该topic目前存在消费者
     * 3.当前消费组中的该topic当前存在在内存中的消息少于2 * num
     * @return
     */
    private boolean prePullCondition(String topicId, String groupId, int num) {
        if(BrokerContext.isExist(topicId)
                && BrokerContext.getSubscribeNum(topicId, groupId) > 0
                && BrokerContext.getMessageSum(topicId, groupId) < 2 * num){
            return true;
        }
        return false;
    }

    @Override
    public void reply(Object result, Connection connection) {
        connection.send((Packet) result, future -> {
            if(future.isSuccess()){
                LogUtils.handler.info("reply pull response packet success : " + result.toString());
            }else{
                LogUtils.handler.info("reply pull response packet failed : " + result.toString());
            }
        });
    }
}
