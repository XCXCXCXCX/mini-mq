package com.xcxcxcxcx.mini.tools.thread;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 线程池管理者
 *
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class ThreadPoolManager implements ThreadFactory {

    private ThreadGroup group;
    private static final Map<String, ThreadGroup> threadGroups = new ConcurrentHashMap<>();
    private final String name_prefix;
    private static final AtomicInteger threadId = new AtomicInteger(0);
    private Generator threadIdGenerator = new Generator() {
        @Override
        public String generate() {
            return String.valueOf(threadId.incrementAndGet());
        }
    };

    public ThreadPoolManager(String groupName) {

        synchronized (threadGroups){
            this.group = threadGroups.get(groupName);
            if(this.group == null){
                this.group = new ThreadGroup(groupName);
                threadGroups.put(groupName, group);
            }
        }

        //FIX:无法获取到value
        //this.group = threadGroups.putIfAbsent(groupName, new ThreadGroup(groupName));

        this.name_prefix = group.getName();
    }

    public static Map<String, ThreadGroup> getThreadGroups() {
        return threadGroups;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, "pool:" + name_prefix + "-" + threadIdGenerator.generate(), 0);
        t.setContextClassLoader(ThreadPoolManager.class.getClassLoader());
        t.setPriority(Thread.NORM_PRIORITY);
        t.setDaemon(true);
        return t;
    }

    /**
     * 单独new出来的线程，命名方式与线程池的命名方式不同
     * @param r
     * @param threadName
     * @return
     */
    public Thread newThread(Runnable r, String threadName) {
        Thread t = new Thread(group, r, "single:" + threadName + "-" + threadIdGenerator.generate(), 0);
        t.setContextClassLoader(ThreadPoolManager.class.getClassLoader());
        t.setPriority(Thread.NORM_PRIORITY);
        t.setDaemon(true);
        return t;
    }

    public interface Generator{

        String generate();

    }

    /**
     * 测试
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        ThreadPoolManager manager = new ThreadPoolManager("service-test");
        ThreadPoolManager manager2 = new ThreadPoolManager("service-test");

        manager.newThread(() -> {
            System.out.println("good");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        manager2.newThread(() -> {
            System.out.println("haha");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        System.in.read();

    }

}
