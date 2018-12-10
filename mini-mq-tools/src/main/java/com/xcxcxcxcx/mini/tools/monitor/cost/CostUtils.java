package com.xcxcxcxcx.mini.tools.monitor.cost;


import com.xcxcxcxcx.mini.tools.config.MiniConfig;
import com.xcxcxcxcx.mini.tools.thread.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * 监控耗时情况及数据统计和导出
 *
 * 1. 可以开启或关闭
 * 2. 可以监控每一种方法的平均耗时/最大耗时/最小耗时
 * 3. 可以监控"慢执行"
 * 4. 可以导出当前监控情况
 * 5. 灵活、简单的API
 *
 * @author XCXCXCXCX
 * @since 1.0
 */
public class CostUtils {

    /**
     * 缓存当前entry数据统计情况
     */
    private static Map<String, EntryCount> entryCache;

    /**
     * 缓存耗时情况严重的entry
     */
    private static List<Entry> entryCostlyCache;

    /**
     * 用于定时统计的线程
     */
    private static ExecutorService executor;

    /**
     * 是否开启监控
     */
    private static final Boolean ENABLE_COST_MONITOR = MiniConfig.mini.monitor.cost.enable_cost_monitor;

    /**
     * 最大耗时限制，当方法耗时超过这个限制时会被记录下来
     */
    private static final Duration MAX_COST_LIMIT = MiniConfig.mini.monitor.cost.max_cost_limit;

    /**
     * 性能监控日志导出路径
     */
    private static final String costPath = MiniConfig.mini.monitor.cost.cost_dump_path;

    /**
     * 是否开启打印，每次方法耗时情况均会打印到控制台
     */
    private static final Boolean ENABLE_PRINT = MiniConfig.mini.monitor.cost.enable_print;

    /**
     * 默认文件名
     */
    private static final String COST_COUNT_FILE_NAME = "cost-count.log";
    private static final String ENTRY_COSTLY_FILE_NAME = "execute-costly-count.log";

    private static AtomicBoolean isInited = new AtomicBoolean(false);

    private static final Logger LOGGER = LoggerFactory.getLogger(CostUtils.class);

    private static ThreadLocal<Entry> currentEntry;

    private static void setCurrentEntry(Entry currentEntry) {
        CostUtils.currentEntry.set(currentEntry);
    }

    private static Entry getCurrentEntry() {
        return currentEntry.get();
    }

    /**
     * 初始化统计线程
     */
    public static void init(){
        if(ENABLE_COST_MONITOR && isInited.compareAndSet(false, true)){
            entryCache = new ConcurrentHashMap<>();
            entryCostlyCache = new CopyOnWriteArrayList<>();
            currentEntry = new ThreadLocal<>();
            executor = Executors.newScheduledThreadPool(1,
                    new ThreadPoolManager("tools-cost"));
        }else if(!ENABLE_COST_MONITOR){
            throw new IllegalStateException("未开启性能监控，check the config file");
        }
    }

    /**
     * 开始
     * @param name
     */
    public static void begin(String name){
        if(isInited.get() && ENABLE_COST_MONITOR){
            if(getCurrentEntry() == null){
                //起始entry
                Entry entry = new Entry(name);
                setCurrentEntry(entry);
            }else{
                //中途加入
                setCurrentEntry(new Entry(name, getCurrentEntry()));
            }
        }
    }

    /**
     * 当前entry结束
     */
    public static void end(){
        if(isInited.get() && ENABLE_COST_MONITOR) {
            Entry entry = getCurrentEntry();
            entry.release();

            if(entry.duration > MAX_COST_LIMIT.toMillis()){
                entryCostlyCache.add(entry);
            }

            EntryCount existed = entryCache.get(entry.name);
            if(existed == null){
                entryCache.put(entry.name,new EntryCount(entry));
            }else{
                existed.add(entry.duration);
            }
            //当前entry结束，返回到父entry
            setCurrentEntry(getCurrentEntry().parent);
        }
    }

