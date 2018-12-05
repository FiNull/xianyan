package cn.finull.framework.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务管理
 */
public class BasicTransactionManager implements TransactionManager {

    // 存放连接
    private static final ThreadLocal<Connection> TL = new ThreadLocal<>();

    private DataSource dataSource;

    public void init() {
        // 通过 FiNull框架将数据源注入进来
        dataSource = get(DataSource.class);
    }

    @Override
    public Connection getConnection() {
        // 从当前线程中获取数据库连接
        Connection conn = TL.get();
        if (conn == null) {
            try {
                // 从数据池中获取数据库连接
                conn = dataSource.getConnection();
                // 将数据库连接放入当前线程中
                TL.set(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    @Override
    public void transaction() {
        Connection conn = getConnection();
        try {
            // 开启事务
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollback() {
        Connection conn = getConnection();
        try {
            // 事务回滚
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void commit() {
        Connection conn = getConnection();
        try {
            // 提交事务
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        Connection conn = getConnection();
        try {
            // 从当前线程中删除该连接
            TL.remove();
            // 关闭连接
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
