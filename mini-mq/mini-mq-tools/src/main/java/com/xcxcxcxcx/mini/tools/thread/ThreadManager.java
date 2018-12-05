package com.xcxcxcxcx.mini.tools.thread;

import java.util.concurrent.ThreadFactory;

/**
 *
 * 单独new的线程，命名为other
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ThreadManager implements ThreadFactory{

    private static final ThreadPoolManager threadManager = new ThreadPoolManager("");

    @Override
    public Thread newThread(Runnable r) {
        return newThread(r,"unknown");
    }

    public static Thread newThread(Runnable r, String threadName) {
        return threadManager.newThread(r, threadName);
    }
}
