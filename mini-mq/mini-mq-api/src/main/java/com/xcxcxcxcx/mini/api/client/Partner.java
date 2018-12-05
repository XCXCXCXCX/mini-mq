package com.xcxcxcxcx.mini.api.client;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface Partner {


    /**
     * 每个角色都有全局唯一的id
     * @return
     */
    String getId();

    /**
     * 每个角色有独立的属性配置
     */
    Role getRole();

}
