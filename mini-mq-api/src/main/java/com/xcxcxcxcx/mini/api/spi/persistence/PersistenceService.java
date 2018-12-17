package com.xcxcxcxcx.mini.api.spi.persistence;

import java.util.List;

/**
 *
 * 真正实现持久化的对象
 * @author XCXCXCXCX
 * @Since 1.0
 */
public interface PersistenceService<T> {

    /**
     * push消息，持久化，等待确认
     *
     * @param t
     * @return
     */
    T push(T t);

    /**
     * 批量push消息，持久化，等待确认
     * @param tList
     * @return
     */
    List<T> batchPush(List<T> tList);

    /**
     * id为消息表的id
     * @param id
     * @return 主键ID
     */
    Long ackPush(Long id);

    /**
     * 批量确认push
     * @param ids
     * @return 主键ID list
     */
    List<Long> batchAckPush(List<Long> ids);

    /**
     * id为消息表的id
     * @param id
     * @return 主键ID
     */
    Long rejectPush(Long id);

    /**
     * 批量删除push消息
     * @param ids
     * @return 主键ID list
     */
    List<Long> batchRejectPush(List<Long> ids);

    /**
     * 预处理topicId=#{topicId} and status=1的消息到订阅者消费组所订阅的topic中
     * 一次处理最多max条消息
     *
     * 将message表中的messageId提取出来，
     * 并构建（mid,consumerGroupId,pulledTimes,expired,status）插入到message_status表中
     *
     * prePullIfAbsent:
     * 关联message表和message_status表，当满足条件topicId=#{topicId} and status=1 and 在message_status里不存在该mid和#{consumerGroupId}
     * batchInsert（#{mid},#{consumerGroupId},1，now() + #{expired},2）
     *
     * prePull:
     * 关联message表和message_status表，当满足条件topicId=#{topicId} and status=1 and 在message_status里存在该mid和#{consumerGroupId}
     * batchUpdate（#{mid},#{consumerGroupId},pulledTimes++,now() + #{expired},2）
     *
     * prePull时，根据mid和consumerGroupId查询是否存在记录，
     * 如果不存在，表示是第一次prePull，插入记录（#{mid},#{consumerGroupId},1，now() + #{expired},2）
     * 如果存在，
     * 筛选出
     * expired < now() and pulledTimes < maxTimes and status = 2（表示是处理过但已超时又还未被标记为dead message） 此类消息不作处理，等待客户端“对账”请求时处理
     * pulledTimes < maxTimes and status = 4 (表示是处理过但已拒绝又还未被标记为dead message) 此类消息作处理
     * 更新记录（#{mid},#{consumerGroupId},pulledTimes++,now() + #{expired},2）
     *
     * 此时status=2，表示消息对于consumerGroupId的状态为“正在处理中”
     *
     *
     * @param topicId
     * @param consumerGroupId
     * @param key
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<T> prePullIfAbsent(String topicId,
                            String consumerGroupId,
                            String key,
                            Integer pageNum,
                            Integer pageSize);

    List<T> prePull(String topicId,
                    String consumerGroupId,
                    String key,
                    Integer pageNum,
                    Integer pageSize);

    /**
     * 根据id和consumerGroupId确认消息
     *
     * 在状态表中更新mid=#{id} and consumerGroupId=#{consumerGroupId} and status=2的记录，更新status为3
     *
     * @param id
     * @param consumerGroupId
     * @return 操作成功的主键ID
     */
    Long ackPull(Long id, String consumerGroupId);

    /**
     *
     * 根据ids和consumerGroupId批量确认消息
     *
     * 在状态表中更新mid in (#{ids}) and consumerGroupId=#{consumerGroupId} and status=2的记录，更新status为3
     * @param ids
     * @param consumerGroupId
     * @return 操作成功的主键IDs
     */
    List<Long> batchAckPull(List<Long> ids, String consumerGroupId);

    /**
     *
     * 根据id和consumerGroupId确认消息
     *
     * 在状态表中更新mid=#{id} and consumerGroupId=#{consumerGroupId} and status=2的记录，更新status为4
     *
     * @param id
     * @param consumerGroupId
     * @return
     */
    Long rejectPull(Long id, String consumerGroupId);

    /**
     *
     * 根据id和consumerGroupId确认消息
     *
     * 在状态表中更新mid in (#{ids}) and consumerGroupId=#{consumerGroupId} and status=2的记录，更新status为4
     *
     * @param ids
     * @param consumerGroupId
     * @return
     */
    List<Long> batchRejectPull(List<Long> ids, String consumerGroupId);

    /**
     * 用于定时clean
     *
     * 消息表中的记录 status=0 and expired > now 被delete
     *
     * @return
     */
    Boolean cleanExpired();

    /**
     * 创建topic
     * @param topicId
     * @param partitionNum
     * @return
     */
    Boolean createTopic(String topicId, Integer partitionNum);

    /**
     * 删除topic
     * @param topicId
     * @return
     */
    Boolean removeTopic(String topicId);

    /**
     * 加载所有topic
     * 用于BrokerContext的初始化
     * @return
     */
    List<TopicEntity> loadAllTopic();

}
