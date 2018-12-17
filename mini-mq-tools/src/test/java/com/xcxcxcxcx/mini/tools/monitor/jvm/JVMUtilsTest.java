package com.xcxcxcxcx.mini.tools.monitor.jvm;

import com.xcxcxcxcx.mini.tools.config.MiniConfig;
import org.junit.Test;

import java.io.IOException;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class JVMUtilsTest {

    @Test
    public void dump() throws IOException {
        JVMUtils.dump();
        System.in.read();
    }
}
