package com.xcxcxcxcx.monitor;

import com.xcxcxcxcx.mini.tools.config.MiniConfig;
import com.xcxcxcxcx.mini.tools.thread.ThreadPoolManager;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public abstract class BaseMonitor implements Monitor, Runnable{

    public static ScheduledExecutorService executorService;

    static {
        if(MiniConfig.mini.monitor.enable_timing_dump){
            executorService = Executors.newSingleThreadScheduledExecutor(new ThreadPoolManager("timing-dump-monitor"));
        }
    }

    protected Duration period;


    @Override
    public abstract void monitor();

    @Override
    public void run() {
        if(executorService != null && period != null){
            executorService.schedule(this, period.toMillis(), TimeUnit.MILLISECONDS);
        }
    }
}
