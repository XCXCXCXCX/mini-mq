package com.xcxcxcxcx.persistence.db.mybatis.druid;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public class DruidDataSourceAdapter extends PooledDataSourceFactory {
    public DruidDataSourceAdapter() {
        this.dataSource = new DruidDataSource();
    }
}