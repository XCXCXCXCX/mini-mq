package com.xcxcxcxcx.network.codec;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 * 编码器
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PacketEncoder extends MessageToByteEncoder<Packet>{

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        doEncode(packet, out);
    }

    private void doEncode(Packet packet, ByteBuf out) {

        Packet.PacketHeader header = packet.getHeader();
        byte[] body = packet.getBody();
        byte cmd = header.getCmd();
        //心跳包
        if(cmd == Command.HEARTBEAT.cmd){
            out.writeByte(Packet.HEATBEAT_BYTE);
        }else{//其他包
            out.writeInt(header.getLength());
            out.writeByte(header.getCmd());
            out.writeShort(header.getCheckcode());
            out.writeByte(header.getFlags());
            out.writeInt(header.getSessionId());
            out.writeByte(header.getLrc());
            if(header.getLength() > 0){
                out.writeBytes(body);
            }
        }

        //length(4) + cmd(1) + checkcode(2) + flags(1) + sessionId(4) + lrc(1)
    }
}
