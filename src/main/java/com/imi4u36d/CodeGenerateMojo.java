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
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangzhuo
 */
@Mojo(name = "code-generate")
public class CodeGenerateMojo extends AbstractMojo {

    @Parameter(property = "configFile", defaultValue = "src/main/resources/codeGenerate-config.yml")
    private File configFile;

    @Override
    public void execute() throws MojoExecutionException {

        try {
            // 读取配置文件
            Yaml yaml = new Yaml();
            Map<String, Object> obj = yaml.load(new FileInputStream(configFile));
            Map<String, String> database = (Map<String, String>) obj.get("database");
            List<Map<String, String>> tables = (List<Map<String, String>>) obj.get("tables");
            Map<String, Boolean> swaggerConfig = (Map<String, Boolean>) obj.get("swaggerConfig");
            Map<String, Object> outputConfig = (Map<String, Object>) obj.get("outputConfig");
            String ftlFileDirConfig = (String) obj.get("ftlFileDir");

            // 基本信息
            //是否开启swagger注解
            final Boolean swaggerEnable = swaggerConfig.get("swaggerEnable");

            //设置文件保存位置
            String outputDir = outputConfig.get("baseOutputDir").toString();
            // 是否开启覆写
            Boolean overwriteEnable = Boolean.parseBoolean(outputConfig.get("overwriteEnable").toString());
            //作者信息
            String author = outputConfig.get("authorInfo").toString();
            //基础包地址
            String packageUrl = outputConfig.get("packageUrl").toString();
            //dto文件包地址
            String dtoUrl = packageUrl + ".dto";
            //entity文件包地址
            String entityUrl = packageUrl + ".domain";
            //service文件包地址
            String serviceUrl = packageUrl + ".service";
            //impl文件包地址
            String implUrl = serviceUrl + ".impl";
            //mapper文件包地址
            String mapperUrl = packageUrl + ".mapper";
            //util文件包地址
            String utilUrl = packageUrl + ".utils";

            // 数据库连接信息
            String url = database.get("url");
            String username = database.get("username");
            String password = database.get("password");
            String driverClassName = database.get("driverClassName");


            //设置数据库相关信息
            DBConfiguration dbConfiguration = new DBConfiguration();
            dbConfiguration.setUrl(url);
            dbConfiguration.setUsername(username);
            dbConfiguration.setPwd(password);
            dbConfiguration.setDriverClassName(driverClassName);
            //设置你需要生成CRUD的表的名称
            List<String> tableNames = tables.stream().map(table -> table.get("tableName")).collect(Collectors.toList());
            dbConfiguration.setTableNames(tableNames);

            BasicConfig basicConfig = new BasicConfig().toBuilder()
                    .swaggerEnable(swaggerEnable)
                    .overWriteEnable(overwriteEnable)
                    .ftlFileDirConfig(ftlFileDirConfig)
                    .build();

            //配置数据库配置
            configDB(dbConfiguration);
            //扫描并注入表信息
            BasicInfo basicInfo = new BasicInfo().toBuilder()
                    .author(author)
                    .packageUrl(packageUrl)
                    .dtoUrl(dtoUrl)
                    .entityUrl(entityUrl)
                    .serviceUrl(serviceUrl)
                    .implUrl(implUrl)
                    .mapperUrl(mapperUrl)
                    .utilUrl(utilUrl)
                    .swaggerEnable(swaggerEnable)
                    .overWriteEnable(overwriteEnable)
                    .build();
            Map<String, BasicInfo> tableInfoList = scanTableInfo(basicInfo);


            //全部生成
            Arrays.stream(FileType.values())
                    .forEach(fileType ->
                            tableInfoList.forEach((tableName, info) ->
                                    FreemarkerUtils.ftlToFile(basicConfig, info, fileType, outputDir)));

            //关闭连接-结束程序
            DBUtils.closeConnection();
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing plugin", e);
        }
    }

    private void configDB(DBConfiguration dbConfiguration) {
        DBUtils.dbConfiguration = dbConfiguration;
    }

    /**
     * 注入部分基本信息之后开始对表进行扫描装配表详细信息
     * @return 表详细信息
     */
    private Map<String, BasicInfo> scanTableInfo(BasicInfo basicInfo) {
        DBUtils.scanInfoToModel(basicInfo);
        //获取所有的tableInfo
        return DBUtils.getTableInfoMap();
    }
}
