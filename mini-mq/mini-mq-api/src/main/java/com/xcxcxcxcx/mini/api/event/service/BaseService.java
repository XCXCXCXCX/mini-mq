package com.xcxcxcxcx.mini.api.event.service;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * service抽象基类
 * @author XCXCXCXCX
 * @since 1.0
 */
public abstract class BaseService implements Service {

    private final AtomicBoolean started = new AtomicBoolean(false);

    @Override
    abstract public void init();

    @Override
    abstract public void destroy();

    /**
     * 使用默认的CompletableFuture，监听执行结果
     * @return {@link CompletableFuture}
     */
    @Override
    public CompletableFuture<Boolean> start() {
        ContextStateListener listener = new ContextStateListener(started);
        start(listener);
        return listener;
    }

    /**
     * 使用自定义的监听器，不会覆盖默认的CompletableFuture
     * @param listener
     */
    public void start(Listener listener) {

        if(!(listener instanceof ContextStateListener)){
            listener = new ContextStateListener(listener, started);
        }
        tryStart(listener);

    }

    private void tryStart(Listener listener) {

        try{
            if (started.compareAndSet(false, true)) {
                init();
                doStart();
                listener.onSuccess();
            }else{
                throw new IllegalStateException("service already started.");
            }
        }catch (Exception e){
            listener.onFailure(e);
            throw e;
        }

    }

    public abstract void doStart();
    public abstract void doStop();

    @Override
    public CompletableFuture<Boolean> stop() {
        ContextStateListener listener = new ContextStateListener(started);
        stop(listener);
        return listener;
    }

    public void stop(Listener listener) {
        if(!(listener instanceof ContextStateListener)){
            listener = new ContextStateListener(listener, started);
        }
        tryStop(listener);
    }

    private void tryStop(Listener listener) {
        try{
            if (started.compareAndSet(true, false)) {
                init();
                doStop();
                listener.onSuccess();
            }else{
                throw new IllegalStateException("service already stopped.");
            }
        }catch (Exception e){
            listener.onFailure(e);
            throw e;
        }
    }


    @Override
    public Boolean synStart() {
        return start().join();
    }

    @Override
    public Boolean synStop() {
        return stop().join();
    }

    @Override
    public Boolean isStarted() {
        return started.get();
    }

    @Override
    public Boolean isStopped() {
        return !started.get();
    }
}
