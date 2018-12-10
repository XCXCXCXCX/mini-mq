package com.xcxcxcxcx.mini.common.handler;

import com.xcxcxcxcx.mini.api.connector.connection.Connection;
import com.xcxcxcxcx.mini.api.connector.message.Packet;
import com.xcxcxcxcx.mini.common.message.entity.Ack;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public abstract class AckHandler extends BaseHandler{

    @Override
    public Object doHandle(Packet packet, Connection connection) {
        Ack ack = getAckRequest(packet, connection);
        List<Long> messageAckIds = ack.messageAckIds;
        List<Long> messageRejectIds = ack.messageRejectIds;

        return updateMessageStatus(messageAckIds, messageRejectIds, getOldStatus(), getAckStatus(), getRejectStatus());
    }

    /**
     * reject状态
     * @return
     */
    protected abstract int getRejectStatus();

    /**
     * ack状态
     * @return
     */
    protected abstract int getAckStatus();

    /**
     * 要修改的状态
     * @return
     */
    protected abstract int getOldStatus();

    /**
     * 获取ack请求，pull类型或push类型
     * @param packet
     * @param connection
     * @return
     */
    protected abstract Ack getAckRequest(Packet packet, Connection connection);

    /**
     * 有PullAckResult和PushAckResult两种
     * @return
     */
    protected abstract Ack getAckResponse();

    /**
     * 更新db中消息的状态
     * 返回更新成功和更新失败的消息id
     * @param messageAckIds
     * @param messageRejectIds
     * @return
     */
    protected Object updateMessageStatus(List<Long> messageAckIds,
                                                  List<Long> messageRejectIds,
                                                  int oldStatus,
                                                  int ackStatus,
                                                  int rejectStatus){

        List<Long> ackSuccessIds =  updateMessageAck(messageAckIds, oldStatus, ackStatus);

        List<Long> rejectSuccessIds = updateMessageReject(messageRejectIds, oldStatus, rejectStatus);

        Ack result = getAckResponse();
        result.messageAckIds = ackSuccessIds;
        result.messageRejectIds = rejectSuccessIds;

        return result;
    }



    private List<Long> updateMessageAck(List<Long> messageAckIds, int oldStatus, int ackStatus) {
        return batchUpdateMessageStatus(messageAckIds, oldStatus, ackStatus);
    }

    private List<Long> updateMessageReject(List<Long> messageRejectIds, int oldStatus, int rejectStatus) {
        if(rejectStatus == -1){
            return batchDeleteMessage(messageRejectIds, oldStatus);
        }else{
            //TODO
            return batchUpdateMessageStatus(messageRejectIds, oldStatus, rejectStatus);
        }

    }

    /**
     * 删除该状态的消息
     * @param messageRejectIds
     * @param oldStatus
     * @return List<Long> 执行成功的id
     */
    private List<Long> batchDeleteMessage(List<Long> messageRejectIds, int oldStatus) {
        //TODO

        return null;
    }

    /**
     * 真正与数据库交互的方法
     * @param messageRejectIds
     * @param oldStatus
     * @param rejectStatus
     * @return List<Long> 执行成功的id
     */
    private List<Long> batchUpdateMessageStatus(List<Long> messageRejectIds, int oldStatus, int rejectStatus) {
        //TODO

        return null;
    }

}
