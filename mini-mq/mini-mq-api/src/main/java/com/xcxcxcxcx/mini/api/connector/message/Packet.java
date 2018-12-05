package com.xcxcxcxcx.mini.api.connector.message;


import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.spi.json.JsonSerializationService;
import com.xcxcxcxcx.mini.api.spi.json.JsonSerializationServiceFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.util.Arrays;

/**
 *
 * 消息报文
 * length(4) + cmd(1) + checkcode(2) + flags(1) + sessionId(4) + lrc(1)
 *
 * @author XCXCXCXCX
 * @Since 1.0
 */
public final class Packet{

    public static final int HEADER_LENGTH = 13;

    /**
     * if (flags & {} = {})
     * 开启压缩
     */
    public static final byte ENABLE_COMPRESS = 1;

    /**
     * 定义心跳包byte值
     */
    public static final byte HEATBEAT_BYTE = -127;

    /**
     * 心跳包
     */
    public static final Packet heartbeat = new Packet(Command.HEARTBEAT, null);

    private PacketHeader header;
    private byte[] body;

    private static JsonSerializationService jsonSerializationService = JsonSerializationServiceFactory.create();


    public Packet(Command command, Object body) {
        this(command, jsonSerializationService.toJson(body));
    }

    public Packet(Command command, byte[] body) {
        this(new PacketHeader(command.cmd), body);
    }

    public Packet(PacketHeader header, byte[] body){
        this.header = header;
        this.header.length = body.length;
        this.body = body;
    }


    public PacketHeader getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    /**
     * 数据包校验
     * @return
     */
    public Boolean isValid(){
        return validateHeader() && validateBody();
    }

    /**
     * 基于lrc校验头
     * @return
     */
    public Boolean validateHeader(){

        return (getLrc() ^ header.getLrc()) == 0;
    }

    private byte getLrc(){
        byte[] data = Unpooled.buffer(HEADER_LENGTH - 1)
                .writeInt(header.getLength())
                .writeByte(header.getCmd())
                .writeShort(header.getCheckcode())
                .writeByte(header.getFlags())
                .writeInt(header.getLrc())
                .array();
        byte lrc = 0;
        for (int i = 0; i < data.length; i++) {
            lrc ^= data[i];
        }
        return lrc;
    }

    /**
     * 基于checkcode校验体
     * @return
     */
    public Boolean validateBody(){

        return getCheckCode() == header.getCheckcode();
    }

    private short getCheckCode(){
        short checkCode = 0;
        if (body != null) {
            for (int i = 0; i < body.length; i++) {
                checkCode += (body[i] & 0x0ff);
            }
        }
        return checkCode;
    }

    public static final class PacketHeader{
        private int length;
        private byte cmd;
        private short checkcode;
        private byte flags;
        private int sessionId;
        private byte lrc;

        public PacketHeader(byte cmd) {
            this.cmd = cmd;
        }

        public PacketHeader(int length, byte cmd, short checkcode, byte flags, int sessionId, byte lrc) {
            this.length = length;
            this.cmd = cmd;
            this.checkcode = checkcode;
            this.flags = flags;
            this.sessionId = sessionId;
            this.lrc = lrc;
        }

        public int getLength() {
            return length;
        }

        public byte getCmd() {
            return cmd;
        }

        public short getCheckcode() {
            return checkcode;
        }

        public byte getFlags() {
            return flags;
        }

        public int getSessionId() {
            return sessionId;
        }

        public byte getLrc() {
            return lrc;
        }

        @Override
        public String toString() {
            return "PacketHeader{" +
                    "length=" + length +
                    ", cmd=" + cmd +
                    ", checkcode=" + checkcode +
                    ", flags=" + flags +
                    ", sessionId=" + sessionId +
                    ", lrc=" + lrc +
                    '}';
        }
    }

    public Object completeHeader(Channel channel){
        this.header.sessionId = channel.hashCode();
        this.header.flags = 0;
        this.header.checkcode = getCheckCode();
        this.header.lrc = getLrc();
        return this;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "header=" + header +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}
