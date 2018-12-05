package com.xcxcxcxcx.mini.api.spi.json;

import com.xcxcxcxcx.mini.api.spi.SpiLoader;

/**
 * json序列化服务工厂类
 * @author XCXCXCXCX
 * @Since 1.0
 */
public final class JsonSerializationServiceFactory {

    private static class JsonSerializationServiceHolder{
        private static JsonSerializationService service = SpiLoader.load(JsonSerializationService.class);
    }

    public static JsonSerializationService create(){
        return JsonSerializationServiceHolder.service;
    }
}
