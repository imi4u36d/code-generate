package com.imi4u36d.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author wangzhuo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BasicConfig {
    /**
     * API文档类型: swagger, openapi, asyncapi
     */
    private String apiDocType;

    /**
     * API文档版本
     */
    private String apiDocVersion;

    /**
     * 是否开启API文档支持
     */
    private Boolean apiDocEnable;

    /**
     * 是否开启强制覆盖
     */
    private Boolean overWriteEnable;

    /**
     * 自定义ftl文件模版
     */
    private String ftlFileDirConfig;

    /**
     * 是否开启Lombok支持
     */
    private Boolean lombokEnable;

    /**
     * 自定义分层后缀
     */
    private Map<String, String> layerSuffix;

    /**
     * 自定义属性
     */
    private Map<String, Object> customProperties;
}
