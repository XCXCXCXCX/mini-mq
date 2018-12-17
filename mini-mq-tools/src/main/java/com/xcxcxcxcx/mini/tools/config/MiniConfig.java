package com.xcxcxcxcx.mini.tools.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

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
        interface registry {

            Config registry = mini.getObject("registry").toConfig();

            String serverHost = registry.getString("server-host");
            int serverPort = registry.getInt("server-port");
            String connectString = registry.getString("connect-string");

        }

        /**
         * dataSource config
         */
        interface dataSource {

            Config dataSource = mini.getObject("dataSource").toConfig();

            String driverClassName = dataSource.getString("driver-class-name");
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

            long max_body_length = packet.getBytes("max-body-length");

        }

        /**
         * 连接配置
         */
        interface connection{

            Config connection = mini.getObject("connection").toConfig();

            int max_heartbeat_timeout_times = connection.getInt("max-heartbeat-timeout-times");

            /**
             * 越大，hash率越大，单个slot的遍历数就越少
             * 空间与时间的权衡
             * 简单的理解为：连接数越多，该值应该相应调大；连接数越少，该值应该相应调小。
             */
            int ticks_per_wheel = connection.getInt("ticks-per-wheel");

            Duration tick_duration = connection.getDuration("tick-duration");


            int send_buf_capacity = (int)connection.getMemorySize("send-buf-capacity").toBytes();
            int receive_buf_capacity = (int)connection.getMemorySize("receive-buf-capacity").toBytes();
            int write_buffer_low = (int)connection.getMemorySize("write-buffer-low").toBytes();
            int write_buffer_high = (int)connection.getMemorySize("write-buffer-high").toBytes();

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

            Boolean enable_timing_dump = mini.getBoolean("enable-timing-dump");

            interface flowcontrol{

                Config flowcontrol = monitor.getObject("flowcontrol").toConfig();

                Boolean enable_traffic_shaping = flowcontrol.getBoolean("enable-traffic-shaping");

                long write_global_limit = flowcontrol.getBytes("write-global-limit");

                long read_global_limit = flowcontrol.getBytes("read-global-limit");

                long write_channel_limit = flowcontrol.getBytes("write-channel-limit");

                long read_channel_limit = flowcontrol.getBytes("read-channel-limit");

                long check_interval = flowcontrol.getDuration("check-interval", TimeUnit.MILLISECONDS);

            }

            /**
             * jvm config
             */
            interface jvm {

                Config jvm = monitor.getObject("jvm").toConfig();

                Boolean enable_jvm_monitor = jvm.getBoolean("enable-jvm-monitor");
                String jvm_dump_dir = jvm.getString("jvm-dump-dir");
                Duration jvm_dump_period = jvm.getDuration("jvm-dump-period");

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

                Duration cost_dump_period = cost.getDuration("cost-dump-period");
            }
        }


    }


}