    /**
     * 导出当前所有entry的统计情况
     */
    public static void dumpEntryCount(){

        if(isInited.get()
                && ENABLE_COST_MONITOR
                && entryCache != null
                && !entryCache.isEmpty()){
            executor.submit(()->{
                File path = new File(costPath);
                if (path.exists() || path.mkdirs()) {
                    File file = new File(path,
                            COST_COUNT_FILE_NAME);
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        doDumpEntryCount(out);
                    } catch (Throwable t) {
                        LOGGER.error("Dump Cost Error!", t);
                    }
                }
            });
        }


    }

    /**
     * 大致格式
     *                               cost
     * ============================================================
     *    name     ||     avg     ||     min     ||     max      ||
     *    name1    ||             ||             ||              ||
     *    name2    ||             ||             ||              ||
     *    name3    ||             ||             ||              ||
     *    ...      ||             ||             ||              ||
     *
     * @param out
     */
    private static void doDumpEntryCount(OutputStream out) {

        PrintWriter printWriter = new PrintWriter(out);

        int gridLength = 24;
        int columnNum = 4;
        int rowLength = (gridLength + 2) * columnNum;
        String title = "cost";

        /**
         * 第一行
         */
        StringBuffer row1 = new StringBuffer("");
        fillWithSpace(rowLength / 2 - title.length(), row1);
        row1.append(title);
        fillWithSpace(rowLength, row1);
        printWriter.print(row1.toString());
        printWriter.println();

        /**
         * 第二行分隔符
         */
        printWriter.print("============================================================================================================");
        printWriter.println();

        /**
         * 表格部分
         */
        String[] needField = new String[]{"name","avg","min","max"};
        int needFieldLength = needField.length;

        //展示四个字段
        printGrid(printWriter, needFieldLength, gridLength, needField, "cost");

        printWriter.flush();
    }

    /**
     * 生成格式化的表格行字符串
     * @param needFieldLength
     * @param gridLength
     * @param sb
     * @param needField
     */
    private static void generateGridRow(int needFieldLength, int gridLength, StringBuffer sb, String[] needField){
        for(int i = 0;i < needFieldLength; i++){
            fillWithSpace((gridLength + 2) * i + gridLength/2 - 4, sb);
            sb.append(needField[i]);
            fillWithSpace((gridLength + 2) * i + gridLength, sb);
            sb.append("||");
        }
    }

    /**
     * 导出较为耗时的entry
     */
    public static void dumpEntryCostly(){

        if(isInited.get()
                && ENABLE_COST_MONITOR
                && entryCostlyCache != null
                && !entryCostlyCache.isEmpty()) {

            executor.submit(()->{
                File path = new File(costPath);
                if (path.exists() || path.mkdirs()) {
                    File file = new File(path,
                            ENTRY_COSTLY_FILE_NAME);
                    //costly 需要增量写
                    try (FileOutputStream out = new FileOutputStream(file, true)) {
                        doDumpEntryCostly(out);
                    } catch (Throwable t) {
                        LOGGER.error("Dump EntryCostly Error!", t);
                    }
                }

                entryCostlyCache.clear();
            });

        }
    }

    /**
     * 大致格式
     *                                          costly
     * ===============================================================================
     *    name     ||    start    ||     end      ||    duration   ||     parent    ||
     *    name1    ||             ||              ||               ||               ||
     *    name2    ||             ||              ||               ||               ||
     *    name3    ||             ||              ||               ||               ||
     *    ...      ||             ||              ||               ||               ||
     *
     * @param out
     */
    private static void doDumpEntryCostly(FileOutputStream out) {
        //追加写文件
        PrintWriter printWriter = new PrintWriter(out);

        int gridLength = 32;
        int columnNum = 5;
        int rowLength = (gridLength + 2) * columnNum;
        String title = "costly";

        /**
         * 第一行
         */
        StringBuffer row1 = new StringBuffer("");
        fillWithSpace(rowLength / 2 - title.length(), row1);
        row1.append("%s");
        fillWithSpace(rowLength, row1);
        printWriter.printf(row1.toString(), title);
        printWriter.println();

        /**
         * 第二行分隔符
         */
        printWriter.printf("==========================================================================================================================================================================");
        printWriter.println();

        /**
         * 表格部分
         */
        String[] needField = new String[]{"name","start","end","duration","parent"};
        int needFieldLength = needField.length;

        //展示五个字段
        printGrid(printWriter, needFieldLength, gridLength, needField, "costly");

        printWriter.println();
        printWriter.println();

        printWriter.flush();
    }

    /**
     * 打印表格
     * @param printWriter
     * @param needFieldLength
     * @param gridLength
     * @param needField
     * @param type
     */
    private static void printGrid(final PrintWriter printWriter, int needFieldLength, int gridLength, String[] needField, String type){
        StringBuffer sb = new StringBuffer("");
        generateGridRow(needFieldLength, gridLength, sb, needField);
        printWriter.print(sb.toString());
        printWriter.println();
        if("cost".equals(type)){
            for(Map.Entry<String, EntryCount> entry : entryCache.entrySet()){
                String name = entry.getKey();
                EntryCount entryCount = entry.getValue().count();
                StringBuffer temp = new StringBuffer("");
                String[] needValue = new String[]{
                        name,
                        String.valueOf(entryCount.currentAvgCost),
                        String.valueOf(entryCount.currentMinCost),
                        String.valueOf(entryCount.currentMaxCost)};
                generateGridRow(needFieldLength, gridLength, temp, needValue);
                printWriter.print(temp.toString());
                printWriter.println();
            }
        }else if("costly".equals(type)){
            for(Entry entry : entryCostlyCache) {
                StringBuffer temp = new StringBuffer("");
                java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String[] needValue = new String[]{
                        entry.name,
                        format.format(new Date(entry.start)),
                        format.format(new Date(entry.end)),
                        String.valueOf(entry.duration),
                        entry.parent == null ? "NULL" : entry.parent.name};
                generateGridRow(needFieldLength, gridLength, temp, needValue);
                printWriter.print(temp.toString());
                printWriter.println();
            }
        }

    }

    /**
     * 自动填充空格，保证格式
     * @param fixedLength 固定长度
     * @param sb          StringBuffer
     */
    private static void fillWithSpace(int fixedLength,final StringBuffer sb){
        int currentLength = sb.length();

        for(int i = currentLength; i < fixedLength; i++){
            sb.append(" ");
        }
    }

    /**
     * 方法耗时类
     */
    private static class Entry{

        /**
         * 同名表示同方法
         */
        private String name;
        private Entry first;
        private Entry parent;
        private long start;
        private long end;

        private long duration;

        public Entry(String name) {
            this.name = name;
            this.first = this;
            this.parent = null;
            this.start = System.currentTimeMillis();
            if(ENABLE_PRINT){
                System.out.println(name + " begin execute");
            }
        }

        public Entry(String name, Entry parent) {
            this.name = name;
            this.first = parent.first;
            this.parent = parent;
            this.start = System.currentTimeMillis();
            if(ENABLE_PRINT){
                System.out.println("son method " + name + " enter execute");
            }
        }

        public void release() {
            this.end = System.currentTimeMillis();
            this.duration = this.end - this.start;
            if(ENABLE_PRINT){
                System.out.println(name + " cost " + duration + "ms");
            }
        }
    }

    /**
     * EntryCount 当前已统计数据情况
     */
    private static class EntryCount{

        private String name;
        private int lastWeight;
        private List<Long> costs;
        private long currentAvgCost;
        private long currentMinCost;
        private long currentMaxCost;

        public EntryCount(Entry entry) {
            this.name = entry.name;
            this.currentAvgCost = entry.duration;
            this.currentMinCost = entry.duration;
            this.currentMaxCost = entry.duration;
            costs = new CopyOnWriteArrayList<>();
            lastWeight = 1;
        }

        /**
         * 加入新的entry耗时值
         * @param duration
         */
        public void add(long duration){
            costs.add(duration);
        }

        /**
         * 统计并获取当前统计情况
         * @return
         */
        public EntryCount count(){

            int costsSize = costs.size();
            if(costs != null){
                lastWeight += costsSize;
            }

            if(costs != null && !costs.isEmpty()){
                long costsAvg = costs.stream().reduce((a,b)-> Long.sum(a,b)).map(a->(a/costsSize)).get();
                this.currentAvgCost = (costsAvg + currentAvgCost)/lastWeight;
                long costsMax = costs.stream().reduce((a,b)-> Long.max(a,b)).get();
                this.currentMaxCost = costsMax > this.currentMaxCost ? costsMax : this.currentAvgCost;
                long costsMin = costs.stream().reduce((a,b)-> Long.min(a,b)).get();
                this.currentMinCost = costsMin < this.currentMinCost ? costsMin : this.currentMinCost;
            }

            costs.clear();
            return this;
        }
    }

}
