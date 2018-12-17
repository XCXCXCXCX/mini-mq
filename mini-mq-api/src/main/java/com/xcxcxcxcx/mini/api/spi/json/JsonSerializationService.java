package com.xcxcxcxcx.mini.api.spi.json;

/**
 * json序列化服务
 * @author XCXCXCXCX
 * @Since 1.0
 */
public interface JsonSerializationService {

    /**
     * 二进制转对象
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T fromJson(byte[] json, Class<T> clazz);

    /**
     * 对象转二进制
     * @param json
     * @return
     */
    byte[] toJson(Object json);

    /**
     * String 转 对象
     */
    <T> T parseObject(String content, Class<T> clazz);
}
