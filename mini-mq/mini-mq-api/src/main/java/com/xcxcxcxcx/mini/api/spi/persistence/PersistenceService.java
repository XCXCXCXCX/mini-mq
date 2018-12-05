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
     * 保存一条记录（ps.覆盖）
     * 用于更新
     * @param t
     * @return
     */
    Boolean save(T t);

    /**
     * 保存多条记录
     * 用于更新
     * @return 保存结果
     */
    Boolean saveList(List<T> tList);

    /**
     * 如果不存在则保存（ps.不允许覆盖）
     * 用于插入
     * @param t
     * @return
     */
    Boolean saveIfAbsent(T t);

    /**
     * 如果不存在则保存（ps.不允许覆盖）
     * 用于插入
     * @param tList
     * @return
     */
    Boolean saveListIfAbsent(List<T> tList);

    /**
     * 删除记录
     * @param id
     * @return
     */
    Boolean remove(Long id);

    /**
     * 按条件删除
     * @param idList
     * @param topicId
     * @param status
     * @param pulledTimes
     * @param expired
     * @return
     */
    Boolean removeList(List<Long> idList, String topicId, Integer status, Integer pulledTimes, Long expired);

    /**
     * 按条件分页查询
     */
    List<T> query(T t, int pageNum, int pageSize);

    /**
     * 创建topic
     * @param topicId
     * @return
     */
    Boolean createTopic(String topicId);

    /**
     * 删除topic
     * @param topicId
     * @return
     */
    Boolean removeTopic(String topicId);

}
