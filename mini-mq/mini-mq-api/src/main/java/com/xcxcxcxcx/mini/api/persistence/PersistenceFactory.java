package com.xcxcxcxcx.mini.api.persistence;

/**
 * @author XCXCXCXCX
 * @Since 1.0
 */

public interface PersistenceFactory {

    /**
     * 获取persistenceMapper
     * @return
     */
    PersistenceMapper getMapper();
}
