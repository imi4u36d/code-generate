package com.imi4u36d.util;

import com.imi4u36d.config.DBConfiguration;
import com.imi4u36d.model.BasicInfo;
import com.imi4u36d.model.ColumnInfo;
import com.imi4u36d.model.ColumnType;
import com.imi4u36d.model.DatabaseType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author wangzhuo
 */
public class DBUtils {

    private static final Logger logger = LoggerFactory.getLogger(DBUtils.class);
    private static final String SQL = "SELECT * FROM ";
    private static final DBUtils INSTANCE = new DBUtils();

    private DBConfiguration dbConfiguration;
    private HikariDataSource dataSource;
    @Getter
    private final Map<String, BasicInfo> tableInfoMap = new ConcurrentHashMap<>();

    /**
     * 私有构造方法，防止外部实例化
     */
    private DBUtils() {
    }

    /**
     * 获取单例实例
     *
     * @return DBUtils实例
     */
    public static DBUtils getInstance() {
        return INSTANCE;
    }

    /**
     * 设置数据库配置
     *
     * @param dbConfig 数据库配置
     */
    public void setDbConfiguration(DBConfiguration dbConfig) {
        this.dbConfiguration = dbConfig;
    }

    /**
     * 初始化数据库连接池
     */
    private void initDataSource() {
        if (dataSource == null) {
            synchronized (this) {
                if (dataSource == null) {
                    try {
                        if (dbConfiguration == null) {
                            throw new IllegalStateException("Database configuration is not set");
                        }
                        HikariConfig config = new HikariConfig();
                        config.setDriverClassName(dbConfiguration.getDriverClassName());
                        config.setJdbcUrl(dbConfiguration.getUrl());
                        config.setUsername(dbConfiguration.getUsername());
                        config.setPassword(dbConfiguration.getPwd());

                        // 设置连接池参数
                        config.setMaximumPoolSize(10);
                        config.setMinimumIdle(5);
                        config.setConnectionTimeout(30000);
                        config.setIdleTimeout(600000);
                        config.setMaxLifetime(1800000);

                        dataSource = new HikariDataSource(config);
                        logger.info("数据库连接池初始化成功");
                    } catch (Exception e) {
                        logger.error("数据库连接池初始化失败", e);
                        throw new RuntimeException("Failed to initialize data source", e);
                    }
                }
            }
        }
    }

