package com.xcxcxcxcx.mini.api.event.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * 用于异步启动服务时，监听服务上下文是否启动成功
 * @author XCXCXCXCX
 * @Since 1.0
 */
public class ContextStateListener extends CompletableFuture<Boolean> implements Listener{

    private final Listener listener;
    private final AtomicBoolean started;

    public ContextStateListener(AtomicBoolean started) {
        listener = null;
        this.started = started;
    }

    public ContextStateListener(Listener listener, AtomicBoolean started) {
        this.listener = listener;
        this.started = started;
    }

    /**
     * 启动成功通知
     */
    public void onSuccess(Object... args){
        if (isDone()) return;// 防止Listener被重复执行
        complete(started.get());
        if (listener != null) listener.onSuccess(args);
    }

    /**
     * 启动失败通知
     * @return
     */
    public void onFailure(Throwable ex){
        if (isDone()) return;// 防止Listener被重复执行
        completeExceptionally(ex);
        if (listener != null) listener.onFailure(ex);
    }


}
