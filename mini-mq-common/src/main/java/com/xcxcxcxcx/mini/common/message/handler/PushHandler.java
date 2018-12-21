package com.xcxcxcxcx.mini.common.message.handler;

import com.xcxcxcxcx.mini.api.connector.command.Command;
import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.api.connector.message.entity.PushResult;
import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.api.persistence.PersistenceMapper;
import com.xcxcxcxcx.mini.api.connector.message.entity.Push;
import com.xcxcxcxcx.mini.common.message.idgenerator.GlobalIdGenerator;
import com.xcxcxcxcx.mini.common.message.wrapper.PushPacketWrapper;
import com.xcxcxcxcx.mini.tools.log.LogUtils;
import com.xcxcxcxcx.persistence.db.persistence.DbFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class PushHandler extends BaseHandler {

    private PersistenceMapper persistenceMapper = DbFactory.getMapper();

    @Override
    public Object doHandle(Packet packet, Connection connection) {

        Push push = new PushPacketWrapper(connection, packet).get();
        String topicId = connection.getSessionContext().getTopicId();
        List<Message> messages = push.messages;

        //补充message信息，持久化到数据库
        messages.forEach(message -> {
                    message.setMid(GlobalIdGenerator.getId());
                    message.setStatus(Message.MessageStatus.NEW.getId());
                    message.setTopicId(topicId);
                });

        //1.把消息持久化到数据库，消息状态为0
        PushResult result = new PushResult();
        result.id = push.id;
        try{
            List<Message> messageList = persistenceMapper.batchPush(messages);
            result.messageAckIds = messageList.stream().map(message -> message.getMid()).collect(Collectors.toList());
        }catch (Exception e){
            result.messageRejectIds = messages.stream().
                    map(message -> message.getMid()).
                    collect(Collectors.toList());
        }

        return new Packet(Command.PUSH_RESPONSE, result);
    }

    @Override
    public void reply(Object result, Connection connection) {
        connection.send((Packet) result, future -> {
            if(future.isSuccess()){
                LogUtils.handler.info("reply push response packet success : " + result.toString());
            }else{
                LogUtils.handler.info("reply push response packet failed : " + result.toString());
            }
        });
    }
}
