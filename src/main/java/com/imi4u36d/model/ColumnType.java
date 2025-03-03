package com.imi4u36d.model;

import lombok.Getter;

/**
 * @author wangzhuo
 */

@Getter
public enum ColumnType {
    INT("Integer", ""),
    BIGINT("Long", ""),
    SMALLINT("Integer", ""),
    MEDIUMINT("Integer", ""),
    VARCHAR("String", ""),
    CHAR("String", ""),
    TEXT("String", ""),
    MEDIUMTEXT("String", ""),
    LONGTEXT("String", ""),
    BINARY("byte[]", ""),
    ENUM("String", ""),
    SET("String", ""),
    DATETIME("Date", "java.util.Date"),
    DATE("Date", "java.util.Date"),
    TIME("LocalTime", "java.time.LocalTime"),
    TIMESTAMP("Timestamp", "java.sql.Timestamp"),
    FLOAT("Float", ""),
    DOUBLE("Double", ""),
    DOUBLE_PRECISION("Double", ""),
    BIT("Boolean", ""),
    TINYINT("Boolean", ""),
    BOOLEAN("Boolean", ""),
    BOOL("Boolean", ""),
    YEAR("Integer", ""),
    DECIMAL("BigDecimal", "java.math.BigDecimal"),
    NUMERIC("BigDecimal", "java.math.BigDecimal"),
    JSON("String", ""),
    GEOMETRY("String", ""),
    POINT("String", ""),
    LINESTRING("String", ""),
    POLYGON("String", ""),
    BLOB("byte[]", ""),
    MEDIUMBLOB("byte[]", ""),
    LONGBLOB("byte[]", "");

    private final String fieldType;
    private final String packageName;

    ColumnType(String fieldType, String packageName) {
        this.fieldType = fieldType;
        this.packageName = packageName;
    }
}