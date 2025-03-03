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
        String ftlName = fileType.getFtlName();
        String fileName = getFileName(basicInfo, fileType, outputDir);
        createFile(basicConfig, basicInfo, fileName, ftlName);
    }

    private static String getFileName(BasicInfo basicInfo, FileType fileType, String outputDir) {
        String extensionName = fileType.getExtension();
        String packageName = fileType.getPackageName();
        return outputDir + File.separator + packageName + File.separator + basicInfo.getEntityName() + extensionName;
    }

    private static BaseResModel createFile(BasicConfig basicConfig, BasicInfo basicInfo, String fileName, String ftlName) {
        BaseResModel resModel = new BaseResModel();
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);

        choseFtlMode(basicConfig, ftlName, configuration);

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
                    file.delete();
                    file.createNewFile();
                } else {
                    resModel.setCode(1001);
                    resModel.setContent("文件已存在！");
                    logger.info("文件已存在！ {}", file.getAbsolutePath());
                    return resModel;
                }
            }
            try (FileWriter out = new FileWriter(file)) {
                template.process(basicInfo, out);
            }
            logger.info("生成文件成功: {}", fileName);
        } catch (IOException | TemplateException e) {
            logger.error("生成文件失败", e);
            throw new RuntimeException(e);
        }
        return resModel;
    }

    private static void choseFtlMode(BasicConfig basicConfig, String ftlName, Configuration configuration) {
        if (basicConfig.getFtlFileDirConfig() != null) {
            try {
                String ftlFileDirConfig = basicConfig.getFtlFileDirConfig();
                configuration.setDirectoryForTemplateLoading(new File(ftlFileDirConfig));
                String path = ftlFileDirConfig + File.separator + ftlName;
                File file = new File(path);
                if (!file.exists()) {
                    logger.info("自定义模板路径 {} 下未找到文件 {}，将使用默认模板", ftlFileDirConfig, ftlName);
                    configuration.setClassForTemplateLoading(FreemarkerUtils.class, "/templates");
                }
            } catch (IOException e) {
                logger.error("加载自定义模板配置失败", e);
                throw new RuntimeException(e);
            }
        } else {
            configuration.setClassForTemplateLoading(FreemarkerUtils.class, "/templates");
        }
    }
}