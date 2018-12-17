package com.xcxcxcxcx.monitor.cost;

import com.xcxcxcxcx.mini.tools.config.MiniConfig;
import com.xcxcxcxcx.mini.tools.monitor.cost.CostUtils;
import com.xcxcxcxcx.monitor.BaseMonitor;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class CostMonitor extends BaseMonitor{


    public CostMonitor() {
        period = MiniConfig.mini.monitor.cost.cost_dump_period;
    }

    @Override
    public void monitor() {
        CostUtils.dumpEntryCostly();
        CostUtils.dumpEntryCount();
    }

}
