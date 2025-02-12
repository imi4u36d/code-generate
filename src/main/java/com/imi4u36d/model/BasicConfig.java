package com.imi4u36d.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wangzhuo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BasicConfig {
    /**
     * 是否开启swagger支持
     */
    private Boolean swaggerEnable;

    /**
     * 是否开启强制覆盖
     */
    private Boolean overWriteEnable;
}

