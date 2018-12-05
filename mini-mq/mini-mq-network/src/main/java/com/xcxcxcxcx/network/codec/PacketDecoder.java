package com.xcxcxcxcx.network.codec;

import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.tools.config.MiniConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 *
 * 消息解码器
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PacketDecoder extends ByteToMessageDecoder{

    private static final int MAX_BODY_LENGTH = MiniConfig.mini.packet.max_body_length;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        doDecode(in, out);
    }

    private void doDecode(ByteBuf in, List<Object> out) {

        //检测是否是心跳包
        while(in.isReadable()){
            if(in.readByte() == Packet.HEATBEAT_BYTE){
                out.add(Packet.heartbeat);
            }else{
                in.readerIndex(in.readerIndex() - 1);
            }
        }

        //从in中解析出packet
        parsePacket(in, out);

    }

    /**
     * 解析packet数据包
     * @param in
     * @param out
     */
    private void parsePacket(ByteBuf in, List<Object> out) {
        Packet packet = doParsePacket(in, out);
        //如果读到不完整或有误的packet
        if(packet == null){
            //重置读索引
            in.resetReaderIndex();
        }else{
            out.add(packet);
        }
    }

    /**
     * 解析packet数据包
     * 1.检验header信息
     * 2.检验checkcode（防止数据包body信息被篡改）
     * @param in
     * @param out
     * @return
     */
    private Packet doParsePacket(ByteBuf in, List<Object> out) {
        Packet.PacketHeader header = parseHeader(in);
        if(header == null) return null;
        byte[] body = null;
        if(header.getLength() > 0){
            body = parseBody(header.getLength(), in);
        }
        return new Packet(header, body);
    }

    /**
     * 解析header
     * 1.可读长度与需读长度不等
     * 2.body长度限制
     * 3.校验lrc（防止数据包header信息被篡改）
     * @param in
     * @return
     */
    private Packet.PacketHeader parseHeader(ByteBuf in) {
        int readableLength = in.readableBytes();
        int length = in.readInt();
        if(readableLength < Packet.HEADER_LENGTH + length){
            return null;
        }
        if(length > MAX_BODY_LENGTH){
            //复位
            in.readerIndex(in.readerIndex() - 4);
            throw new TooLongFrameException("packet body is too long: " + length);
        }

        return new Packet.PacketHeader(length, in.readByte(), in.readShort(), in.readByte(), in.readInt(), in.readByte());
    }

    /**
     * 解析body
     * @param in
     * @return
     */
    private byte[] parseBody(int bodyLength, ByteBuf in) {
        byte[] body = new byte[bodyLength];
        in.readBytes(body);
        return body;
    }

    /**
     * 测试
     * @param args
     */
    public static void main(String[] args) {
        Byte b = -127;
        System.out.println(Integer.toBinaryString(b));
    }
}
