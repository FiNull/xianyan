package cn.finull.framework.db;

import cn.finull.framework.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnection {

    private static final Logger LOG = LoggerFactory.getLogger(JDBCConnection.class);

    private JDBCConnection() {
    }

    public static JDBCConnection getInstance() {
        return new JDBCConnection();
    }

    static {
        try {
            // 加载驱动
            Class.forName(AppConfig.getDBDriver());
        } catch (ClassNotFoundException e) {
            LOG.error("Database driver error: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     */
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(AppConfig.getDBUrl(), AppConfig.getDBUsername(), AppConfig.getDBPassword());
        } catch (SQLException e) {
            LOG.error("Get Database connection failure: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
