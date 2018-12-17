package com.xcxcxcxcx.mini.api.connector.session;


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

    private int sessionId;

    /**
     * 角色id
     */
    private String id;

    /**
     * idInGroup
     * 只有消费者持有
     */
    private int idInGroup;

    /**
     * 订阅的topicId
     */
    private String topicId;

    /**
     * 角色类型：生产者或消费者
     */
    private String roleName;

    private AtomicReference<SessionStatus> status;

    public SessionContext() {
        initStatus();
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdInGroup() {
        return idInGroup;
    }

    public void setIdInGroup(int idInGroup) {
        this.idInGroup = idInGroup;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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
