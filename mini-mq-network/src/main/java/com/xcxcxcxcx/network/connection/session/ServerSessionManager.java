package com.xcxcxcxcx.network.connection.session;

import com.xcxcxcxcx.mini.api.connector.session.SessionContext;
import com.xcxcxcxcx.mini.api.connector.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ServerSessionManager implements SessionManager{


    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSessionManager.class);

    private static final Map<Integer, SessionContext> sessionContextMap = new ConcurrentHashMap<>();


    /**
     * 暴露SessionContext，方便传递SessionContext参数
     * sessionId 由服务端生成
     * @return
     */
    public static SessionContext newSession() {
        SessionContext sessionContext = new SessionContext();
        int sessionId = getSessionId();
        sessionContext.setSessionId(sessionId);
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

    private static final AtomicInteger sessionIdGenerator = new AtomicInteger(1);

    private static int getSessionId(){
        return sessionIdGenerator.getAndIncrement();
    }
}
