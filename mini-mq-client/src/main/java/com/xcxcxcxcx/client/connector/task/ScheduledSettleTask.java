package com.xcxcxcxcx.client.connector.task;

import com.xcxcxcxcx.client.storage.abs.MessageStorage;
import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePullAck;
import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePushAck;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class ScheduledSettleTask implements Runnable{

    private final MessageStorage messageStorage;

    private final Connection connection;

    private final ScheduledExecutorService executorService;

    public ScheduledSettleTask(MessageStorage messageStorage, Connection connection, ScheduledExecutorService executorService) {
        this.messageStorage = messageStorage;
        this.connection = connection;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        //pushAck对账
        SettlePushAck settle1 = new SettlePushAck();
        settle1.ackIds = messageStorage.getPushAckFailedMessageId();
        connection.send(new Packet(Command.PUSH_ACK_SETTLE, settle1));

        //pullAck对账
        SettlePullAck settle2 = new SettlePullAck();
        settle2.ackIds = messageStorage.getPullAckFailedMessageId();
        settle2.rejectIds = messageStorage.getPullRejectFailedMessageId();
        connection.send(new Packet(Command.PULL_ACK_SETTLE, settle2));
        executorService.schedule(this, 5, TimeUnit.MINUTES);
    }
}
