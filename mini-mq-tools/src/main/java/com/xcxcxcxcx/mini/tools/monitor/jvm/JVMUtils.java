package com.xcxcxcxcx.mini.tools.monitor.jvm;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.xcxcxcxcx.mini.tools.config.MiniConfig;
import com.xcxcxcxcx.mini.tools.thread.ThreadManager;
import com.xcxcxcxcx.mini.tools.thread.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.*;
import java.lang.management.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * JVM监控，定期dump jstack/jmap
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class JVMUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JVMUtils.class);

    private static final String JSTACK_FILE_PREFIX = "jstack";
    private static final String JMAP_FILE_PREFIX = "jmap";

    private static class JVMBeanHolder{
        private static final String HOT_SPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";
        private static final HotSpotDiagnosticMXBean hotSpotMXBean = get();
        private static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        private static HotSpotDiagnosticMXBean get(){
            try {
                return AccessController.doPrivileged(new PrivilegedExceptionAction<HotSpotDiagnosticMXBean>() {
                    public HotSpotDiagnosticMXBean run() throws Exception {
                        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                        Set<ObjectName> s = server.queryNames(new ObjectName(HOT_SPOT_BEAN_NAME), null);
                        Iterator<ObjectName> itr = s.iterator();
                        if (itr.hasNext()) {
                            ObjectName name = itr.next();
                            HotSpotDiagnosticMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server,
                                    name.toString(), HotSpotDiagnosticMXBean.class);
                            return bean;
                        } else {
                            return null;
                        }
                    }
                });
            } catch (PrivilegedActionException e) {
                LOGGER.error("HotSpotMXBean Init Error");
                e.printStackTrace();
            }
            return null;
        }
    }

    private static final ThreadPoolManager.Generator jvmFileIdGenerator = new ThreadPoolManager.Generator() {
        @Override
        public String generate() {
            return String.valueOf(System.currentTimeMillis());
        }
    };

    /**
     * 对外提供的dump接口，启动两个线程分别dump jstack和jmap
     */
    public static void dump(){
        if(MiniConfig.mini.monitor.jvm.enable_jvm_monitor){
            doDump(MiniConfig.mini.monitor.jvm.jvm_dump_dir);
        }
    }

    private static void doDump(final String jvmPath) {
        ThreadManager.newThread(()->{
            doDumpJstack(jvmPath);
        },"jvm-jstack-dump").start();
        ThreadManager.newThread(()->{
            doDumpJmap(jvmPath);
        },"jvm-jmap-dump").start();

    }

    /**
     * 导出jmap
     * @param jvmPath
     */
    private static void doDumpJmap(final String jvmPath) {
        File file = new File(jvmPath,
                JMAP_FILE_PREFIX + "-" + jvmFileIdGenerator.generate() + ".log");
        String path = file.getPath();

        try {
            if (file.exists()) {
                file.delete();
            }
            JVMBeanHolder.hotSpotMXBean.dumpHeap(path, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 导出jstack
     * @param jvmPath
     */
    private static void doDumpJstack(final String jvmPath) {
        File path = new File(jvmPath);
        if (path.exists() || path.mkdirs()) {
            File file = new File(path,
                    JSTACK_FILE_PREFIX + "-" + jvmFileIdGenerator.generate() + ".log");
            try (FileOutputStream out = new FileOutputStream(file)) {
                jstack(out);
            } catch (Throwable t) {
                LOGGER.error("Dump Jstack Error!", t);
            }
        }
    }

    /**
     * jstack信息格式化到流
     * @param o
     */
    private static void jstack(OutputStream o) {
        PrintStream out = new PrintStream(o);
        boolean cpuTimeEnabled = JVMBeanHolder.threadMXBean.isThreadCpuTimeSupported() && JVMBeanHolder.threadMXBean.isThreadCpuTimeEnabled();
        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();

        for (Map.Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
            Thread t = entry.getKey();
            StackTraceElement[] elements = entry.getValue();

            ThreadInfo tt = JVMBeanHolder.threadMXBean.getThreadInfo(t.getId());
            long tid = t.getId();
            Thread.State state = t.getState();
            long cpuTimeMillis = cpuTimeEnabled ? JVMBeanHolder.threadMXBean.getThreadCpuTime(tid) / 1000000 : -1;
            long userTimeMillis = cpuTimeEnabled ? JVMBeanHolder.threadMXBean.getThreadUserTime(tid) / 1000000 : -1;

            out.printf("%s id=%d state=%s deamon=%s priority=%s cpu[total=%sms,user=%sms]", t.getName(),
                    tid, t.getState(), t.isDaemon(), t.getPriority(), cpuTimeMillis, userTimeMillis);
            final LockInfo lock = tt.getLockInfo();
            if (lock != null && state != Thread.State.BLOCKED) {
                out.printf("%n    - waiting on <0x%08x> (a %s)", lock.getIdentityHashCode(), lock.getClassName());
                out.printf("%n    - locked <0x%08x> (a %s)", lock.getIdentityHashCode(), lock.getClassName());
            } else if (lock != null && state == Thread.State.BLOCKED) {
                out.printf("%n    - waiting to lock <0x%08x> (a %s)", lock.getIdentityHashCode(),
                        lock.getClassName());
            }

            if (tt.isSuspended()) {
                out.print(" (suspended)");
            }

            if (tt.isInNative()) {
                out.print(" (running in native)");
            }

            out.println();
            if (tt.getLockOwnerName() != null) {
                out.printf("     owned by %s id=%d%n", tt.getLockOwnerName(), tt.getLockOwnerId());
            }

            final MonitorInfo[] monitors = tt.getLockedMonitors();

            for (int i = 0; i < elements.length; i++) {
                final StackTraceElement element = elements[i];
                out.printf("    at %s%n", element);
                for (int j = 1; j < monitors.length; j++) {
                    final MonitorInfo monitor = monitors[j];
                    if (monitor.getLockedStackDepth() == i) {
                        out.printf("      - locked %s%n", monitor);
                    }
                }
            }

            out.println();

            final LockInfo[] locks = tt.getLockedSynchronizers();
            if (locks.length > 0) {
                out.printf("    Locked synchronizers: count = %d%n", locks.length);
                for (LockInfo l : locks) {
                    out.printf("      - %s%n", l);
                }
                out.println();
            }
        }
    }
}
