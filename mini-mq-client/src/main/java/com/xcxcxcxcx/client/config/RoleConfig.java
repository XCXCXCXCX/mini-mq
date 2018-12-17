package com.xcxcxcxcx.client.config;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class RoleConfig {

    /**
     * 最大pending数量，用于批量确认、批量push的统一参数
     */
    public static final String max_pending_num_limit = "MAX_PENDING_NUM_LIMIT";

    /**
     * 最大pending时间，用于批量确认、批量push的统一参数
     */
    public static final String max_pending_time_limit = "MAX_PENDING_TIME_LIMIT";

    /**
     * 最大prefetch的消息容量
     * 当达到prefetch的容量的一半时，触发prefetch
     */
    public static final String max_prefetch_capacity = "MAX_PREFETCH_CAPACITY";

    /**
     * 一次prefetch的最大消息数量
     */
    public static final String one_prefetch_max_num = "ONE_PREFETCH_MAX_NUM";

    /**
     * 消息超时时间，超时会被broker删除或被认为消费失败
     * 1.push消息超时会被认为消息失效，在broker端被删除
     * 2.pull消息超时会被认为消息消费失败，暂时不会被拉到内存中供消费
     */
    public static final String message_expired_time = "MESSAGE_EXPIRED_TIME";

}
