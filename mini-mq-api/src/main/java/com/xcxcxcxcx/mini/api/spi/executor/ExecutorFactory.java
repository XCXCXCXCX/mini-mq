package com.xcxcxcxcx.mini.api.spi.executor;

import com.xcxcxcxcx.mini.api.spi.SpiLoader;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ExecutorFactory {

    private static class ExecutorHolder{
        private static ExecutorService service = SpiLoader.load(ExecutorService.class);
    }

    public static ExecutorService create(){
        return ExecutorHolder.service;
    }

}
