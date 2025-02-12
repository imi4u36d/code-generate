# Maven 代码生成插件

## 项目简介
这是一个 Maven 插件项目，旨在通过配置文件自动化生成基于数据库表的 Java 代码，包括 Controller、Service、ServiceImpl、Entity、DTO、Mapper 和 XML 文件。通过简单的配置，用户可以快速生成项目的基础代码，提高开发效率。

## 功能特点
- **自动化代码生成**：根据数据库表结构自动生成 Controller、Service、ServiceImpl、Entity、DTO、Mapper 和 XML 文件。
- **多表同时生成**：更高效的多表同时生成
- **灵活的配置**：通过 YAML 配置文件，用户可以轻松指定数据库连接信息、需要生成代码的表以及输出路径等。
- **支持 Swagger 配置**：可选择是否启用 Swagger 文档生成。
- **代码覆盖控制**：支持覆盖已存在的代码文件（可配置）。

## 配置文件示例
以下是配置文件的示例，用户需要根据实际情况进行修改：

```yaml
database:
  url: jdbc:mysql://localhost:3306/auth
  username: root
  password: root
  driverClassName: com.mysql.cj.jdbc.Driver

tables:
  - tableName: ad_account_info
  - tableName: ad_account_auth_info

swaggerConfig:
  swaggerEnable: false

outputConfig:
  baseOutputDir: /Users/wangzhuo/Downloads/code
  authorInfo: wz
  packageUrl: com.imi4u36d
  overwriteEnable: true