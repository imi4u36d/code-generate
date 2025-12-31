package com.imi4u36d.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author wangzhuo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BasicInfo {
    /**
     * 实体(驼峰大写开头)
     */
    private String entityName;

    /**
     * 数据库表名称
     */
    private String tableName;

    /**
     * 数据库表注释
     */
    private String tableComment;

    /**
     * 当前时间
     */
    private String curTime = nowTime();

    /**
     * 需要导入的包名
     */
    private List<String> importPackages;

    /**
     * 基础包地址
     */
    String packageUrl;

    /**
     * dto文件包地址
     */
    String dtoUrl;

    /**
     * entity文件包地址
     */
    String entityUrl;

    /**
     * service文件包地址
     */
    String serviceUrl;

    /**
     * impl文件包地址
     */
    String implUrl;

    /**
     * mapper文件包地址
     */
    String mapperUrl;

    /**
     * util文件包地址
     */
    String utilUrl;

    /**
     * res文件包地址
     */
    String resUrl;

    /**
     * 说明
     */
    String illustrate;

    /**
     * 作者
     */
    String author;

    /**
     * 实体(驼峰首字母小写)
     */
    String entityStartByLowCase;

    /**
     * 字段信息
     */
    List<ColumnInfo> columnInfos;

    /**
     * 是否开启swagger支持
     */
    private Boolean swaggerEnable;

    /**
     * 是否开启强制覆盖
     */
    private Boolean overWriteEnable;

    /**
     * 是否开启Lombok支持
     */
    private Boolean lombokEnable;

    /**
     * 自定义分层后缀
     */
    private String controllerSuffix = "Controller";
    private String serviceSuffix = "Service";
    private String serviceImplSuffix = "Impl";
    private String mapperSuffix = "Mapper";
    private String dtoSuffix = "Dto";
    private String entitySuffix = "Entity";

    public BasicInfo(BasicInfo basicInfo) {
        this.entityName = basicInfo.getEntityName();
        this.tableName = basicInfo.getTableName();
        this.tableComment = basicInfo.getTableComment();
        this.importPackages = basicInfo.getImportPackages();
        this.packageUrl = basicInfo.getPackageUrl();
        this.dtoUrl = basicInfo.getDtoUrl();
        this.entityUrl = basicInfo.getEntityUrl();
        this.serviceUrl = basicInfo.getServiceUrl();
        this.implUrl = basicInfo.getImplUrl();
        this.mapperUrl = basicInfo.getMapperUrl();
        this.utilUrl = basicInfo.getUtilUrl();
        this.resUrl = basicInfo.getResUrl();
        this.illustrate = basicInfo.getIllustrate();
        this.author = basicInfo.getAuthor();
        this.entityStartByLowCase = basicInfo.getEntityStartByLowCase();
        this.columnInfos = basicInfo.getColumnInfos();
        this.swaggerEnable = basicInfo.getSwaggerEnable();
        this.overWriteEnable = basicInfo.getOverWriteEnable();
        this.lombokEnable = basicInfo.getLombokEnable();
        this.controllerSuffix = basicInfo.getControllerSuffix();
        this.serviceSuffix = basicInfo.getServiceSuffix();
        this.serviceImplSuffix = basicInfo.getServiceImplSuffix();
        this.mapperSuffix = basicInfo.getMapperSuffix();
        this.dtoSuffix = basicInfo.getDtoSuffix();
        this.entitySuffix = basicInfo.getEntitySuffix();
    }

    private String nowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
