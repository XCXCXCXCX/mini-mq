package com.xcxcxcxcx.mini.api.client;


import com.xcxcxcxcx.mini.api.spi.json.JsonSerializationService;
import com.xcxcxcxcx.mini.api.spi.json.JsonSerializationServiceFactory;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface Partner {

    JsonSerializationService jsonService = JsonSerializationServiceFactory.create();

    /**
     * 每个角色都有全局唯一的id
     * @return
     */
    String getId();

    /**
     * 每个角色有独立的属性配置
     */
    Role getRole();

    /**
     * 角色订阅的topicId
     * @return
     */
    String getTopicId();

    /**
     * key
     */
    String getKey();

}
