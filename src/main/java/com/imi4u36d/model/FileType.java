package com.imi4u36d.model;

/**
 * @author wangzhuo
 */

public enum FileType {
    CONTROLLER("Controller.java", "controller.ftl", "controller"),
    ENTITY(".java", "entity.ftl", "entity"),
    SERVICE("Service.java", "service.ftl", "service"),
    IMPL("ServiceImpl.java", "impl.ftl", "service"),
    MAPPER("Mapper.java", "mapper.ftl", "mapper"),
    XML("Mapper.xml", "xml.ftl", "mapper"),
    DTO("Dto.java", "dto.ftl", "dto"),
    BASERESDTO("BaseResponseDto.java", "baseResponseDto.ftl", "res"),
    RES("Result.java", "result.ftl", "res");

    private final String extension;
    private final String ftlName;
    private final String packageName;

    FileType(String extension, String ftlName, String packageName) {
        this.extension = extension;
        this.ftlName = ftlName;
        this.packageName = packageName;
    }

    public String getExtension() {
        return extension;
    }

    public String getFtlName() {
        return ftlName;
    }

    public String getPackageName() {
        return packageName;
    }
}