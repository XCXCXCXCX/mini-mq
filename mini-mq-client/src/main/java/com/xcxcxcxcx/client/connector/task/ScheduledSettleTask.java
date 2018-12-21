package com.xcxcxcxcx.client.connector.task;

import com.xcxcxcxcx.client.storage.abs.MessageStorage;
import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePullAck;
import com.xcxcxcxcx.mini.api.connector.message.entity.SettlePushAck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class ScheduledSettleTask implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledSettleTask.class);

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
        LOGGER.info("trigger settle request automatically");
        //pushAck对账
        SettlePushAck settle1 = new SettlePushAck();
        settle1.ackIds = messageStorage.getPushAckFailedMessageId();
        LOGGER.debug("prepare SettlePushAck : ackIds = {}", settle1.ackIds);
        if(settle1.ackIds != null && !settle1.ackIds.isEmpty()){
            connection.send(new Packet(Command.PUSH_ACK_SETTLE, settle1));
        }

        //pullAck对账
        SettlePullAck settle2 = new SettlePullAck();
        settle2.ackIds = messageStorage.getPullAckFailedMessageId();
        settle2.rejectIds = messageStorage.getPullRejectFailedMessageId();
        LOGGER.debug("prepare SettlePullAck : ackIds = {}", settle1.ackIds);
        if((settle2.ackIds != null && !settle2.ackIds.isEmpty())
                || (settle2.rejectIds !=null && !settle2.rejectIds.isEmpty())){
            connection.send(new Packet(Command.PULL_ACK_SETTLE, settle2));
        }
        executorService.schedule(this, 5, TimeUnit.MINUTES);
    }
}
