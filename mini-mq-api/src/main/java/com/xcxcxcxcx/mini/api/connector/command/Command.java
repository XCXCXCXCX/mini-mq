package com.xcxcxcxcx.mini.api.connector.command;

/**
 * 命令
 * 用于驱动不同的消息处理
 * @author XCXCXCXCX
 * @Since 1.0
 */
public enum Command {

    HEARTBEAT(0),
    PUSH(1),
    PULL(2),
    PUSH_RESPONSE(3),
    PULL_RESPONSE(4),
    PUSH_ACK(5),
    PULL_ACK(6),
    PUSH_ACK_RESPONSE(7),
    PULL_ACK_RESPONSE(8),
    HAND_SHAKE(9),
    HAND_SHAKE_OK(10),
    PUSH_ACK_SETTLE(11),
    PULL_ACK_SETTLE(12),
    PUSH_ACK_SETTLE_RESPONSE(13),
    PULL_ACK_SETTLE_RESPONSE(14),
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
