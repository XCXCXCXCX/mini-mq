package com.xcxcxcxcx.mini.api.persistence;

import com.xcxcxcxcx.mini.api.spi.persistence.PersistenceService;

import java.util.List;

/**
 *
 * 持久化对象的代理对象
 * @author XCXCXCXCX
 * @Since 1.0
 */
public interface PersistenceMapper<T> extends PersistenceService<T>{

    /**
     * 存储类型
     * db/file/memory
     */
    PersistenceType getPersistenceType();

    /**
     * 获取原生对象（真正进行持久化的对象）
     */
    PersistenceService<T> getPersistenceService();

}
