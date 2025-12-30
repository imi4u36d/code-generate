package com.imi4u36d.model;

import lombok.Getter;

/**
 * 数据库类型枚举
 * 
 * @author wangzhuo
 */
@Getter
public enum DatabaseType {
    MYSQL("mysql", "MySQL"),
    POSTGRESQL("postgresql", "PostgreSQL"),
    ORACLE("oracle", "Oracle"),
    SQL_SERVER("sqlserver", "SQL Server"),
    DB2("db2", "DB2"),
    H2("h2", "H2 Database");

    private final String type; // 用于URL识别的类型字符串
    private final String name; // 数据库名称

    DatabaseType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * 根据JDBC URL获取数据库类型
     * 
     * @param jdbcUrl JDBC URL
     * @return 数据库类型
     */
    public static DatabaseType fromJdbcUrl(String jdbcUrl) {
        if (jdbcUrl == null) {
            throw new IllegalArgumentException("JDBC URL cannot be null");
        }
        
        String url = jdbcUrl.toLowerCase();
        for (DatabaseType type : values()) {
            if (url.contains(type.getType())) {
                return type;
            }
        }
        
        // 默认返回MySQL
        return MYSQL;
    }
}