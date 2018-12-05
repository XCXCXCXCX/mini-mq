package com.xcxcxcxcx.mini.api.spi.persistence;

import com.xcxcxcxcx.mini.api.connector.message.Message;
import com.xcxcxcxcx.mini.api.spi.Spi;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @Since 1.0
 */
@Spi(order = 1000)
public class PersistenceServiceImplTest implements PersistenceService<Message> {

    @Override
    public Boolean save(Message message) {
        return null;
    }

    @Override
    public Boolean saveList(List<Message> messages) {
        return null;
    }

    @Override
    public Boolean saveIfAbsent(Message message) {
        return null;
    }

    @Override
    public Boolean saveListIfAbsent(List<Message> messages) {
        return null;
    }

    @Override
    public Boolean remove(Long id) {
        return null;
    }

    @Override
    public Boolean removeList(List<Long> idList, Object... args) {
        return null;
    }

    @Override
    public List<Message> query(Message message, int pageNum, int pageSize) {
        return null;
    }
}
