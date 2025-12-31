package com.imi4u36d;

import com.imi4u36d.config.DBConfiguration;
import com.imi4u36d.model.BasicConfig;
import com.imi4u36d.model.BasicInfo;
import com.imi4u36d.model.FileType;
import com.imi4u36d.util.DBUtils;
import com.imi4u36d.util.FreemarkerUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wangzhuo
 */
@Mojo(name = "code-generate")
public class CodeGenerateMojo extends AbstractMojo {

    @Parameter(property = "configFile", defaultValue = "src/main/resources/codeGenerate-config.yml")
    private File configFile;

    private static final Logger logger = LoggerFactory.getLogger(CodeGenerateMojo.class);

    @Override
    public void execute() throws MojoExecutionException {

        try {
            logger.info("开始执行代码生成插件");
            logger.info("读取配置文件: {}", configFile.getAbsolutePath());

            // 读取配置文件
            var yaml = new Yaml();
            @SuppressWarnings("unchecked")
            var obj = (Map<String, Object>) yaml.load(new FileInputStream(configFile));

            // 验证配置
            Objects.requireNonNull(obj, "配置文件为空");

            // 验证配置完整性
            validateConfig(obj);

            var database = (Map<String, String>) obj.get("database");
            var tables = (List<Map<String, String>>) obj.get("tables");
            var swaggerConfig = (Map<String, Object>) obj.get("swaggerConfig");
            var outputConfig = (Map<String, Object>) obj.get("outputConfig");
            var ftlFileDirConfig = (String) obj.get("ftlFileDir");

            // 自定义属性
            @SuppressWarnings("unchecked")
            var customProperties = (Map<String, Object>) obj.getOrDefault("customProperties", new HashMap<>());

            // Lombok支持
            var lombokEnable = Boolean.parseBoolean(customProperties.getOrDefault("lombokEnable", "false").toString());
            logger.info("Lombok支持状态: {}", lombokEnable);

            // 自定义分层后缀
            @SuppressWarnings("unchecked")
            var layerSuffix = (Map<String, String>) customProperties.getOrDefault("layerSuffix", new HashMap<>());

            // API文档配置
            // 是否开启API文档
            final var apiDocEnable = swaggerConfig.getOrDefault("swaggerEnable", false);
            // API文档类型: swagger, openapi, asyncapi
            final var apiDocType = swaggerConfig.getOrDefault("apiDocType", "swagger").toString();
            // API文档版本
            final var apiDocVersion = swaggerConfig.getOrDefault("apiDocVersion", "3.0").toString();

            logger.info("API文档启用状态: {}", apiDocEnable);
            logger.info("API文档类型: {}", apiDocType);
            logger.info("API文档版本: {}", apiDocVersion);

            // 设置文件保存位置
            var outputDir = outputConfig.get("baseOutputDir").toString();
            // 是否开启覆写
            var overwriteEnable = Boolean.parseBoolean(outputConfig.get("overwriteEnable").toString());
            // 作者信息
            var author = outputConfig.get("authorInfo").toString();
            // 基础包地址
            var packageUrl = outputConfig.get("packageUrl").toString();

            logger.info("输出目录: {}", outputDir);
            logger.info("是否开启覆写: {}", overwriteEnable);
            logger.info("作者信息: {}", author);
            logger.info("基础包地址: {}", packageUrl);

            // 自定义分层包名称
            @SuppressWarnings("unchecked")
            var layerPackageName = (Map<String, String>) customProperties.getOrDefault("layerPackageName",
                    new HashMap<>());

            // 构建包路径
            var dtoLayer = layerPackageName.getOrDefault("dto", "dto");
            var entityLayer = layerPackageName.getOrDefault("entity", "domain");
            var serviceLayer = layerPackageName.getOrDefault("service", "service");
            var serviceImplLayer = layerPackageName.getOrDefault("serviceImpl", "service.impl");
            var mapperLayer = layerPackageName.getOrDefault("mapper", "mapper");
            var controllerLayer = layerPackageName.getOrDefault("controller", "controller");
            var utilLayer = layerPackageName.getOrDefault("util", "utils");

            // dto文件包地址
            var dtoUrl = packageUrl + "." + dtoLayer;
            // entity文件包地址
            var entityUrl = packageUrl + "." + entityLayer;
            // service文件包地址
            var serviceUrl = packageUrl + "." + serviceLayer;
            // impl文件包地址
            var implUrl = packageUrl + "." + serviceImplLayer;
            // mapper文件包地址
            var mapperUrl = packageUrl + "." + mapperLayer;
            // util文件包地址
            var utilUrl = packageUrl + "." + utilLayer;

            logger.info("分层包配置: dto={}, entity={}, service={}, serviceImpl={}, mapper={}, controller={}, util={}",
                    dtoLayer, entityLayer, serviceLayer, serviceImplLayer, mapperLayer, controllerLayer, utilLayer);

            // 数据库连接信息
            var url = database.get("url");
            var username = database.get("username");
            var password = database.get("password");
            var driverClassName = database.get("driverClassName");

            logger.info("数据库URL: {}", url);
            logger.info("数据库用户名: {}", username);
            logger.info("数据库驱动: {}", driverClassName);

            // 设置数据库相关信息
            var dbConfiguration = new DBConfiguration();
            dbConfiguration.setUrl(url);
            dbConfiguration.setUsername(username);
            dbConfiguration.setPwd(password);
            dbConfiguration.setDriverClassName(driverClassName);
            // 设置你需要生成CRUD的表的名称
            var tableNames = tables.stream()
                    .map(table -> table.get("tableName"))
                    .collect(Collectors.toList());
            dbConfiguration.setTableNames(tableNames);

            logger.info("生成代码的表: {}", tableNames);

            var basicConfig = new BasicConfig().toBuilder()
                    .apiDocEnable((Boolean) apiDocEnable)
                    .apiDocType(apiDocType)
                    .apiDocVersion(apiDocVersion)
                    .overWriteEnable(overwriteEnable)
                    .ftlFileDirConfig(ftlFileDirConfig)
                    .lombokEnable(lombokEnable)
                    .layerSuffix(layerSuffix)
                    .customProperties(customProperties)
                    .build();

            // 配置数据库配置
            configDB(dbConfiguration);

            // 扫描并注入表信息
            logger.info("开始扫描表信息");
            var basicInfoBuilder = new BasicInfo().toBuilder()
                    .author(author)
                    .packageUrl(packageUrl)
                    .dtoUrl(dtoUrl)
                    .entityUrl(entityUrl)
                    .serviceUrl(serviceUrl)
                    .implUrl(implUrl)
                    .mapperUrl(mapperUrl)
                    .utilUrl(utilUrl)
                    .swaggerEnable((Boolean) apiDocEnable)
                    .overWriteEnable(overwriteEnable)
                    .lombokEnable(lombokEnable);

            // 设置自定义分层后缀
            basicInfoBuilder.controllerSuffix(layerSuffix.getOrDefault("controller", "Controller"));
            basicInfoBuilder.serviceSuffix(layerSuffix.getOrDefault("service", "Service"));
            basicInfoBuilder.serviceImplSuffix(layerSuffix.getOrDefault("serviceImpl", "Impl"));
            basicInfoBuilder.mapperSuffix(layerSuffix.getOrDefault("mapper", "Mapper"));
            basicInfoBuilder.dtoSuffix(layerSuffix.getOrDefault("dto", "Dto"));
            basicInfoBuilder.entitySuffix(layerSuffix.getOrDefault("entity", "Entity"));

            var basicInfo = basicInfoBuilder.build();
            var tableInfoList = scanTableInfo(basicInfo);
            logger.info("表信息扫描完成，共扫描 {} 张表", tableInfoList.size());

            // 开始生成代码文件
            logger.info("开始生成代码文件");
            
            // 先获取第一个表的basicInfo作为通用类生成的基础
            var firstTableInfo = tableInfoList.values().iterator().next();
            
            // 分离通用类和普通类
            var commonFileTypes = Arrays.asList(FileType.RES, FileType.BASERESDTO);
            var normalFileTypes = Arrays.stream(FileType.values())
                    .filter(fileType -> !commonFileTypes.contains(fileType))
                    .collect(Collectors.toList());
            
            // 生成通用类（只生成一次）
            commonFileTypes.forEach(fileType -> {
                logger.info("生成通用类: {}", fileType.getExtension());
                // 使用第一个表的输出目录，通用类将生成在该目录下
                String commonOutputDir = outputDir + File.separator + tableInfoList.keySet().iterator().next();
                FreemarkerUtils.ftlToFile(basicConfig, firstTableInfo, fileType, commonOutputDir);
            });
            
            // 生成为每个表生成的类
            normalFileTypes.parallelStream().forEach(fileType -> {
                tableInfoList.forEach((tableName, info) -> {
                    FreemarkerUtils.ftlToFile(basicConfig, info, fileType, outputDir + File.separator + tableName);
                });
            });

            logger.info("代码生成完成");

            // 关闭连接-结束程序
            DBUtils.getInstance().closeConnection();
            logger.info("数据库连接池已关闭");
        } catch (FileNotFoundException e) {
            logger.error("配置文件未找到: {}", configFile.getAbsolutePath(), e);
            throw new MojoExecutionException("配置文件未找到", e);
        } catch (ClassCastException e) {
            logger.error("配置文件格式错误", e);
            throw new MojoExecutionException("配置文件格式错误", e);
        } catch (IllegalArgumentException | NullPointerException e) {
            logger.error("配置参数错误: {}", e.getMessage(), e);
            throw new MojoExecutionException("配置参数错误", e);
        } catch (Exception e) {
            logger.error("执行插件失败", e);
            throw new MojoExecutionException("执行插件失败", e);
        }
    }

