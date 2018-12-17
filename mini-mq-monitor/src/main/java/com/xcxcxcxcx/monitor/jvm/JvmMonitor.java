package com.xcxcxcxcx.monitor.jvm;

import com.xcxcxcxcx.mini.tools.config.MiniConfig;
import com.xcxcxcxcx.mini.tools.monitor.jvm.JVMUtils;
import com.xcxcxcxcx.monitor.BaseMonitor;



/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class JvmMonitor extends BaseMonitor{

    public JvmMonitor() {
        period = MiniConfig.mini.monitor.jvm.jvm_dump_period;
    }

    @Override
    public void monitor() {
        JVMUtils.dump();
    }

}
