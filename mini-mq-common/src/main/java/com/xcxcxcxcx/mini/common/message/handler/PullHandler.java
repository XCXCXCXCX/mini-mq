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
import com.xcxcxcxcx.mini.api.spi.executor.ExecutorFactory;
import com.xcxcxcxcx.mini.common.message.wrapper.PullPacketWrapper;
import com.xcxcxcxcx.mini.common.topic.BrokerContext;
import com.xcxcxcxcx.mini.tools.log.LogUtils;
import com.xcxcxcxcx.mini.tools.thread.ThreadManager;
import com.xcxcxcxcx.persistence.db.persistence.DbFactory;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * pull消息处理器
 *
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PullHandler extends BaseHandler{

    private static final long MAX_PREPULL_TIME_LIMIT = 10 * 1000;

    private static final PersistenceMapper persistenceMapper = DbFactory.getMapper();

    private static final Executor executor = ExecutorFactory.create().get("Asyn-pull-to-memory-executor");

    private AtomicBoolean pullRequest = new AtomicBoolean(false);

    private static class AsynPullToMemoryTask implements AutoPullToMemoryTask{

        private final AtomicBoolean pullRequest;

        private final String topicId;

        private final String groupId;

        private final String key;

        private final int num;

        public AsynPullToMemoryTask(AtomicBoolean pullRequest, String topicId, String groupId, String key, int num) {
            this.pullRequest = pullRequest;
            this.topicId = topicId;
            this.groupId = groupId;
            this.key = key;
            this.num = num;
        }

        @Override
        public Boolean autoPullCondition() {
            return pullRequest.compareAndSet(false, true);
        }

        @Override
        public void doPullToMemory() {
            List<Message> messages =
                    persistenceMapper.
                            prePullIfAbsent(topicId, groupId, key,1, 2 * num);
            if(messages != null){
                if(key == null){
                    BrokerContext.sendMessage(topicId, messages);
                }else{
                    BrokerContext.sendMessage(key, topicId, messages);
                }
            }
            pullRequest.set(false);
        }

        @Override
        public void run() {
            if (autoPullCondition()) {
                doPullToMemory();
            }
        }
    }

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
            executor.execute(new AsynPullToMemoryTask(pullRequest,
                    topicId,groupId, key, num));
        }

        long lastTime = System.currentTimeMillis();
        while(BrokerContext.getMessageSum(groupId, topicId) == 0
                && System.currentTimeMillis() - lastTime < MAX_PREPULL_TIME_LIMIT){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
                && BrokerContext.getSubscribeNum(groupId, topicId) > 0
                && BrokerContext.getMessageSum(groupId, topicId) < 2 * num){
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
