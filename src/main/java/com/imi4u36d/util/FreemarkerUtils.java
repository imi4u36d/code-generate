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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangzhuo
 */
public class FreemarkerUtils {
    private static final Logger logger = LoggerFactory.getLogger(FreemarkerUtils.class);

    // 模板配置缓存，key为模板目录路径
    private static final Map<String, Configuration> CONFIGURATION_CACHE = new ConcurrentHashMap<>();

    // 模板缓存，key为模板目录路径+模板名称
    private static final Map<String, Template> TEMPLATE_CACHE = new ConcurrentHashMap<>();

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

    private static BaseResModel createFile(BasicConfig basicConfig, BasicInfo basicInfo, String fileName,
            String ftlName) {
        BaseResModel resModel = new BaseResModel();

        // 获取模板目录标识
        String templateDirKey = getTemplateDirKey(basicConfig);

        // 获取或创建Configuration对象
        Configuration configuration = getOrCreateConfiguration(basicConfig, templateDirKey);

        // 获取或加载模板
        Template template = getOrLoadTemplate(configuration, templateDirKey, ftlName);

        try {
            File file = new File(fileName);
            logger.debug("生成文件: {}", file.getAbsolutePath());

            if (!file.getParentFile().exists()) {
                logger.debug("创建父目录: {}", file.getParentFile().getAbsolutePath());
                file.getParentFile().mkdirs();
            }

            if (file.exists()) {
                if (basicConfig.getOverWriteEnable()) {
                    logger.info("覆写已有文件: {}", file.getAbsolutePath());
                    file.delete();
                } else {
                    resModel.setCode(1001);
                    resModel.setContent("文件已存在！");
                    logger.info("文件已存在，跳过生成: {}", file.getAbsolutePath());
                    return resModel;
                }
            }

            file.createNewFile();
            try (FileWriter out = new FileWriter(file)) {
                template.process(basicInfo, out);
            }
            logger.info("生成文件成功: {}", file.getAbsolutePath());
        } catch (IOException e) {
            logger.error("文件操作失败: {}", fileName, e);
            throw new RuntimeException("文件操作失败", e);
        } catch (TemplateException e) {
            logger.error("模板处理失败: {}", ftlName, e);
            throw new RuntimeException("模板处理失败", e);
        } catch (Exception e) {
            logger.error("生成文件失败: {}", fileName, e);
            throw new RuntimeException("生成文件失败", e);
        }
        return resModel;
    }

    /**
     * 获取模板目录标识
     */
    private static String getTemplateDirKey(BasicConfig basicConfig) {
        return basicConfig.getFtlFileDirConfig() == null ? "default" : basicConfig.getFtlFileDirConfig();
    }

    /**
     * 获取或创建Configuration对象
     */
    private static Configuration getOrCreateConfiguration(BasicConfig basicConfig, String templateDirKey) {
        return CONFIGURATION_CACHE.computeIfAbsent(templateDirKey, key -> {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
            choseFtlMode(basicConfig, "", configuration);
            logger.info("创建并缓存模板配置: {}", templateDirKey);
            return configuration;
        });
    }

    /**
     * 获取或加载模板
     */
    private static Template getOrLoadTemplate(Configuration configuration, String templateDirKey, String ftlName) {
        String templateKey = templateDirKey + ":" + ftlName;

        return TEMPLATE_CACHE.computeIfAbsent(templateKey, key -> {
            try {
                logger.debug("加载并缓存模板: {}", ftlName);
                return configuration.getTemplate(ftlName);
            } catch (IOException e) {
                logger.error("加载模板失败: {}", ftlName, e);
                throw new RuntimeException("加载模板失败", e);
            }
        });
    }

    private static void choseFtlMode(BasicConfig basicConfig, String ftlName, Configuration configuration) {
        if (basicConfig.getFtlFileDirConfig() != null) {
            try {
                String ftlFileDirConfig = basicConfig.getFtlFileDirConfig();
                configuration.setDirectoryForTemplateLoading(new File(ftlFileDirConfig));
                String path = ftlFileDirConfig + File.separator + ftlName;
                File file = new File(path);
                if (!ftlName.isEmpty() && !file.exists()) {
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