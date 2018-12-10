package com.xcxcxcxcx.mini.tools.config;

import com.typesafe.config.ConfigRenderOptions;
import org.junit.Test;

/**
 * 配置文件加载测试
 * @author XCXCXCXCX
 * @Since 1.0
 */
public class MiniConfigTest {

    @Test
    public void loadConfig(){

        System.out.println(MiniConfig.config.root().render(ConfigRenderOptions.concise().setFormatted(true)));

    }

}
