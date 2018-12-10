package com.xcxcxcxcx.persistence.db.persistence;

import com.xcxcxcxcx.mini.api.persistence.PersistenceMapper;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class DbFactory{

    private static class DbMapperHolder{
        private static final DbMapper mapper = new DbMapper();
    }

    public static PersistenceMapper getMapper() {
        return DbMapperHolder.mapper;
    }
}
