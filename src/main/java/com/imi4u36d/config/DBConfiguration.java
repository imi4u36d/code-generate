package com.imi4u36d.config;

import lombok.Data;

import java.util.List;

/**
 * @author wangzhuo
 */
@Data
public class DBConfiguration {

    public String url;

    public String username;

    public String pwd;

    public List<String> tableNames;

    public String driverClassName;
}