    /**
     * 获取数据库连接
     *
     * @return Connection
     */
    private Connection getConnection() {
        initDataSource();
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("获取数据库连接失败", e);
            throw new RuntimeException("Failed to get connection", e);
        }
    }

    /**
     * 关闭数据库连接池
     */
    public void closeConnection() {
        if (dataSource != null) {
            dataSource.close();
            logger.info("数据库连接池已关闭");
        }
    }

    /**
     * 获得某表的注释
     *
     * @param tableName
     * @return 表的注释
     */
    private String getCommentByTableName(String tableName) {
        String comment = "";

        // 尝试使用标准JDBC方法获取表注释
        try (Connection conn = getConnection();
                ResultSet rs = conn.getMetaData().getTables(null, null, tableName, new String[] { "TABLE" })) {
            if (rs.next()) {
                comment = rs.getString("REMARKS");
                if (comment != null && !comment.isEmpty()) {
                    return comment;
                }
            }
        } catch (SQLException e) {
            logger.error("使用标准JDBC方法获取表注释失败: {}", tableName, e);
        }

        // 如果标准JDBC方法失败或未获取到注释，尝试使用数据库特定的方法
        try (Connection conn = getConnection()) {
            comment = getTableCommentBySpecificDB(tableName, conn);
        } catch (SQLException e) {
            logger.error("使用特定数据库方法获取表注释失败: {}", tableName, e);
        }

        return comment != null ? comment : "";
    }

    /**
     * 针对不同数据库使用特定的方法获取表注释
     */
    private String getTableCommentBySpecificDB(String tableName, Connection conn) {
        try {
            DatabaseType dbType = DatabaseType.fromJdbcUrl(dbConfiguration.getUrl());

            switch (dbType) {
                case MYSQL:
                    return getMySQLTableComment(tableName, conn);
                case POSTGRESQL:
                    return getPostgreSQLTableComment(tableName, conn);
                case ORACLE:
                    return getOracleTableComment(tableName, conn);
                case SQL_SERVER:
                    return getSqlServerTableComment(tableName, conn);
                case H2:
                    return getH2TableComment(tableName, conn);
                default:
                    logger.warn("不支持的数据库类型: {}", dbType.getName());
                    return "";
            }
        } catch (SQLException e) {
            logger.error("获取表注释失败: {}", tableName, e);
            return "";
        }
    }

    /**
     * 获取MySQL表注释
     */
    private String getMySQLTableComment(String tableName, Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE " + tableName)) {
            if (rs != null && rs.next()) {
                String createDDL = rs.getString(2);
                int index = createDDL.indexOf("COMMENT='");
                if (index < 0) {
                    return "";
                }
                String comment = createDDL.substring(index + 9);
                comment = comment.substring(0, comment.indexOf("'", index + 9));
                return comment;
            }
        }
        return "";
    }

    /**
     * 获取PostgreSQL表注释
     */
    private String getPostgreSQLTableComment(String tableName, Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT obj_description(oid, 'pg_class') FROM pg_class WHERE relname = ?");) {
            pstmt.setString(1, tableName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String comment = rs.getString(1);
                    return comment != null ? comment : "";
                }
            }
        }
        return "";
    }

    /**
     * 获取Oracle表注释
     */
    private String getOracleTableComment(String tableName, Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT comments FROM user_tab_comments WHERE table_name = ?");) {
            pstmt.setString(1, tableName.toUpperCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String comment = rs.getString(1);
                    return comment != null ? comment : "";
                }
            }
        }
        return "";
    }

    /**
     * 获取SQL Server表注释
     */
    private String getSqlServerTableComment(String tableName, Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT value FROM sys.extended_properties WHERE major_id = OBJECT_ID(?) AND name = 'MS_Description' AND minor_id = 0");) {
            pstmt.setString(1, tableName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String comment = rs.getString(1);
                    return comment != null ? comment : "";
                }
            }
        }
        return "";
    }

    /**
     * 获取H2表注释
     */
    private String getH2TableComment(String tableName, Connection conn) throws SQLException {
        // H2支持SHOW CREATE TABLE语法，类似于MySQL
        return getMySQLTableComment(tableName, conn);
    }

    /**
     * 获取数据库下的所有表名
     */
    private List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        try (Connection conn = getConnection();
                ResultSet rs = conn.getMetaData().getTables(null, null, null, null)) {
            // 从元数据中获取到所有的表名
            while (rs.next()) {
                tableNames.add(rs.getString(3));
            }
        } catch (SQLException e) {
            logger.error("获取表名列表失败", e);
        }
        return tableNames;
    }

    /**
     * 获取表中所有字段名称
     *
     * @param tableName 表名
     * @return
     */
    private List<String> getColumnNames(String tableName) {
        List<String> columnNames = new ArrayList<>();
        String tableSql = SQL + tableName;
        try (Connection conn = getConnection();
                PreparedStatement pStemt = conn.prepareStatement(tableSql)) {
            // 结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            // 表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnNames.add(rsmd.getColumnName(i + 1));
            }
        } catch (SQLException e) {
            logger.error("获取字段名称失败: {}", tableName, e);
        }
        return columnNames;
    }

    /**
     * 获取表中所有字段类型
     *
     * @param tableName
     * @return
     */
    private List<String> getColumnTypes(String tableName) {
        List<String> columnTypes = new ArrayList<>();
        String tableSql = SQL + tableName;
        try (Connection conn = getConnection();
                PreparedStatement pStemt = conn.prepareStatement(tableSql)) {
            // 结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            // 表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnTypes.add(rsmd.getColumnTypeName(i + 1));
            }
        } catch (SQLException e) {
            logger.error("获取字段类型失败: {}", tableName, e);
        }
        return columnTypes;
    }

    /**
     * 获取表中字段的所有注释
     *
     * @param tableName
     * @return
     */
    private List<String> getColumnComments(String tableName) {
        List<String> columnComments = new ArrayList<>();// 列名注释集合
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("show full columns from " + tableName)) {
            while (rs.next()) {
                columnComments.add(rs.getString("Comment"));
            }
        } catch (SQLException e) {
            logger.error("获取字段注释失败: {}", tableName, e);
        }
        return columnComments;
    }

    /**
     * 扫描表信息存储到数据模型种
     */
    public void scanInfoToModel(BasicInfo basicInfo) {
        List<String> tableNames = dbConfiguration.getTableNames();
        logger.info("正在加载表：" + tableNames.toString());
        tableInfoMap.clear(); // 清空之前的表信息

        try (Connection conn = getConnection()) {
            DatabaseMetaData dbMetaData = conn.getMetaData();

            for (String tableName : tableNames) {
                logger.info("开始加载:" + tableName);
                // 获取表注释
                String tableComment = getCommentByTableName(tableName);
                // 获取实体类名称
                String entityName = CovertUtils.underline2Camel(tableName, true);
                String entityNameStartByLowCase = CovertUtils.underline2Camel(tableName, false);
                // 设置一些常用的参数数据
                BasicInfo curBasicInfo = new BasicInfo(basicInfo);
                curBasicInfo.setEntityName(entityName);
                curBasicInfo.setTableName(tableName);
                curBasicInfo.setTableComment(tableComment);
                curBasicInfo.setEntityStartByLowCase(entityNameStartByLowCase);

                // 一次查询获取所有字段信息
                List<ColumnInfo> columnInfos = new ArrayList<>();
                Set<String> importPackages = new HashSet<>();

                // 如果开启Lombok支持，添加Lombok的import包
                if (basicInfo.getLombokEnable()) {
                    importPackages.add("lombok.Data");
                    importPackages.add("lombok.NoArgsConstructor");
                    importPackages.add("lombok.AllArgsConstructor");
                }

                // 获取字段信息（名称、类型）
                try (ResultSet columnsRs = dbMetaData.getColumns(null, null, tableName, null)) {
                    // 同时获取字段注释
                    Map<String, String> columnCommentMap = getColumnCommentMap(tableName, conn);

                    while (columnsRs.next()) {
                        String columnName = columnsRs.getString(4); // COLUMN_NAME
                        String columnType = columnsRs.getString(6); // TYPE_NAME
                        String columnComment = columnCommentMap.getOrDefault(columnName, "");

                        ColumnInfo.ColumnInfoBuilder builder = ColumnInfo.builder();
                        ColumnInfo columnInfo = builder.columnName(columnName)
                                .columnType(columnType)
                                .javaName(CovertUtils.underline2Camel(columnName, false))
                                .javaType(ColumnType.valueOf(columnType).getFieldType())
                                .columnComment(columnComment)
                                .build();
                        columnInfos.add(columnInfo);

                        // 添加需要导包的数据类型
                        String packageName = ColumnType.valueOf(columnType).getPackageName();
                        if (!packageName.isEmpty()) {
                            importPackages.add(packageName);
                        }
                    }
                }

                // 设定详细的包信息/列信息
                List<String> importInfos = importPackages.stream().collect(Collectors.toList());
                curBasicInfo.setImportPackages(importInfos);
                curBasicInfo.setColumnInfos(columnInfos);
                tableInfoMap.put(tableName, curBasicInfo);
                logger.info("加载完成:" + tableName);
            }
        } catch (SQLException e) {
            logger.error("获取表信息失败", e);
            throw new RuntimeException("Failed to get table information", e);
        }

        logger.info("所有表加载完成，共加载了 {} 张表", tableInfoMap.size());
    }

    /**
     * 批量获取字段注释
     */
    private Map<String, String> getColumnCommentMap(String tableName, Connection conn) {
        Map<String, String> commentMap = new HashMap<>();

        // 尝试使用标准JDBC方法获取字段注释
        try (ResultSet columnsRs = conn.getMetaData().getColumns(null, null, tableName, null)) {
            while (columnsRs.next()) {
                String columnName = columnsRs.getString("COLUMN_NAME");
                String columnComment = columnsRs.getString("REMARKS");
                commentMap.put(columnName, columnComment != null ? columnComment : "");
            }
        } catch (SQLException e) {
            logger.error("使用标准JDBC方法获取字段注释失败: {}", tableName, e);
        }

        // 如果标准JDBC方法未获取到注释，尝试使用数据库特定的方法
        boolean hasComments = commentMap.values().stream().anyMatch(comment -> !comment.isEmpty());
        if (!hasComments) {
            commentMap = getColumnCommentBySpecificDB(tableName, conn);
        }

        return commentMap;
    }

    /**
     * 针对不同数据库使用特定的方法获取字段注释
     */
    private Map<String, String> getColumnCommentBySpecificDB(String tableName, Connection conn) {
        Map<String, String> commentMap = new HashMap<>();

        try {
            DatabaseType dbType = DatabaseType.fromJdbcUrl(dbConfiguration.getUrl());

            switch (dbType) {
                case MYSQL:
                    return getMySQLColumnComments(tableName, conn);
                case POSTGRESQL:
                    return getPostgreSQLColumnComments(tableName, conn);
                case ORACLE:
                    return getOracleColumnComments(tableName, conn);
                case SQL_SERVER:
                    return getSqlServerColumnComments(tableName, conn);
                case H2:
                    return getH2ColumnComments(tableName, conn);
                default:
                    logger.warn("不支持的数据库类型: {}", dbType.getName());
                    return commentMap;
            }
        } catch (SQLException e) {
            logger.error("获取字段注释失败: {}", tableName, e);
            return commentMap;
        }
    }

    /**
     * 获取MySQL字段注释
     */
    private Map<String, String> getMySQLColumnComments(String tableName, Connection conn) throws SQLException {
        Map<String, String> commentMap = new HashMap<>();

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SHOW FULL COLUMNS FROM " + tableName)) {
            while (rs.next()) {
                String columnName = rs.getString("Field");
                String comment = rs.getString("Comment");
                commentMap.put(columnName, comment != null ? comment : "");
            }
        }

        return commentMap;
    }

    /**
     * 获取PostgreSQL字段注释
     */
    private Map<String, String> getPostgreSQLColumnComments(String tableName, Connection conn) throws SQLException {
        Map<String, String> commentMap = new HashMap<>();

        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT column_name, column_comment FROM information_schema.columns WHERE table_name = ?")) {
            pstmt.setString(1, tableName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String columnName = rs.getString("column_name");
                    String comment = rs.getString("column_comment");
                    commentMap.put(columnName, comment != null ? comment : "");
                }
            }
        }

        return commentMap;
    }

    /**
     * 获取Oracle字段注释
     */
    private Map<String, String> getOracleColumnComments(String tableName, Connection conn) throws SQLException {
        Map<String, String> commentMap = new HashMap<>();

        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT column_name, comments FROM user_col_comments WHERE table_name = ?")) {
            pstmt.setString(1, tableName.toUpperCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String columnName = rs.getString("column_name");
                    String comment = rs.getString("comments");
                    commentMap.put(columnName.toLowerCase(), comment != null ? comment : "");
                }
            }
        }

        return commentMap;
    }

    /**
     * 获取SQL Server字段注释
     */
    private Map<String, String> getSqlServerColumnComments(String tableName, Connection conn) throws SQLException {
        Map<String, String> commentMap = new HashMap<>();

        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT c.name AS column_name, ep.value AS column_comment " +
                        "FROM sys.columns c " +
                        "LEFT JOIN sys.extended_properties ep ON ep.major_id = c.object_id AND ep.minor_id = c.column_id AND ep.name = 'MS_Description' "
                        +
                        "WHERE OBJECT_NAME(c.object_id) = ?")) {
            pstmt.setString(1, tableName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String columnName = rs.getString("column_name");
                    String comment = rs.getString("column_comment");
                    commentMap.put(columnName, comment != null ? comment : "");
                }
            }
        }

        return commentMap;
    }

    /**
     * 获取H2字段注释
     */
    private Map<String, String> getH2ColumnComments(String tableName, Connection conn) throws SQLException {
        // H2支持SHOW FULL COLUMNS语法，类似于MySQL
        return getMySQLColumnComments(tableName, conn);
    }

}
