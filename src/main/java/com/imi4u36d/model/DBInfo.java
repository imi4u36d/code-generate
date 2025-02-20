package com.imi4u36d.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangzhuo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DBInfo {

    private String url;

    private String userName;

    private String pwd;

}
