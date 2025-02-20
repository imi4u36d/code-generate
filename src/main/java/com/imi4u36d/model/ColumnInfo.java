package com.imi4u36d.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author wangzhuo
 */
@Data
@Builder
public class ColumnInfo {

    private String columnName;

    private String columnType;

    private String javaName;

    private String javaType;

    private String columnComment;

}
