package com.xcxcxcxcx.mini.tools.json;

import com.alibaba.fastjson.JSON;
import com.xcxcxcxcx.mini.api.spi.Spi;
import com.xcxcxcxcx.mini.api.spi.json.JsonSerializationService;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
@Spi(order = 1)
public class FastjsonSerializationService implements JsonSerializationService{

    @Override
    public <T> T fromJson(byte[] json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    @Override
    public byte[] toJson(Object json) {
        return JSON.toJSONBytes(json);
    }

    @Override
    public <T> T parseObject(String content, Class<T> clazz) {
        return JSON.parseObject(content, clazz);
    }

    @Override
    public String parseString(Object o) {
        return JSON.toJSONString(o);
    }
}
