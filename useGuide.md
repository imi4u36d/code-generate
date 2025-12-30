# Maven 代码生成插件 - 功能与用法总结

## 项目简介
这是一个 Maven 插件项目，旨在通过配置文件自动化生成基于数据库表的 Java 代码，包括 Controller、Service、ServiceImpl、Entity、DTO、Mapper 和 XML 文件。通过简单的配置，用户可以快速生成项目的基础代码，提高开发效率。

## 支持的功能

### 核心功能
- **自动化代码生成**：根据数据库表结构自动生成 Controller、Service、ServiceImpl、Entity、DTO、Mapper 和 XML 文件。
- **多表同时生成**：支持同时为多个数据库表生成代码，提高开发效率。
- **灵活的配置**：通过 YAML 配置文件，用户可以轻松指定数据库连接信息、需要生成代码的表以及输出路径等。
- **自定义生成模版**：可自定义生成模版，按需调整代码生成规则。
- **支持 Swagger 配置**：可选择是否启用 Swagger 文档生成。
- **代码覆盖控制**：支持覆盖已存在的代码文件（可配置）。
- **超小体积**：45KB 超小体积，小身材，大作用。

### 高级功能
- **自定义属性支持**：通过 `customProperties` 节点配置各种自定义属性。
- **Lombok 支持**：自动添加 Lombok 注解和导入语句。
- **自定义分层后缀**：可自定义各个分层的类名后缀（如 Controller、Service 等）。
- **自定义分层包名称**：可自定义各个分层的包名称（如 dto、entity、service 等）。
- **自定义返回对象**：支持配置自定义的返回对象。
- **DTO 作为请求对象**：新增、修改、删除等操作使用 DTO 作为请求对象。

## 配置文件示例

```yaml
database:
  url: jdbc:mysql://localhost:3306/auth
  username: root
  password: Wz.0323.
  driverClassName: com.mysql.cj.jdbc.Driver

tables:
  - tableName: ad_account_info
  - tableName: ad_account_auth_info

swaggerConfig:
  swaggerEnable: true

outputConfig:
  baseOutputDir: /Users/wangzhuo/Downloads/code
  authorInfo: wz
  packageUrl: com.imi4u36d
  overwriteEnable: true

ftlFileDir: /Users/wangzhuo/Downloads/ftl

# 自定义属性
customProperties:
  # Lombok支持
  lombokEnable: true
  # 自定义分层后缀
  layerSuffix:
    controller: Controller
    service: Service
    serviceImpl: Impl
    mapper: Mapper
    dto: Dto
    entity: Entity
  # 自定义分层包名称
  layerPackageName:
    dto: dto
    entity: domain
    service: service
    serviceImpl: service.impl
    mapper: mapper
    controller: controller
```

## 用法

### 1. 在 Maven 项目中引入插件

在项目的 `pom.xml` 文件中添加以下配置：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.nowork</groupId>
            <artifactId>code-generate</artifactId>
            <version>1.1.1</version>
            <configuration>
                <configFile>src/main/resources/codeGenerate-config.yml</configFile>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 2. 创建配置文件

在 `src/main/resources` 目录下创建 `codeGenerate-config.yml` 文件，并根据实际情况配置数据库连接信息、需要生成代码的表以及输出路径等。

### 3. 执行代码生成

在项目根目录下执行以下命令：

```bash
mvn code-generate:code-generate
```

### 4. 查看生成的代码

生成的代码将输出到配置文件中指定的 `baseOutputDir` 目录下，按照指定的包结构组织。

## 自定义属性说明

| 属性名 | 类型 | 说明 | 默认值 |
|-------|------|------|--------|
| lombokEnable | Boolean | 是否启用 Lombok 支持 | false |
| layerSuffix.controller | String | Controller 类名后缀 | Controller |
| layerSuffix.service | String | Service 接口名后缀 | Service |
| layerSuffix.serviceImpl | String | Service 实现类名后缀 | Impl |
| layerSuffix.mapper | String | Mapper 接口名后缀 | Mapper |
| layerSuffix.dto | String | DTO 类名后缀 | Dto |
| layerSuffix.entity | String | Entity 类名后缀 | Entity |
| layerPackageName.dto | String | DTO 包名称 | dto |
| layerPackageName.entity | String | Entity 包名称 | domain |
| layerPackageName.service | String | Service 包名称 | service |
| layerPackageName.serviceImpl | String | Service 实现包名称 | service.impl |
| layerPackageName.mapper | String | Mapper 包名称 | mapper |
| layerPackageName.controller | String | Controller 包名称 | controller |
| layerPackageName.util | String | Util 包名称 | utils |
| returnObject.packagePath | String | 自定义返回对象的包路径 | com.miaomiao.miaomiaoservice.utils.Result |

## 版本信息

- **swagger** - swagger3
- **jdk** - JDK11
- **mysql** - mysql8.0
- **freemarker** - freemarker2.3.31
- **maven** - maven3.x

## 注意事项

1. 配置文件中的 `baseOutputDir` 和 `ftlFileDir` 需要使用绝对路径。
2. 确保数据库连接信息正确，且数据库中存在需要生成代码的表。
3. 如果使用自定义模板，确保模板文件存在且格式正确。
4. 生成代码前，建议备份已有代码，以免被意外覆盖。

## 常见问题

### Q: 生成的代码没有 Lombok 注解？
A: 请确保在配置文件中设置了 `lombokEnable: true`。

### Q: 如何自定义生成的代码模板？
A: 将自定义模板文件放入 `ftlFileDir` 目录下，插件会自动使用自定义模板。

### Q: 生成的代码包结构不符合预期？
A: 请检查 `layerPackageName` 配置，确保各个分层的包名称设置正确。

### Q: 如何生成特定表的代码？
A: 在 `tables` 节点中添加需要生成代码的表名即可。

通过以上配置和用法，您可以轻松使用该代码生成插件，提高开发效率，减少重复劳动。