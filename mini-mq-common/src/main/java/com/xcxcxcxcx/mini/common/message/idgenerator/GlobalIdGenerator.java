package com.xcxcxcxcx.mini.common.message.idgenerator;

/**
 *
 * 基于雪花算法，分布式ID生成器
 * 根据zk上的服务节点ip:port决定datacenterId，workerId
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class GlobalIdGenerator{

    private static final IdGenerator idGenerator;


    /**
     * 根据zk上的ip:port信息生成workerId和datacenterId
     * zk注册后，将ip:port信息保存到ServiceConfig
     */
    static {
        //从ServiceConfig中获取ip:port
        //TODO
        //ip为a.b.c.d:p
        // (a*100+b*10+c)%(2^5) = datacenterId
        long datacenterId = 1;
        // (d*10+p)%(2^5) = workerId
        long workerId = 1;
        idGenerator = new SnowflakeIdWorker(datacenterId, workerId);
    }

    /**
     * 取id
     * @return
     */
    public static long getId(){
        return idGenerator.nextId();
    }

}
