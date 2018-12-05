package com.xcxcxcxcx.mini.tools.monitor.cost;

import org.junit.Test;

import java.io.IOException;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class CostTest {


    @Test
    public void costTest() throws InterruptedException, IOException {

        CostUtils.init();


        CostUtils.begin("costTest");
        CostUtils.begin("costTest2");
        Thread.sleep(300);
        CostUtils.end();
        CostUtils.end();

        CostUtils.begin("costTest");
        CostUtils.begin("costTest2");
        Thread.sleep(200);
        CostUtils.end();
        CostUtils.end();


        CostUtils.dumpEntryCount();
        CostUtils.dumpEntryCostly();

        System.in.read();
    }
}
