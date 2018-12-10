package com.xcxcxcxcx.mini.api.connector.connection;

import com.xcxcxcxcx.mini.api.event.service.LifeCycle;
import io.netty.channel.Channel;

/**
 *
 * 连接管理者
 * @author XCXCXCXCX
 * @Since 1.0
 */
public interface ConnectionManager extends LifeCycle{

    Connection getConnection(Channel channel);

    Connection addConnection(Connection connection);

    void removeAndCloseConnection(Channel channel);

    int countCurrentConnection();

}
