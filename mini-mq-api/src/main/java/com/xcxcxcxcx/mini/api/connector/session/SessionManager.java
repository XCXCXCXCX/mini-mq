package com.xcxcxcxcx.mini.api.connector.session;


/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface SessionManager {

    /**
     * 创建会话
     */
    SessionContext newSession(int sessionId);

    /**
     * 会话建立成功
     */
    SessionContext openSession(int sessionId);

    /**
     * 关闭会话
     */
    void closeSession(int sessionId);

}
