package com.xcxcxcxcx.network.codec;

import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.tools.config.MiniConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * 消息解码器
 *
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PacketDecoder extends LengthFieldBasedFrameDecoder {

    private static final long MAX_BODY_LENGTH = MiniConfig.mini.packet.max_body_length;

    public PacketDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return doDecode(in);
    }

    private Object doDecode(ByteBuf in) {

        //检测是否是心跳包
        if (in.readByte() == Packet.HEATBEAT_BYTE) {
            in.discardReadBytes();
            return Packet.heartbeat;
        } else {
            in.readerIndex(in.readerIndex() - 1);
            //从in中解析出packet
            return parsePacket(in);
        }

    }

    /**
     * 解析packet数据包
     *
     * @param in
     */
    private Object parsePacket(ByteBuf in) {
        Packet packet = doParsePacket(in);
        //如果读到不完整或有误的packet
        if (packet == null) {
            //重置读索引
            in.resetReaderIndex();
            return null;
            //throw new RuntimeException("decode packet error");
        } else {
            in.discardReadBytes();
            return packet;
        }
    }

    /**
     * 解析packet数据包
     * 1.检验header信息
     * 2.检验checkcode（防止数据包body信息被篡改）
     *
     * @param in
     * @return
     */
    private Packet doParsePacket(ByteBuf in) {
        Packet.PacketHeader header = parseHeader(in);
        if (header == null) return null;
        byte[] body = null;
        if (header.getLength() > 0) {
            body = parseBody(header.getLength(), in);
        }
        return new Packet(header, body);
    }

    /**
     * 解析header
     * 1.可读长度与需读长度不等
     * 2.body长度限制
     * 3.校验lrc（防止数据包header信息被篡改）
     *
     * @param in
     * @return
     */
    private Packet.PacketHeader parseHeader(ByteBuf in) {
        int readableLength = in.readableBytes();
        int length = in.readInt();
        if (readableLength < Packet.HEADER_LENGTH + length) {
            return null;
        }
        if (length > MAX_BODY_LENGTH) {
            //复位
            in.readerIndex(in.readerIndex() - 4);
            throw new TooLongFrameException("packet body is too long: " + length);
        }

        return new Packet.PacketHeader(length, in.readByte(), in.readShort(), in.readByte(), in.readInt(), in.readByte());
    }

    /**
     * 解析body
     *
     * @param in
     * @return
     */
    private byte[] parseBody(int bodyLength, ByteBuf in) {
        byte[] body = new byte[bodyLength];
        in.readBytes(body);
        return body;
    }

}
