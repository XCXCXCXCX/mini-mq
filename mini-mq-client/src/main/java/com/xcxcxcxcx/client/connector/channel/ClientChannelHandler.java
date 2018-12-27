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
import com.xcxcxcxcx.mini.api.connector.message.HandshakeOKListener;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.PacketDispatcher;
import com.xcxcxcxcx.mini.api.connector.message.entity.*;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.api.event.service.Listener;
import com.xcxcxcxcx.mini.api.spi.json.JsonSerializationService;
import com.xcxcxcxcx.mini.api.spi.json.JsonSerializationServiceFactory;
import com.xcxcxcxcx.mini.tools.thread.ThreadManager;
import com.xcxcxcxcx.mini.tools.thread.ThreadPoolManager;
import com.xcxcxcxcx.network.connection.NettyConnection;
import io.netty.channel.*;
import io.netty.util.*;
import io.netty.util.Timer;
import io.netty.util.concurrent.*;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
@ChannelHandler.Sharable
public class ClientChannelHandler extends ChannelInboundHandlerAdapter implements ResponseReceiver,HandshakeOKListener,Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientChannelHandler.class);

    /**
     * 最大pending消息数量限制，3000个
     */
    private static final int MAX_PENDING_NUM_LIMIT = 3000;

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
    private static final int ONE_PREFETCH_MAX_NUM = 2000;

    /**
     * 最大send失败重试次数
     */
    private static final int MAX_FAIL_RETRY = 3;

    /**
     * 默认消息超时时间是5分钟
     */
    private static final int MESSAGE_EXPIRED_TIME = 5 * 60 * 1000;

    /**
     * 默认最大阻塞请求数是5000个
     */
    private static final int MAX_PENDING_REQUEST_NUM_LIMIT = 5000;

    private int max_pending_num_limit = MAX_PENDING_NUM_LIMIT;
    private long max_pending_time_limit = MAX_PENDING_TIME_LIMIT;
    private int max_prefetch_capacity = MAX_PREFETCH_CAPACITY;
    private int max_fail_retry = MAX_FAIL_RETRY;
    private int one_prefetch_max_num = ONE_PREFETCH_MAX_NUM;
    private long message_expired_time = MESSAGE_EXPIRED_TIME;

    private final ExecutorService pendingExecutor = Executors.newSingleThreadExecutor(new ThreadPoolManager("pending-pool"));
    private ExecutorService pendingAckExecutor;
    private final ScheduledExecutorService scheduleExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadPoolManager("schedule-settle-pool"));

    private final Queue<Message> pendingHandleMessage = new ConcurrentLinkedQueue<>();
    private final Queue<Message> pendingSend = new ConcurrentLinkedQueue<>();
    private final Queue<IdAndAck> pendingReceiveAck = new ConcurrentLinkedQueue<>();
    private final Queue<Integer> requestIdBuckets = new ConcurrentLinkedQueue<>();

    private volatile CompletableFuture<Boolean> currentSendFuture;
    private volatile CompletableFuture<Boolean> currentReceiveAckFuture;

    private static final AtomicLong pendingSendStart = new AtomicLong(0);
    private static final AtomicLong pendingReceiveAckStart = new AtomicLong(0);

    private final JsonSerializationService jsonService = JsonSerializationServiceFactory.create();

    /**
     * 本地消息信息存储
     */
    private final MessageStorage messageStorage = new CacheCenter();

    /**
     * 保存请求的回调
     */
    private final Map<Integer, CompletableFuture<Boolean>> requestCallback = new ConcurrentHashMap<>();

    private final ConnectionFactory connectionFactory;

    private final PacketDispatcher packetDispatcher;

    private Connection connection;

    private final String roleName;

    private final String id;

    private final String topicId;

    private final String key;

    private volatile Boolean isPendingPush = false;
    private volatile Boolean isPendingPullAckOrReject = false;
    private volatile Boolean isPrefetching = false;
    private volatile Boolean shakehandOK = false;

    private final Listener listener;

    public ClientChannelHandler(Partner partner, Listener listener) {
        roleName = partner.getRole().getRoleName();
        id = partner.getId();
        topicId = partner.getTopicId();
        key = partner.getKey();
        resolveProperties(partner.getRole().getConfig());

        connectionFactory = NettyConnection::new;

        this.listener = listener;
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
        packetDispatcher.register(Command.PULL_RESPONSE, new PullResponseHandler(pendingHandleMessage, this));
        packetDispatcher.register(Command.PUSH_ACK_RESPONSE, new PushAckResponseHandler(this));
        packetDispatcher.register(Command.PULL_ACK_RESPONSE, new PullAckResponseHandler(this));
        packetDispatcher.register(Command.PUSH_ACK_SETTLE_RESPONSE, new SettlePushAckResponseHandler(messageStorage));
        packetDispatcher.register(Command.PULL_ACK_SETTLE_RESPONSE, new SettlePullAckResponseHandler(messageStorage));
        packetDispatcher.register(Command.HAND_SHAKE_OK, new ClientHandshakeOKHandler(this, listener));

        /**
         * 初始化请求ID桶
         */
        for(int i = 0; i < MAX_PENDING_REQUEST_NUM_LIMIT; i++){
            requestIdBuckets.offer(i);
        }

        if(roleName.equals(Role.PRODUCER)){
            pendingAckExecutor =
                    Executors.newSingleThreadExecutor(new ThreadPoolManager("pending-ack-pool"));
        }
        scheduleExecutor.submit(new ScheduledSettleTask(messageStorage, connection, scheduleExecutor));

    }

    private void resolveProperties(Map<String, Object> props) {
        if(props != null){
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

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(connection!=null && connection.getChannel().id() == ctx.channel().id()){
            return;
        }
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
            connection.updateLastReadTime();
            packetDispatcher.dispatch((Packet) msg, connection);
        }else{
            LOGGER.error("channel read invalid packet : {}", msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        connection.close();
        LOGGER.error("caught an ex, channel={}", ctx.channel(), cause);
        if(cause instanceof IOException){
            initFlags();
            this.listener.onFailure(cause);
        }
    }

    /**
     * 重连时重置运行时标志位
     */
    private void initFlags(){
        shakehandOK = false;
        isPrefetching = false;
        isPendingPullAckOrReject = false;
        isPendingPush = false;
    }

    @Override
    public void notifyHandshakeOK() {
        shakehandOK = true;
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

        /**
         * 快速失败
         * 如果超过pending阈值，直接返回失败
         */
        int currentPendingNum;
        if((currentPendingNum = pendingSend.size()) > MAX_PENDING_NUM_LIMIT){
            LOGGER.error("too much pending num ! " + currentPendingNum + " PUSH waiting for ACK" );
            CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
            completableFuture.complete(false);
            return completableFuture;
        }

        //加入pending pushAck队列
        Message message = new Message();
        //默认消息超时时间5分钟，超时会被broker删除
        message.setKey(key);
        message.setExpired(System.currentTimeMillis() + message_expired_time);
        message.setContent(jsonService.parseString(o));
        if(!pendingSend.offer(message)){
            throw new RuntimeException("加入pendingReceiveAck队列失败，队列可能已经满了!");
        }

        if(!isPendingPush && !pendingSend.isEmpty()){
            currentSendFuture = new CompletableFuture<>();
            isPendingPush = true;
        }else {
            return currentSendFuture;
        }

        pendingExecutor.submit(this);

        return currentSendFuture;

    }

    private void doPush(final CompletableFuture<Boolean> completableFuture) {
        pendingSendStart.set(System.currentTimeMillis());
        Push push = new Push();
        List<Message> pendingPushMessages = new ArrayList<>();
        while(pendingPushMessages.size() < max_pending_num_limit){
            Message o = pendingSend.poll();
            if(o != null){
                pendingPushMessages.add(o);
            }else{
                if(System.currentTimeMillis() - pendingSendStart.get() > max_pending_time_limit){
                    break;
                }
            }
        }
        isPendingPush = false;

        /**
         * key可以让消息发送到同一个partition，从而保证了一定的顺序
         * ps.不能完全保证消息顺序，只能保证积压的消息的顺序性
         */
        push.id = getRequestId();
        push.messages = pendingPushMessages;

        doSendPush(push, completableFuture);

    }

    private void doSendPush(Push push, final CompletableFuture<Boolean> completableFuture) {
        waitForHandshakeOK();
        connection.send(new Packet(Command.PUSH, push), new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    LOGGER.info("push request send success, wait response...");
                    requestCallback.put(push.id, completableFuture);
                }else{
                    LOGGER.error("push request send failed ! ");
                    completableFuture.complete(false);
                }
            }
        });
    }

    /**
     * 连接建立成功且第二次握手成功，则认为初始化成功
     * @return
     */
    private boolean isInited() {
        return connection != null && shakehandOK;
    }

    private void waitForHandshakeOK(){
        int times = 0;
        while(!isInited()){
            try {
                times++;
                if(times > 5){
                    throw new RuntimeException("handshake failed, maybe [Handshake packet] is lost, try again.");
                }
                LOGGER.info("wait handshakeOK ...");
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 可以设立自动push的条件，如果有人（send）触发自动push，则会自动找寻pendingSend中的msg进行发送
     * 如果发现N次pendingSend中没有pending的msg，则令线程休眠，等待下次send时唤醒
     */
    @Override
    public void run() {
        if(roleName.equals(Role.PRODUCER)){
            doPush(currentSendFuture);
        } else if(roleName.equals(Role.CONSUMER)){
            doPullAckOrReject(currentReceiveAckFuture);
        }
    }

    public Message receive(Boolean isBlocking) throws IllegalAccessException {
        if(roleName.equals(Role.PRODUCER)){
            throw new IllegalAccessException("生产者不允许消费消息");
        }

        Message message = null;
        if(isBlocking){
            while((message = pendingHandleMessage.poll()) == null && !Thread.interrupted()){
                //从当前消息池中取消息 条件1.消息池不为空 2.当消息池中消息少于阈值，触发prefetch
                if(prefetchCondition()){
                    Pull pull = new Pull();
                    pull.key = this.key;
                    pull.num = one_prefetch_max_num;
                    waitForHandshakeOK();
                    prefetch(pull);
                }
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
            }
        }else{
            message = pendingHandleMessage.poll();
            //从当前消息池中取消息 条件1.消息池不为空 2.当消息池中消息少于阈值，触发prefetch
            if(prefetchCondition()){
                Pull pull = new Pull();
                pull.key = this.key;
                pull.num = one_prefetch_max_num;
                waitForHandshakeOK();
                prefetch(pull);
            }
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

        /**
         * 快速失败
         * 如果超过pending阈值，直接返回失败
         */
        int currentPendingNum;
        if((currentPendingNum = pendingReceiveAck.size()) > MAX_PENDING_NUM_LIMIT){
            LOGGER.error("too much pending num ! " + currentPendingNum + " PULL_ACK_OR_REJECT waiting for ACK" );
            CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
            completableFuture.complete(false);
            return completableFuture;
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

        pendingExecutor.submit(this);

        return currentReceiveAckFuture;
    }

    private void doPullAckOrReject(final CompletableFuture<Boolean> completableFuture) {

        pendingReceiveAckStart.set(System.currentTimeMillis());
        PullAck ack = new PullAck();
        List<Long> pendingAckId = new ArrayList<>();
        List<Long> pendingRejectId = new ArrayList<>();

        getAckIdsAndRejectIds(pendingAckId, pendingRejectId, pendingReceiveAck, pendingReceiveAckStart);
        isPendingPullAckOrReject = false;

        ack.id = getRequestId();
        ack.key = this.key;
        ack.messageAckIds = pendingAckId;
        ack.messageRejectIds = pendingRejectId;

        doSendPullAck(ack, 1, completableFuture);

    }

    /**
     * 拿取requestId
     * @return
     */
    private int getRequestId() {
        Integer x = requestIdBuckets.poll();
        if(x == null){
            throw new RuntimeException("current request too many! try to set bigger MAX_REQUEST_NUM_LIMIT.");
        }
        return x;
    }

    /**
     * 归还requestId
     * @param x
     */
    private void returnRequestId(Integer x){
        if(x == null){
            throw new NullPointerException("return NULL requestId!");
        }
        requestIdBuckets.offer(x);
    }

    private void doSendPullAck(PullAck ack, int times, final CompletableFuture<Boolean> completableFuture) {

        connection.send(new Packet(Command.PULL_ACK, ack), new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    LOGGER.info("pullAck request send success, wait response... ");
                    messageStorage.localPullAck(ack.messageAckIds);
                    messageStorage.localPullReject(ack.messageRejectIds);
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

    private void pushAckOrReject(int requestId,
                                 List<Long> ackId,
                                 List<Long> rejectId,
                                 final CompletableFuture<Boolean> completableFuture) {

        if((ackId == null || ackId.isEmpty())
                && (rejectId == null || rejectId.isEmpty())){
            completableFuture.complete(true);
            return;
        }

        pendingAckExecutor.submit(()->{
            doPushAckOrReject(requestId, ackId, rejectId, completableFuture);
        });

    }

    private void doPushAckOrReject(int requestId,
                                   List<Long> ackId,
                                   List<Long> rejectId,
                                   final CompletableFuture<Boolean> completableFuture) {

        PullAck ack = new PullAck();

        ack.id = requestId;
        ack.messageAckIds = ackId;
        ack.messageRejectIds = rejectId;

        doSendPushAck(ack, 1, completableFuture);

    }

    private void doSendPushAck(PullAck ack, int times, final CompletableFuture<Boolean> completableFuture) {
        connection.send(new Packet(Command.PUSH_ACK, ack), new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    LOGGER.info("pushAck request send success, wait response... ");
                    messageStorage.localPushAck(ack.messageAckIds);
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
        while(pendingAckId.size() < MAX_PENDING_NUM_LIMIT){
            IdAndAck idAndAck = pendingAck.poll();
            if(idAndAck != null){
                if(idAndAck.getAck()){
                    pendingAckId.add(idAndAck.getId());
                }else{
                    pendingRejectId.add(idAndAck.getId());
                }
            }else{
                if(System.currentTimeMillis() - pendingAckStart.get() > MAX_PENDING_TIME_LIMIT){
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
            /**
             * 此时认为第一步push成功，但无法保证消息被确认
             * 此时发起消息确认的请求
             */
            pushAckOrReject(result.id, ackIds, null, requestCallback.get(result.id));
            return;
        }

        //pushAck响应
        if(response.getResponseType() == ResponseEnum.PUSH_ACK_RESPONSE.getCode()){
            PushAckResult result = (PushAckResult)response.get();
            messageStorage.remotePushAck(result.messageAckIds);
            completeRequestCallback(response.getId());
            return;
        }

        //pull响应
        if(response.getResponseType() == ResponseEnum.PULL_RESPONSE.getCode()){
            isPrefetching = false;
            PullResult result = (PullResult)response.get();
            List<Message> messages = result.messages;
            //超时时间5分钟
            if(messages != null){
                List<MessageInfo> messageInfos = messages.stream().map(message ->
                        new MessageInfo().setId(message.getMid())
                                .setExpired(System.currentTimeMillis() + message_expired_time)
                                .setStatus(2)
                ).collect(Collectors.toList());
                messageStorage.remotePull(messageInfos);
            }
            return;
        }

        //pullAck响应
        if(response.getResponseType() == ResponseEnum.PULL_ACK_RESPONSE.getCode()){
            PullAckResult result = (PullAckResult) response.get();
            messageStorage.remotePullAck(result.messageAckIds);
            messageStorage.remotePullReject(result.messageRejectIds);
            completeRequestCallback(response.getId());
            return;
        }

    }

    private static final Timer retry_callback_timer =
            new HashedWheelTimer(new ThreadPoolManager("retry-callback-pool"));

    private void completeRequestCallback(int id) {
        try {
            requestCallback.remove(id).complete(true);
            returnRequestId(id);
            LOGGER.debug("request callback [id={}] success", id);
        }catch (NullPointerException e){
            //定时任务重试
            LOGGER.error("catch NullPointerException when completeRequestCallback, start retry-callback-pool..");
            LOGGER.debug("request callback map : " + requestCallback.toString());
            retry_callback_timer.newTimeout(timeout -> {
                LOGGER.debug("retry-callback-pool try handle [id={}] NullPointerException ...", id);
                completeRequestCallback(id);
            },5, TimeUnit.SECONDS);
        }
    }
}
