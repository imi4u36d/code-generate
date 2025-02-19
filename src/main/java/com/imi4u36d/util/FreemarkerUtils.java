package com.imi4u36d.util;


import com.imi4u36d.model.BaseResModel;
import com.imi4u36d.model.BasicConfig;
import com.imi4u36d.model.BasicInfo;
import com.imi4u36d.model.FileType;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author wangzhuo
 */
public class FreemarkerUtils {
    private static final Logger logger = LoggerFactory.getLogger(FreemarkerUtils.class);

    public static void ftlToFile(BasicConfig basicConfig, BasicInfo basicInfo, FileType fileType, String outputDir) {
        if (fileType.equals(FileType.CONTROLLER)) {
            createFile(basicConfig, basicInfo, outputDir + File.separator + basicInfo.getEntityName() + "Controller.java", "controller.ftl");
        } else if (fileType.equals(FileType.ENTITY)) {
            createFile(basicConfig, basicInfo, outputDir + File.separator + basicInfo.getEntityName() + ".java", "entity.ftl");
        } else if (fileType.equals(FileType.SERVICE)) {
            createFile(basicConfig, basicInfo, outputDir + File.separator + basicInfo.getEntityName() + "Service.java", "service.ftl");
        } else if (fileType.equals(FileType.IMPL)) {
            createFile(basicConfig, basicInfo, outputDir + File.separator + basicInfo.getEntityName() + "ServiceImpl.java", "impl.ftl");
        } else if (fileType.equals(FileType.MAPPER)) {
            createFile(basicConfig, basicInfo, outputDir + File.separator + basicInfo.getEntityName() + "Mapper.java", "mapper.ftl");
        } else if (fileType.equals(FileType.XML)) {
            createFile(basicConfig, basicInfo, outputDir + File.separator + basicInfo.getEntityName() + "Mapper.xml", "xml.ftl");
        } else if (fileType.equals(FileType.DTO)) {
            createFile(basicConfig, basicInfo, outputDir + File.separator + basicInfo.getEntityName() + "Dto.java", "dto.ftl");
        } else if (fileType.equals(FileType.BASERESDTO)) {
            createFile(basicConfig, basicInfo, outputDir + File.separator + "BaseResponseDto.java", "baseResponseDto.ftl");
        } else if (fileType.equals(FileType.RES)) {
            createFile(basicConfig, basicInfo, outputDir + File.separator + "Result.java", "result.ftl");
        }
    }

    private static BaseResModel createFile(BasicConfig basicConfig, BasicInfo basicInfo, String fileName, String ftlName) {
        BaseResModel resModel = new BaseResModel();
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        if (basicConfig.getFtlFileDirConfig() != null) {
            try {
                String ftlFileDirConfig = basicConfig.getFtlFileDirConfig();
                configuration.setDirectoryForTemplateLoading(new File(ftlFileDirConfig));
                // 从当前绝对路径下进行寻找，如果找的到就使用，否则退回默认配置
                String path = basicConfig.getFtlFileDirConfig() + File.separator + ftlName;
                File file = new File(path);
                if (!file.exists()) {
                    // 文件不存在
                    logger.info("{}下未找到自定义{}，开始使用默认ftl生成", basicConfig.getFtlFileDirConfig(), ftlName);
                    configuration.setClassForTemplateLoading(FreemarkerUtils.class, "/templates");
                }
            } catch (IOException e) {
                logger.error("加载自定义ftl配置失败", e);
                throw new RuntimeException(e);
            }
        } else {
            configuration.setClassForTemplateLoading(FreemarkerUtils.class, "/templates");
        }
        Template template;
        try {
            template = configuration.getTemplate(ftlName);
            File file = new File(fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            } else {
                if (basicConfig.getOverWriteEnable()) {
                    // 删除当前文件
                    file.delete();
                    file.createNewFile();
                } else {
                    resModel.setCode(1001);
                    resModel.setContent("文件已存在！");
                    logger.info("文件已存在！ " + file.getAbsolutePath());
                    return resModel;
                }
            }
            // 创建输出流
            FileWriter out = new FileWriter(file);
            // 生成文件
            template.process(basicInfo, out);
            logger.info("success ==> {}", fileName);

        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
        return resModel;
    }

}
