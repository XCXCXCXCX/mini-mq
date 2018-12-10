package com.xcxcxcxcx.mini.common.message.session;

import com.xcxcxcxcx.mini.api.connector.session.SessionContext;
import com.xcxcxcxcx.mini.api.connector.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ServerSessionManager implements SessionManager{


    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSessionManager.class);

    private final Map<Integer, SessionContext> sessionContextMap = new ConcurrentHashMap<>();


    /**
     * 暴露SessionContext，方便传递SessionContext参数
     * @param sessionId 由客户端生成
     * @return
     */
    @Override
    public SessionContext newSession(int sessionId) {
        SessionContext sessionContext = new SessionContext();
        sessionContextMap.put(sessionId, sessionContext);
        LOGGER.info("create session : sessionId = " + sessionId);
        return sessionContext;
    }

    /**
     * 会话建立成功
     * @param sessionId 客户端传来的参数
     * @return
     */
    @Override
    public SessionContext openSession(int sessionId) {
        SessionContext sessionContext = sessionContextMap.get(sessionId);
        if(sessionContext != null){
            sessionContext.changeStatus(SessionContext.SessionStatus.NEW, SessionContext.SessionStatus.CONNECTED);
            LOGGER.info("session connected: sessionId = " + sessionId);
            return sessionContext;
        }else{
            throw new IllegalStateException("不存在该session : sessionId = " + sessionId);
        }
    }

    /**
     * 关闭会话
     *
     * @param sessionId
     */
    @Override
    public void closeSession(int sessionId) {
        if(sessionContextMap.containsKey(sessionId)){
            if(sessionContextMap.remove(sessionId) != null){
                LOGGER.info("session closed: sessionId = " + sessionId);
            }
        }else{
            throw new IllegalStateException("不存在该session : sessionId = " + sessionId);
        }
    }
}
