package com.xcxcxcxcx.mini.api.connector.session;

import com.xcxcxcxcx.mini.api.client.Partner;

import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * 会话上下文
 * @author XCXCXCXCX
 * @Since 1.0
 */
public final class SessionContext {

    /**
     * 默认心跳间隔10s
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

    private AtomicReference<SessionStatus> status;

    public SessionContext() {
        initStatus();
    }

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

    public SessionStatus getStatus(){
        return status.get();
    }

    private void initStatus(){
        status = new AtomicReference<>(SessionStatus.NEW);
    }

    public Boolean changeStatus(SessionStatus oldStatus, SessionStatus newStatus){
        return status.compareAndSet(oldStatus, newStatus);
    }

    public Boolean isActive(){
        return status.get() == SessionStatus.CONNECTED;
    }

    public int getHeartbeat() {
        return heartbeat;
    }

    public enum SessionStatus{
        NEW(0),
        CONNECTED(1);

        private int code;

        SessionStatus(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

}
