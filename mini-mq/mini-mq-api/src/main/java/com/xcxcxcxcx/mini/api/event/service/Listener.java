package com.xcxcxcxcx.mini.api.event.service;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface Listener {

    /**
     * 成功通知
     */
    void onSuccess(Object... args);

    /**
     * 失败通知
     * @return
     */
    void onFailure(Throwable ex);
}
