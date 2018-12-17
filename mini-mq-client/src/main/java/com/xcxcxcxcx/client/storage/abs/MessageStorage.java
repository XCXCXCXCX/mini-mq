package com.xcxcxcxcx.client.storage.abs;

import java.util.List;

/**
 *
 * 消息存储，client端收到响应的数据需要保存在本地
 * 这样保证了数据不丢失，也便于在出现问题时，确定数据是否一致
 *
 * 客户端只需要保存push后的响应信息和pushAck后的响应信息、pull后的响应信息和pullAck后的响应信息，
 * 保证两点：
 * 1.保证push的消息在客户端发送ack指令成功后在broker正确确认
 * 2.保证pull的消息在客户端有且仅被消费一次并被客户端和broker正确确认
 *
 *
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface MessageStorage {

    /**
     * 服务器响应的push成功的消息id，即：消息被成功持久化但等待确认
     * 状态为0
     *
     * ps.只要收到服务端响应的push的响应消息，则消息必定被成功持久化
     */
    void remotePush(List<MessageInfo> messageInfos);

    /**
     * 本地pushAck，只要ack消息被成功发送出去，则认为消息会被确认成功
     * 状态为1
     *
     * 当执行{@link MessageStorage#remotePushAck(java.util.List)}后，本地pushAck消息被删除，
     * 此时认为服务端和客户端均认为该消息已被持久化且确认
     * @param ids
     */
    void localPushAck(List<Long> ids);

    /**
     * 服务器响应的pushAck的消息id，即：消息被成功持久化到broker且在服务端确认成功
     *
     * 可覆盖savePush的结果
     *
     * ps.只要收到服务端响应的pushAck的响应消息，则消息必定被成功持久化和确认
     */
    void remotePushAck(List<Long> ids);

    /**
     * 获得本地认为发送失败的消息：
     * 1.消息被认为已持久化，且已发送ack请求，但未收到服务端确认响应，且已超时，则认为消息发送失败(localPushAck:ids)
     * 2.消息被认为已持久化，且已发送reject请求，但未收到服务端确认响应，且已超时，则认为消息发送失败（pushAckResult:rejectIds）
     *
     * 由于存在一种情况，消息在push成功后，在服务端ack成功但未将ack结果响应给客户端
     * 那么就会出现这样的问题：客户端认为消息发送失败，但实际上消息已发送成功，且该消息会被所订阅的消费组消费
     * 解决的方案：
     *  1.客户端只要发送了ack请求，则认为消息确认成功，在客户端多次重试pushAck的消息，确保push消息被ack
     *  2.客户端只要发送了ack请求，则认为消息确认成功，由后台定期与服务端“对账”，保证push成功且发出ack的消息被ack
     *
     * 使用两种方式来保证消息被客户端和服务端均成功确认
     *
     * 此方法是为了第二种解决方案
     * @return
     */
    List<Long> getPushAckFailedMessageId();

    /**
     * 服务器响应的pull消息，即：消息被成功的拉取到client但等待确认
     * 状态为2
     *
     * ps.只要收到服务端响应的pull的响应消息，则消息必定被成功拉取
     */
    void remotePull(List<MessageInfo> messageInfos);

    /**
     * 本地pullAck，只要本地pullAck，则认为消息已被消费且成功确认
     *
     * 当执行{@link MessageStorage#remotePullAck(java.util.List)}后，本地pullAck消息被删除，
     * 此时认为服务端和客户端均认为该消息已被消费且确认
     * @param ids
     */
    void localPullAck(List<Long> ids);
    void localPullReject(List<Long> ids);

    /**
     * 服务器响应的pullAck的消息id，即：消息被成功消费且在服务端确认成功的消息
     * 状态为3
     * 将savePull的结果移除存储容器，表示该条记录已确保客户端和服务端均认为消费成功。
     *
     * ps.只要收到服务端响应的pullAck的响应消息，则消息必定被成功消费和broker确认
     */
    void remotePullAck(List<Long> ids);
    void remotePullReject(List<Long> ids);

    /**
     * 获得本地认为已消费失败的消息
     * 1.本地保存pull信息后，且发送了pullAck请求，获取响应数据超时(remotePull:超时ids)
     * 2.本地拒绝pull消息，且在超时时间后还未收到远程reject响应
     *
     * 与服务端进行比对，
     * 1.如果有消息ack但被本地认为reject，则触发一次prefetch（仅针对消费失败的消息）
     * 2.如果在服务端发现有消息已被ack成功但本地认为ack失败，则删除本地pullAck列表中的该条记录
     * @return
     */
    List<Long> getPullAckFailedMessageId();

    List<Long> getPullRejectFailedMessageId();
}
