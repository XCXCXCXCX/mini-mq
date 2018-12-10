package com.xcxcxcxcx.mini.api.connector.connection;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface ConnectionHolder{
    Connection get();
    void close();
}