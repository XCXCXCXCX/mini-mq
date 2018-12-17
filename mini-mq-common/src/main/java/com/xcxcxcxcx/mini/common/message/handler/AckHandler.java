package com.xcxcxcxcx.mini.common.message.handler;

import com.xcxcxcxcx.mini.api.connector.message.handler.BaseHandler;
import com.xcxcxcxcx.mini.api.persistence.PersistenceMapper;
import com.xcxcxcxcx.persistence.db.persistence.DbFactory;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public abstract class AckHandler extends BaseHandler {

    protected PersistenceMapper persistenceMapper = DbFactory.getMapper();

}
