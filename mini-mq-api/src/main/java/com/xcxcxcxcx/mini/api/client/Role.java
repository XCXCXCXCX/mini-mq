package com.xcxcxcxcx.mini.api.client;

import java.util.Map;

/**
 *
 * 生产者/消费者角色，及其配置
 * @author XCXCXCXCX
 * @Since 1.0
 */
public final class Role {

    public static final String PRODUCER = "producer";

    public static final String CONSUMER ="consumer";

    private String roleName;

    private Map<String, Object> config;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
}
