package com.xcxcxcxcx.mini.api.connector.command;

/**
 * 命令
 * 用于驱动不同的消息处理
 * @author XCXCXCXCX
 * @Since 1.0
 */
public enum Command {

    HEARTBEAT(0),
    PULL_RESPONSE(1),
    PUSH_RESPONSE(2),
    PULL_ACK_RESPONSE(3),
    PUSH_ACK_RESPONSE(4),
    UNKNOWN(-1);

    Command(int cmd) {
        this.cmd = (byte) cmd;
    }

    public final byte cmd;

    public static Command toCMD(byte b) {
        Command[] values = values();
        if (b > 0 && b < values.length) return values[b - 1];
        return UNKNOWN;
    }
}
