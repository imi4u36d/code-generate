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
public class BaseResModel {

    private Integer code;

    private String content;

    private Object detail;

}
