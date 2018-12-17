package com.xcxcxcxcx.mini.api.connector.message.wrapper;


import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.PacketWrapper;
import com.xcxcxcxcx.mini.api.spi.json.JsonSerializationService;
import com.xcxcxcxcx.mini.api.spi.json.JsonSerializationServiceFactory;
import io.netty.channel.ChannelFutureListener;


/**
 *
 * packetWrapper基类
 * @author XCXCXCXCX
 * @since 1.0
 */
public abstract class BasePacketWrapper<T> implements PacketWrapper<T>{

    private final Connection connection;
    private final Packet packet;
    private final T messageHolder;

    protected final JsonSerializationService jsonService = JsonSerializationServiceFactory.create();

    public BasePacketWrapper(Connection connection, Packet packet) {
        this.connection = connection;
        this.packet = packet;
        this.messageHolder = decodeFromBody(packet);
    }

    protected abstract T decodeFromBody(Packet packet);

    @Override
    public T get() {
        return messageHolder;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public Packet getPacket() {
        return packet;
    }

    @Override
    public void send(ChannelFutureListener listener) {
        if((Packet.ENABLE_COMPRESS & getPacket().getHeader().getFlags()) == Packet.ENABLE_COMPRESS){
            compressPacket();
        }

        doSend(listener);

        finishSend();

    }

    protected void finishSend(){

    }

    /**
     * 压缩
     */
    private void compressPacket() {
        //TODO
    }

    private void doSend(ChannelFutureListener listener){
        getConnection().send(getPacket(), listener);
    }
}
