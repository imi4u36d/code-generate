# Maven 代码生成插件

## 项目简介

这是一个 Maven 插件项目，旨在通过配置文件自动化生成基于数据库表的 Java 代码，包括 Controller、Service、ServiceImpl、Entity、DTO、Mapper 和 XML 文件。通过简单的配置，用户可以快速生成项目的基础代码，提高开发效率。

## 功能特点

- **自动化代码生成**：根据数据库表结构自动生成 Controller、Service、ServiceImpl、Entity、DTO、Mapper 和 XML 文件。
- **多表同时生成**：支持同时为多个数据库表生成代码，提高开发效率。
- **灵活的配置**：通过 YAML 配置文件，用户可以轻松指定数据库连接信息、需要生成代码的表以及输出路径等。
- **自定义生成模版**：可自定义生成模版，按需调整。
- **多数据库支持**：支持 MySQL、PostgreSQL、Oracle、SQL Server、H2 等多种数据库。
- **API 文档增强**：支持 Swagger、OpenAPI、AsyncAPI 等多种 API 文档格式和版本。
- **代码覆盖控制**：支持覆盖已存在的代码文件（可配置）。
- **超小体积**：45KB 超小体积，小身材，大作用。
- **Lombok 支持**：自动添加 Lombok 注解和导入语句。
- **自定义分层后缀**：可自定义各个分层的类名后缀。
- **自定义分层包名称**：可自定义各个分层的包名称。
- **自定义返回对象**：支持配置自定义的返回对象。
- **DTO 作为请求对象**：新增、修改、删除等操作使用 DTO 作为请求对象。

## 快速开始

### 1. 配置文件示例

在项目的 `src/main/resources` 目录下创建 `codeGenerate-config.yml` 文件：

```yaml
database:
  url: jdbc:mysql://localhost:3306/your-db-name
  username: your-username
  password: your-password
  driverClassName: com.mysql.cj.jdbc.Driver

tables:
  - tableName: your_table_name

swaggerConfig:
  swaggerEnable: true
  # API文档类型：swagger, openapi, asyncapi（默认：swagger）
  apiDocType: swagger
  # API文档版本（默认：3.0）
  apiDocVersion: 3.0

outputConfig:
  baseOutputDir: /path/to/output
  authorInfo: your-name
  packageUrl: com.yourcompany
  overwriteEnable: true

# 自定义模板目录（可选，不配置则使用默认模板）
# ftlFileDir: /path/to/your/templates

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

### 2. 执行生成命令

在项目根目录下运行以下命令：

```bash
mvn code-generate:code-generate
```

### 3. 生成效果

自动生成完整的代码结构，包括以下文件：

```
output-directory
├── src
│   └── main
│       └── java
│           └── com
│               └── yourcompany
│                   └── res
│                       ├── BaseResponseDto.java
│                       └── Result.java
└── your_table_name
    └── src
        └── main
            ├── java
            │   └── com
            │       └── yourcompany
            │           ├── controller
            │           │   └── YourTableNameController.java
            │           ├── domain
            │           │   └── YourTableNameEntity.java
            │           ├── dto
            │           │   └── YourTableNameDto.java
            │           ├── mapper
            │           │   └── YourTableNameMapper.java
            │           └── service
            │               ├── YourTableNameService.java
            │               └── impl
            │                   └── YourTableNameServiceImpl.java
            └── resources
                └── mapper
                    └── YourTableNameMapper.xml
```

### 4. 核心代码示例

生成的 Controller 包含完整的 CRUD 接口：

```java
package com.yourcompany.controller;

import com.yourcompany.dto.YourTableNameDto;
import com.yourcompany.res.BaseResponseDto;
import com.yourcompany.res.Result;
import com.yourcompany.service.YourTableNameService;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;

/**
* @说明: YourTableName相关接口
* @作者: your-name
* @创建时间: 2024-01-01 12:00:00
*/
@RestController
@Api(tags = {"YourTableName相关接口"})
@RequestMapping("/api/yourTableName")
public class YourTableNameController {

    private final YourTableNameService yourTableNameService;

    public YourTableNameController(YourTableNameService yourTableNameService) {
        this.yourTableNameService = yourTableNameService;
    }

    /**
    * 查询列表
    */
    @ApiOperation("查询列表")
    @PostMapping("/list")
    public Result list(@RequestBody YourTableNameDto yourTableName) {
        return yourTableNameService.list(yourTableName);
    }

    /**
    * 通过id查询对象
    */
    @ApiOperation("通过id查询对象")
    @GetMapping("/selById/{id}")
    public Result selById(@ApiParam(name = "id", value = "需要查询数据的id") @PathVariable Long id) {
        return yourTableNameService.selById(id);
    }

    /**
    * 新增
    */
    @ApiOperation("新增")
    @PostMapping("/add")
    public Result add(@RequestBody YourTableNameDto yourTableName) {
        return yourTableNameService.add(yourTableName);
    }

    /**
    * 更新
    */
    @ApiOperation("更新")
    @PutMapping("/update")
    public Result update(@RequestBody YourTableNameDto yourTableName) {
        return yourTableNameService.update(yourTableName);
    }

    /**
    * 通过id删除
    */
    @ApiOperation("通过id删除")
    @PutMapping("/delById/{id}")
    public Result delById(@ApiParam(name = "id", value = "需要删除数据的id") @PathVariable Long id) {
        return yourTableNameService.delById(id);
    }

    /**
    * 批量删除
    */
    @ApiOperation("批量删除")
    @PutMapping("/delBatchByIdList")
    public Result delBatchByIdList(@RequestBody List ids) {
        return yourTableNameService.delBatchByIdList(ids);
    }

    /**
    * 分页列表查询
    */
    @ApiOperation("分页列表查询")
    @PostMapping("/page/{pageNum}/{pageSize}")
    public Result page(
        @RequestBody YourTableNameDto yourTableName,
        @ApiParam(name = "pageNum", value = "页码") @PathVariable int pageNum,
        @ApiParam(name = "pageSize", value = "每页数量") @PathVariable int pageSize
    ) {
        return yourTableNameService.page(yourTableName, pageNum, pageSize);
    }
}
```

## 详细用法

请查看 [使用指南](useGuide.md) 文件，获取详细的使用说明和自定义配置选项。

## 版本备注

- **swagger** - swagger3
- **jdk** - JDK11
- **mysql** - mysql8.0
- **freemarker** - freemarker2.3.31
- **maven** - maven3.x
