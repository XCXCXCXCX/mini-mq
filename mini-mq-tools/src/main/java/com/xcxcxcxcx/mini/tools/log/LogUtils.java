package com.xcxcxcxcx.mini.tools.log;

import com.typesafe.config.ConfigRenderOptions;
import com.xcxcxcxcx.mini.tools.config.MiniConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class LogUtils {

    /**
     * 控制台日志
     */
    public static final Logger console = LoggerFactory.getLogger("console");

    static {
        System.setProperty("log.home", MiniConfig.mini.log.log_dir);
        System.setProperty("log.root.level", MiniConfig.mini.log.log_level);
        System.setProperty("logback.configurationFile", MiniConfig.mini.log.log_conf_path);

        console.info(MiniConfig.config.root().render(ConfigRenderOptions.concise().setFormatted(true)));
    }

    /**
     * 监控日志
     */
    public static final Logger monitor = LoggerFactory.getLogger("monitor");

    /**
     * 连接日志
     */
    public static final Logger connection = LoggerFactory.getLogger("connection");

    /**
     * 消息处理日志
     */
    public static final Logger handler = LoggerFactory.getLogger("handler");

    /**
     * 消息持久化日志
     */
    public static final Logger persistence = LoggerFactory.getLogger("persistence");



}
