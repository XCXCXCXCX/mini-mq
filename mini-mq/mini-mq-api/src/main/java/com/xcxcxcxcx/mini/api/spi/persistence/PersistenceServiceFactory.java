package com.xcxcxcxcx.mini.api.spi.persistence;

import com.xcxcxcxcx.mini.api.spi.SpiLoader;

/**
 * 持久化服务扩展点工厂类
 * @author XCXCXCXCX
 * @Since 1.0
 */
public final class PersistenceServiceFactory {

    private static class PersistenceServiceHolder{
        private static PersistenceService service = SpiLoader.load(PersistenceService.class);
    }

    public static PersistenceService create(){
        return PersistenceServiceHolder.service;
    }
}
