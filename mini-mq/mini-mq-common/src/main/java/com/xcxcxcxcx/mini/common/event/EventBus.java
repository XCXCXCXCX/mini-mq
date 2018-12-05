package com.xcxcxcxcx.mini.common.event;

import com.google.common.eventbus.AsyncEventBus;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.xcxcxcxcx.mini.api.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 *
 * 事件总线，用于控制子服务的启动
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class EventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);
    private static com.google.common.eventbus.EventBus eventBus;

    public static void init(Executor executor) {
        eventBus = new AsyncEventBus(executor, (exception, context)
                -> LOGGER.error("event bus subscriber ex", exception));
    }

    public static void post(Event event) {
        if(isInit()) eventBus.post(event);
    }

    public static void register(Object bean) {
        if(isInit()) eventBus.register(bean);
    }

    public static void unregister(Object bean) {
        if(isInit()) eventBus.unregister(bean);
    }

    private static Boolean isInit() {
        if(eventBus == null){
            LOGGER.error("evenBus还未初始化");
            throw new IllegalStateException("evenBus还未初始化");
        }
        return true;
    }
}
