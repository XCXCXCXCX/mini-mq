package com.xcxcxcxcx.client.connector.channel;

import com.xcxcxcxcx.client.config.RoleConfig;
import com.xcxcxcxcx.client.connector.message.DefaultClientPacketDispatcher;
import com.xcxcxcxcx.client.connector.message.handler.*;
import com.xcxcxcxcx.client.connector.task.ScheduledSettleTask;
import com.xcxcxcxcx.client.storage.abs.MessageInfo;
import com.xcxcxcxcx.client.storage.abs.MessageStorage;
import com.xcxcxcxcx.client.storage.cache.CacheCenter;
import com.xcxcxcxcx.mini.api.client.Partner;
import com.xcxcxcxcx.mini.api.client.ResponseReceiver;
import com.xcxcxcxcx.mini.api.client.Role;
import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.connection.ConnectionFactory;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.PacketDispatcher;
import com.xcxcxcxcx.mini.api.connector.message.entity.*;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.tools.thread.ThreadManager;
import com.xcxcxcxcx.network.connection.NettyConnection;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class ClientChannelHandler extends ChannelInboundHandlerAdapter implements ResponseReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientChannelHandler.class);

    /**
     * 最大pending消息数量限制，1000个
     */
    private static final int MAX_PENDING_NUM_LIMIT = 1000;

    /**
     * 最大pending时间限制，1秒
     */
    private static final long MAX_PENDING_TIME_LIMIT = 1000;

    /**
     * prefetch的最大容量默认为1000条
     */
    private static final int MAX_PREFETCH_CAPACITY = 1000;

    /**
     * 一次prefetch的最大消息数量
     */
    private static final int ONE_PREFETCH_MAX_NUM = 100;

    /**
     * 最大send失败重试次数
     */
    private static final int MAX_FAIL_RETRY = 3;

    /**
     * 默认消息超时时间是5分钟
     */
    private static final int MESSAGE_EXPIRED_TIME = 5 * 60 * 1000;

    private int max_pending_num_limit = MAX_PENDING_NUM_LIMIT;
    private long max_pending_time_limit = MAX_PENDING_TIME_LIMIT;
    private int max_prefetch_capacity = MAX_PREFETCH_CAPACITY;
    private int max_fail_retry = MAX_FAIL_RETRY;
    private int one_prefetch_max_num = ONE_PREFETCH_MAX_NUM;
    private long message_expired_time = MESSAGE_EXPIRED_TIME;

    private final Queue<Message> pendingHandleMessage = new ConcurrentLinkedQueue<>();
    private final Queue<Message> pendingSend = new ConcurrentLinkedQueue<>();
    private final Queue<IdAndAck> pendingSendAck = new ConcurrentLinkedQueue<>();
    private final Queue<IdAndAck> pendingReceiveAck = new ConcurrentLinkedQueue<>();

    private volatile CompletableFuture<Boolean> currentSendFuture;
    private volatile CompletableFuture<Boolean> currentSendAckFuture;
    private volatile CompletableFuture<Boolean> currentReceiveAckFuture;

    private static final AtomicLong pendingSendStart = new AtomicLong(0);
    private static final AtomicLong pendingSendAckStart = new AtomicLong(0);
    private static final AtomicLong pendingReceiveAckStart = new AtomicLong(0);

    /**
     * 本地消息信息存储
     */
    private final MessageStorage messageStorage = new CacheCenter();

    /**
     * 保存请求的回调
     */
    private final Map<Integer, CompletableFuture<Boolean>> requestCallback = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduleExecutor;

    private final ConnectionFactory connectionFactory;

    private final PacketDispatcher packetDispatcher;

    private Connection connection;

    private final String roleName;

    private final String id;

    private final String topicId;

    private final String key;

    private boolean isPendingPush;
    private boolean isPendingPushAckOrReject;
    private boolean isPendingPullAckOrReject;
    private boolean isPrefetching;

    public ClientChannelHandler(Partner partner) {
        roleName = partner.getRole().getRoleName();
        id = partner.getId();
        topicId = partner.getTopicId();
        key = partner.getKey();
        resolveProperties(partner.getRole().getConfig());

        connectionFactory = NettyConnection::new;

        /**
         * packetDispatcher初始化
         */
        this.packetDispatcher = new DefaultClientPacketDispatcher();
        packetDispatcher.register(Command.HEARTBEAT, new BaseHandler() {
            @Override
            public void reply(Object result, Connection connection) {
                //do nothing
            }

            @Override
            public Object doHandle(Packet packet, Connection connection) {
                LOGGER.info("receive heartbeat packet , {}", packet);
                return null;
            }
        });
        packetDispatcher.register(Command.PUSH_RESPONSE, new PushResponseHandler(this));
        packetDispatcher.register(Command.PULL_RESPONSE, new PullResponseHandler(pendingHandleMessage, isPrefetching, this));
        packetDispatcher.register(Command.PUSH_ACK_RESPONSE, new PushAckResponseHandler(this));
        packetDispatcher.register(Command.PULL_ACK_RESPONSE, new PullAckResponseHandler(this));
        packetDispatcher.register(Command.HAND_SHAKE_OK, new ClientHandshakeOKHandler());

        scheduleExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduleExecutor.submit(new ScheduledSettleTask(messageStorage, connection, scheduleExecutor));

    }

    private void resolveProperties(Map<String, Object> props) {
        Object max_pending_num_limit = props.get(RoleConfig.max_pending_num_limit);
        Object max_pending_time_limit = props.get(RoleConfig.max_pending_time_limit);
        Object max_prefetch_capacity = props.get(RoleConfig.max_prefetch_capacity);
        Object one_prefetch_max_num = props.get(RoleConfig.one_prefetch_max_num);
        Object message_expired_time = props.get(RoleConfig.message_expired_time);
        if(max_pending_num_limit != null){
            this.max_pending_num_limit = (Integer)max_pending_num_limit;
        }
        if(max_pending_time_limit != null){
            this.max_pending_time_limit = (Long)max_pending_time_limit;
        }
        if(max_prefetch_capacity != null){
            this.max_prefetch_capacity = (Integer)max_prefetch_capacity;
        }
        if(one_prefetch_max_num != null){
            this.one_prefetch_max_num = (Integer)one_prefetch_max_num;
        }
        if(one_prefetch_max_num != null){
            this.message_expired_time = (Long)message_expired_time;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        connection = connectionFactory.create(ctx.channel());

        /**
         * 发起第一次握手
         */
        Handshake handshake = new Handshake();
        handshake.roleName = this.roleName;
        handshake.id = this.id;
        handshake.topicId = this.topicId;
        connection.send(new Packet(Command.HAND_SHAKE, handshake));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        connection.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(((Packet) msg).isValid()){
            packetDispatcher.dispatch((Packet) msg, connection);
        }else{
            LOGGER.error("channel read invalid packet : {}", msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        connection.close();
        LOGGER.error("caught an ex, channel={}", ctx.channel(), cause);
    }


    public CompletableFuture<Boolean> send(Object o) throws IllegalAccessException {
        return send(null, o);
    }

    public CompletableFuture<Boolean> send(String key, Object o) throws IllegalAccessException {
        if(roleName.equals(Role.CONSUMER)){
            throw new IllegalAccessException("消费者不允许生产消息");
        }

        //加入pending send队列

        //达到阈值后处理为push对象，然后批量发送
        return pendingPush(key, o);
    }

    private CompletableFuture<Boolean> pendingPush(String key, Object o) {

        if(pendingSend.isEmpty()){
            pendingSendStart.set(System.currentTimeMillis());
        }

        //加入pending pushAck队列
        Message message = new Message();
        //默认消息超时时间5分钟，超时会被broker删除
        message.setKey(key);
        message.setExpired(System.currentTimeMillis() + message_expired_time);
        message.setContent(o.toString());
        if(!pendingSend.offer(message)){
            throw new RuntimeException("加入pendingReceiveAck队列失败，队列可能已经满了!");
        }

        if(!isPendingPush){
            currentSendFuture = new CompletableFuture();
            isPendingPush = true;
        }else{
            return currentSendFuture;
        }

        ThreadManager.newThread(()->{
            doPush(currentSendFuture);
            isPendingPush = false;
        },"client-pending-push");

        return currentSendFuture;

    }

    private void doPush(final CompletableFuture<Boolean> completableFuture) {
        Push push = new Push();
        List<Message> pendingPushMessages = new ArrayList<>();

        while(pendingPushMessages.size() <= max_pending_num_limit){
            Message o = pendingSend.poll();
            if(o != null){
                pendingPushMessages.add(o);
            }else{
                if(System.currentTimeMillis() - pendingSendStart.get() <= max_pending_time_limit){
                    break;
                }
            }
        }
        pendingSendStart.set(System.currentTimeMillis());

        /**
         * key可以让消息发送到同一个partition，从而保证了一定的顺序
         * ps.不能完全保证消息顺序，只能保证积压的消息的顺序性
         */
        push.id = getRequestId(push);
        push.messages = pendingPushMessages;

        doSendPush(push, completableFuture);

    }

    private void doSendPush(Push push, final CompletableFuture<Boolean> completableFuture) {
        connection.send(new Packet(Command.PUSH, push), new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    LOGGER.error("push request send success, wait response... ");
                    requestCallback.put(push.id, completableFuture);
                }else{
                    LOGGER.error("push request send failed ! ");
                    completableFuture.complete(false);
                }
            }
        });
    }

    public CompletableFuture<Boolean> sendAck(Long id, Boolean ack) throws IllegalAccessException {
        if(roleName.equals(Role.CONSUMER)){
            throw new IllegalAccessException("消费者不允许确认生产消息");
        }
        //加入pending sendAck队列

        //达到阈值后处理为pushAck对象，然后批量发送

        return pendingPushAckOrReject(id, ack);
    }

    public Message receive(Boolean isBlocking) throws IllegalAccessException {
        if(roleName.equals(Role.PRODUCER)){
            throw new IllegalAccessException("生产者不允许消费消息");
        }
        //从当前消息池中取消息 条件1.消息池不为空 2.当消息池中消息少于阈值，触发prefetch

        if(prefetchCondition()){
            Pull pull = new Pull();
            pull.key = this.key;
            pull.num = one_prefetch_max_num;
            prefetch(pull);
        }

        Message message = null;
        if(isBlocking){
            while((message = pendingHandleMessage.poll()) == null && !Thread.interrupted()){
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));
            }
        }else{
            message = pendingHandleMessage.poll();
        }

        return message;
    }

    private Boolean prefetchCondition(){
        if(!isPrefetching && pendingHandleMessage.size() <= max_prefetch_capacity/2){
            isPrefetching = true;
            return true;
        }

        return false;
    }

    private void prefetch(Pull pull) {
        connection.send(new Packet(Command.PULL, pull), new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    LOGGER.info("prefetch request send success , wait response... ");
                }else{
                    LOGGER.error("prefetch request send failed ! ");
                }
            }
        });

    }

    public CompletableFuture<Boolean> receiveAck(Long id, Boolean ack) throws IllegalAccessException {
        if(roleName.equals(Role.PRODUCER)){
            throw new IllegalAccessException("生产者不允许确认消费消息");
        }

        //加入pending receiveAck队列
        //达到阈值后处理为pullAck对象，然后批量发送

        return pendingPullAckOrReject(id, ack);
    }

    private CompletableFuture<Boolean> pendingPullAckOrReject(Long id, Boolean ack) {

        if(pendingReceiveAck.isEmpty()){
            pendingReceiveAckStart.set(System.currentTimeMillis());
        }

        //加入pending receiveAck队列
        if(!pendingReceiveAck.offer(new IdAndAck(id, ack))){
            throw new RuntimeException("加入pendingReceiveAck队列失败，队列可能已经满了!");
        }

        if(!isPendingPullAckOrReject){
            currentReceiveAckFuture = new CompletableFuture();
            isPendingPullAckOrReject = true;
        }else{
            return currentReceiveAckFuture;
        }

        ThreadManager.newThread(()->{
            doPullAckOrReject(currentReceiveAckFuture);
            isPendingPullAckOrReject = false;
        },"client-pending-pullAck");

        return currentReceiveAckFuture;
    }

    private void doPullAckOrReject(final CompletableFuture<Boolean> completableFuture) {

        PullAck ack = new PullAck();
        List<Long> pendingAckId = new ArrayList<>();
        List<Long> pendingRejectId = new ArrayList<>();

        getAckIdsAndRejectIds(pendingAckId, pendingRejectId, pendingReceiveAck, pendingReceiveAckStart);

        pendingReceiveAckStart.set(System.currentTimeMillis());
        ack.id = getRequestId(ack);
        ack.messageAckIds = pendingAckId;
        ack.messageRejectIds = pendingRejectId;

        doSendPullAck(ack, 1, completableFuture);

    }

    /**
     * requestId
     * @return
     */
    private int getRequestId(Object o) {
        return Integer.valueOf(new SimpleDateFormat("ddHHmmss").format(new Date())) * 100 + o.hashCode() % 100;
    }

    private void doSendPullAck(PullAck ack, int times, final CompletableFuture<Boolean> completableFuture) {

        connection.send(new Packet(Command.PULL_ACK, ack), new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    LOGGER.error("push request send success, wait response... ");
                    requestCallback.put(ack.id, completableFuture);
                }else{
                    LOGGER.error("pullAck request send failed ! " + times + " retry...max_retry = " + max_fail_retry);
                    if(times >= max_fail_retry){
                        completableFuture.complete(false);
                    }else{
                        doSendPullAck(ack, times + 1, completableFuture);
                    }
                }
            }
        });
    }

    private class IdAndAck{
        private Long id;
        private Boolean ack;

        public IdAndAck(Long id, Boolean ack) {
            this.id = id;
            this.ack = ack;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Boolean getAck() {
            return ack;
        }

        public void setAck(Boolean ack) {
            this.ack = ack;
        }
    }

    private CompletableFuture<Boolean> pendingPushAckOrReject(Long id, Boolean ack) {

        if(pendingSendAck.isEmpty()){
            pendingSendAckStart.set(System.currentTimeMillis());
        }

        //加入pending receiveAck队列
        if(!pendingSendAck.offer(new IdAndAck(id, ack))){
            throw new RuntimeException("加入pendingSendAck队列失败，队列可能已经满了!");
        }

        if(!isPendingPushAckOrReject){
            currentSendAckFuture = new CompletableFuture();
            isPendingPushAckOrReject = true;
        }else{
            return currentSendAckFuture;
        }

        ThreadManager.newThread(()->{
            doPushAckOrReject(currentSendAckFuture);
            isPendingPushAckOrReject = false;
        },"client-pending-pushAck");

        return currentSendAckFuture;

    }

    private void doPushAckOrReject(final CompletableFuture<Boolean> completableFuture) {

        PullAck ack = new PullAck();
        List<Long> pendingAckId = new ArrayList<>();
        List<Long> pendingRejectId = new ArrayList<>();

        getAckIdsAndRejectIds(pendingAckId, pendingRejectId, pendingSendAck, pendingSendAckStart);
        pendingSendAckStart.set(System.currentTimeMillis());
        ack.id = getRequestId(ack);
        ack.messageAckIds = pendingAckId;
        ack.messageRejectIds = pendingRejectId;

        doSendPushAck(ack, 1, completableFuture);

    }

    private void doSendPushAck(PullAck ack, int times, final CompletableFuture<Boolean> completableFuture) {
        connection.send(new Packet(Command.PUSH_ACK, ack), new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    LOGGER.error("push request send success, wait response... ");
                    requestCallback.put(ack.id, completableFuture);
                }else{
                    LOGGER.error("pushAck request send failed ! " + times + " retry...max_retry = " + max_fail_retry);
                    if(times >= max_fail_retry){
                        completableFuture.complete(false);
                    }else{
                        doSendPushAck(ack, times + 1, completableFuture);
                    }
                }
            }
        });
    }


    private void getAckIdsAndRejectIds(List<Long> pendingAckId,
                                       List<Long> pendingRejectId,
                                       final Queue<IdAndAck> pendingAck,
                                       final AtomicLong pendingAckStart){
        while(pendingAckId.size() <= MAX_PENDING_NUM_LIMIT){
            IdAndAck idAndAck = pendingAck.poll();
            if(idAndAck != null){
                if(idAndAck.getAck()){
                    pendingAckId.add(idAndAck.getId());
                }else{
                    pendingRejectId.add(idAndAck.getId());
                }
            }else{
                if(System.currentTimeMillis() - pendingAckStart.get() <= MAX_PENDING_TIME_LIMIT){
                    break;
                }
            }
        }
    }

    @Override
    public void receive(Response response) {
        //push响应
        if(response.getResponseType() == ResponseEnum.PUSH_RESPONSE.getCode()){
            PushResult result = (PushResult)response.get();
            List<Long> ackIds = result.messageAckIds;
            //消息超时时间5分钟
            List<MessageInfo> messageInfos = ackIds.stream().map(id->
                new MessageInfo().setId(id).setExpired(System.currentTimeMillis() + message_expired_time).setStatus(0)
            ).collect(Collectors.toList());
            messageStorage.remotePush(messageInfos);
            requestCallback.remove(response.getId()).complete(true);
            return;
        }

        //pushAck响应
        if(response.getResponseType() == ResponseEnum.PUSH_ACK_RESPONSE.getCode()){
            PushAckResult result = (PushAckResult)response.get();
            messageStorage.remotePushAck(result.messageAckIds);
            requestCallback.remove(response.getId()).complete(true);
            return;
        }

        //pull响应
        if(response.getResponseType() == ResponseEnum.PULL_RESPONSE.getCode()){
            PullResult result = (PullResult)response.get();
            List<Message> messages = result.messages;
            //超时时间5分钟
            List<MessageInfo> messageInfos = messages.stream().map(message ->
                new MessageInfo().setId(message.getMid())
                        .setExpired(System.currentTimeMillis() + message_expired_time)
                        .setStatus(2)
            ).collect(Collectors.toList());
            messageStorage.remotePull(messageInfos);
        }

        //pullAck响应
        if(response.getResponseType() == ResponseEnum.PULL_ACK_RESPONSE.getCode()){
            PullAckResult result = (PullAckResult) response.get();
            messageStorage.remotePullAck(result.messageAckIds);
            messageStorage.remotePullReject(result.messageRejectIds);
            requestCallback.remove(response.getId()).complete(true);
            return;
        }

    }
}
