package com.xcxcxcxcx.mini.api.persistence;

import com.xcxcxcxcx.mini.api.spi.persistence.PersistenceService;

import java.util.List;

/**
 *
 * 持久化对象的代理对象
 * @author XCXCXCXCX
 * @Since 1.0
 */
public interface PersistenceMapper<T> {

    /**
     * 存储类型
     * db/file/memory
     */
    PersistenceType getPersistenceType();

    /**
     * 获取原生对象（真正进行持久化的对象）
     */
    PersistenceService getPersistenceService();

    /**
     * 插入一条数据
     */
    int insert(T t);

    /**
     * 按主键批量插入
     */
    int batchInsert(List<T> tList);

    /**
     * 按条件更新数据
     */
    int update(T t);

    /**
     * 按主键批量更新
     */
    int batchUpdate(List<T> tList);

    /**
     * 按条件删除数据
     */
    int delete(T t);

    /**
     * 按主键批量删除
     */
    int batchDelete(List<T> tList);

    /**
     * 按条件分页查询
     */
    int select(T t, int pageNum, int pageSize);

}
