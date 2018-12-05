package com.xcxcxcxcx.mini.api.persistence;

/**
 *
 * 持久化类型
 * @author XCXCXCXCX
 * @Since 1.0
 */
public enum PersistenceType {
    DB("数据库存储"),
    FILE("文件存储"),
    MEMORY("内存存储");


    private String name;

    PersistenceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
