package com.xcxcxcxcx.mini.api.connector.session;

import com.xcxcxcxcx.mini.api.client.Partner;

/**
 *
 * 会话上下文
 * @author XCXCXCXCX
 * @Since 1.0
 */
public final class SessionContext {

    /**
     * 默认心跳10s
     */
    public static final int heartbeat = 10 * 1000;

    /**
     * 应用名
     */
    private String applicationName;

    /**
     * 生产者或消费者或两者都是
     */
    private Partner partner;


    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public int getHeartbeat() {
        return heartbeat;
    }

}
