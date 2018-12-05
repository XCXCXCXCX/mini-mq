package com.xcxcxcxcx.mini.tools.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.time.Duration;

/**
 * 加载配置文件
 *
 * @author XCXCXCXCX
 * @Since 1.0
 */
public interface MiniConfig {

    Config config = load();

    static Config load() {
        Config config = ConfigFactory.load();//扫描加载所有可用的配置文件
        String custom_conf = "Mini.conf";
        if (config.hasPath(custom_conf)) {
            File file = new File(config.getString(custom_conf));
            if (file.exists()) {
                Config custom = ConfigFactory.parseFile(file);
                config = custom.withFallback(config);
            }
        }
        return config;
    }

    interface mini {

        Config mini = config.getObject("mini").toConfig();

        /**
         * dataSource config
         */
        interface dataSource {

            Config dataSource = mini.getObject("dataSource").toConfig();

            String driverClassName = dataSource.getString("driverClassName");
            String url = dataSource.getString("url");
            String username = dataSource.getString("username");
            String password = dataSource.getString("password");

        }

        /**
         * log config
         */
        interface log {

            Config log = mini.getObject("log").toConfig();

            String log_dir = log.getString("log-dir");
            String log_level = log.getString("log-level");
            String log_conf_path = log.getString("log-conf-path");

        }



        /**
         * 默认executor config
         */
        interface threadPool {

            Config threadPool = mini.getObject("threadPool").toConfig();

            int core_pool_size = threadPool.getInt("core-pool-size");
            int max_pool_size = threadPool.getInt("max-pool-size");
            int queue_capacity = threadPool.getInt("queue-capacity");
            int keep_alive_seconds = threadPool.getInt("keep-alive-seconds");

        }

        /**
         * 最大消息
         */
        interface packet{

            Config packet = mini.getObject("packet").toConfig();

            int max_body_length = packet.getInt("max-body-length");

        }

        /**
         * partition配置
         *
         * pull阈值
         */
        interface partition{

            Config partition = mini.getObject("partition").toConfig();

            Duration max_poll_time = partition.getDuration("max-poll-time");
            int max_poll_message_num = partition.getInt("max-poll-message-num");

        }

        /**
         * 监控配置
         */
        interface monitor{

            Config monitor = mini.getObject("monitor").toConfig();

            /**
             * jvm config
             */
            interface jvm {

                Config jvm = monitor.getObject("jvm").toConfig();

                String jvm_dump_dir = jvm.getString("jvm-dump-dir");
            }

            /**
             * 性能监控配置
             */
            interface cost{

                Config cost = monitor.getObject("cost").toConfig();

                Boolean enable_cost_monitor = cost.getBoolean("enable-cost-monitor");

                Duration max_cost_limit = cost.getDuration("max-cost-limit");

                String cost_dump_path = cost.getString("cost-dump-path");

                Boolean enable_print = cost.getBoolean("enable-print");
            }
        }


    }


}