    /**
     * 验证配置完整性
     */
    private void validateConfig(Map<String, Object> config) {
        logger.info("开始验证配置完整性");

        // 验证数据库配置
        var database = (Map<String, String>) config.get("database");
        Objects.requireNonNull(database, "配置中缺少database节点");
        validateRequiredFields(database, "database", "url", "username", "password", "driverClassName");

        // 验证表配置
        var tables = (List<Map<String, String>>) config.get("tables");
        Objects.requireNonNull(tables, "配置中缺少tables节点");
        if (tables.isEmpty()) {
            throw new IllegalArgumentException("配置中tables列表为空");
        }
        for (int i = 0; i < tables.size(); i++) {
            var table = tables.get(i);
            validateRequiredFields(table, "tables[" + i + "]", "tableName");
        }

        // 验证swagger配置
        var swaggerConfig = (Map<String, Boolean>) config.get("swaggerConfig");
        Objects.requireNonNull(swaggerConfig, "配置中缺少swaggerConfig节点");
        validateRequiredFields(swaggerConfig, "swaggerConfig", "swaggerEnable");

        // 验证输出配置
        var outputConfig = (Map<String, Object>) config.get("outputConfig");
        Objects.requireNonNull(outputConfig, "配置中缺少outputConfig节点");
        validateRequiredFields(outputConfig, "outputConfig", "baseOutputDir", "overwriteEnable", "authorInfo",
                "packageUrl");

        logger.info("配置验证通过");
    }

    /**
     * 验证必填字段
     */
    private <T> void validateRequiredFields(Map<String, T> map, String section, String... fields) {
        for (String field : fields) {
            if (!map.containsKey(field) || map.get(field) == null) {
                throw new IllegalArgumentException("配置中缺少必填字段: " + section + "." + field);
            }
        }
    }

    private void configDB(DBConfiguration dbConfiguration) {
        DBUtils.getInstance().setDbConfiguration(dbConfiguration);
    }

    /**
     * 注入部分基本信息之后开始对表进行扫描装配表详细信息
     * 
     * @return 表详细信息
     */
    private Map<String, BasicInfo> scanTableInfo(BasicInfo basicInfo) {
        DBUtils.getInstance().scanInfoToModel(basicInfo);
        // 获取所有的tableInfo
        return DBUtils.getInstance().getTableInfoMap();
    }
}
