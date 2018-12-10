package com.xcxcxcxcx.network.connection;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.connection.ConnectionHolder;
import com.xcxcxcxcx.mini.tools.config.MiniConfig;
import com.xcxcxcxcx.mini.tools.log.LogUtils;
import com.xcxcxcxcx.mini.tools.thread.ThreadPoolManager;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class HeartbeatConnection implements ConnectionHolder, TimerTask {

    private Connection connection;

    private byte timeoutTimes;

    private static final int MAX_TIMEOUT_TIMES = MiniConfig.mini.connection.max_heartbeat_timeout_times;

    private int timeoutMillis;

    public static final HashedWheelTimer timer;

    static {
        long tickDuration = MiniConfig.mini.connection.tick_duration.toMillis();
        int ticksPerWheel = MiniConfig.mini.connection.ticks_per_wheel;
        timer = new HashedWheelTimer(
                new ThreadPoolManager("network-connection-heartbeat"),
                tickDuration, TimeUnit.MILLISECONDS, ticksPerWheel);
    }

    public HeartbeatConnection(Connection connection) {
        this.connection = connection;
        if (connection != null && connection.isConnected()) {
            init();
        } else {
            throw new IllegalArgumentException("连接不可用，无法进行心跳检测");
        }
    }

    void init() {
        timeoutTimes = 0;
        timeoutMillis = connection.getSessionContext().getHeartbeat();
        timer.newTimeout(this, timeoutMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run(Timeout timeout) throws Exception {

        Connection connection = this.connection;

        if (connection == null || !connection.isConnected()) {
            LogUtils.connection.info("heartbeat timeout times={}, connection disconnected, conn={}", timeoutTimes, connection);
            return;
        }

        if(connection.isReadTimeout()){

            //心跳次数超过限制，关闭连接
            if(++timeoutTimes > MAX_TIMEOUT_TIMES){
                connection.close();
                LogUtils.connection.info("heartbeat timeout times={}, connection disconnected, conn={}", timeoutTimes, connection);
                return;
            }else{
                //心跳超时但未超过限制
                LogUtils.connection.info("heartbeat timeout times={}, connection disconnected, conn={}", timeoutTimes, connection);
            }

        }else{

            timeoutTimes = 0;

        }

        timer.newTimeout(this, timeoutMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public Connection get() {
        return this.connection;
    }

    @Override
    public void close() {
        if (get() != null) {
            get().close();
        }
    }
}
