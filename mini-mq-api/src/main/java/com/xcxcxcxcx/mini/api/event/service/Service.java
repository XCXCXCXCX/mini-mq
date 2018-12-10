package com.xcxcxcxcx.mini.api.event.service;


import java.util.concurrent.CompletableFuture;

/**
 *
 * 服务上下文，用于控制服务的开启关闭
 * 服务间可能存在依赖关系，需要同步
 * @author XCXCXCXCX
 * @Since 1.0
 */
public interface Service extends LifeCycle{

    /**
     * 启动，提供启动结果回调接口
     */
    CompletableFuture<Boolean> start();

    /**
     * 关闭，提供关闭结果回调接口
     */
    CompletableFuture<Boolean> stop();

    /**
     * 同步启动
     * @return
     */
    Boolean synStart();

    /**
     * 同步关闭
     * @return
     */
    Boolean synStop();

    /**
     * 是否启动
     */
    Boolean isStarted();

    /**
     * 是否关闭
     */
    Boolean isStopped();



}
