package com.xcxcxcxcx.mini.api.connector.message;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface AutoPullToMemoryTask extends Runnable{

    Boolean autoPullCondition();

    void doPullToMemory();
}
