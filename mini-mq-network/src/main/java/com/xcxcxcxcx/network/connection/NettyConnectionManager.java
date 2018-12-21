package com.xcxcxcxcx.network.connection;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.connection.ConnectionHolder;
import com.xcxcxcxcx.mini.api.connector.connection.ConnectionHolderFactory;
import com.xcxcxcxcx.mini.api.connector.connection.ConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.xcxcxcxcx.network.connection.HeartbeatConnection.timer;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class NettyConnectionManager implements ConnectionManager {

    private Map<ChannelId, ConnectionHolder> connections;

    private ConnectionHolderFactory connectionHolderFactory;

    public NettyConnectionManager() {
        init();
    }

    @Override
    public void init() {
        connections = new ConcurrentHashMap<>();
        connectionHolderFactory = HeartbeatConnection::new;
    }

    @Override
    public void destroy() {
        if(timer != null){
            timer.stop();
        }
        connections.clear();
        connections = null;
    }

    @Override
    public Connection getConnection(Channel channel) {
        return connections.get(channel.id()).get();
    }

    @Override
    public Connection addConnection(Connection connection) {
        ConnectionHolder holder = connectionHolderFactory.create(connection);
        connections.put(connection.getChannel().id(), holder);
        return holder.get();
    }

    @Override
    public void removeAndCloseConnection(Channel channel) {
        ConnectionHolder holder = connections.remove(channel.id());
        if(holder == null){
            new NettyConnection(channel).close();
        }
        holder.close();
    }

    @Override
    public int countCurrentConnection() {
        return connections.size();
    }

}
