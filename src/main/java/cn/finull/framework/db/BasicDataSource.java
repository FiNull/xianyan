package cn.finull.framework.db;

import cn.finull.framework.config.AppConfig;
import cn.finull.framework.core.bean.Bean;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * 数据源
 */
public class BasicDataSource implements DataSource,Bean {

    @Override
    public Class getClassKey() {
        return DataSource.class;
    }

    // 存放数据库连接的队列
    private static List<Connection> DATA_SOURCE = Collections.synchronizedList(new LinkedList<>());

    static {
        // 向连接池中添加连接
        IntStream.range(0,AppConfig.getDBPool())
                .forEach(i -> DATA_SOURCE.add(JDBCConnection.getInstance().getConnection()));
    }

    @Override
    public Connection getConnection() {
        if (DATA_SOURCE.isEmpty()) {
            throw new RuntimeException("Unable get database connection!");
        }
        else {
            Connection conn = DATA_SOURCE.remove(0);
            // 对Connection 进行动态代理
            return (Connection) Proxy.newProxyInstance(conn.getClass().getClassLoader(),
                    conn.getClass().getInterfaces(), (p,m,a) -> {
                        Object re = null;
                        if ("close".equals(m.getName())) {
                            // 用户调用close()方法时步关闭连接，而是将连接放回连接池
                            DATA_SOURCE.add(conn);
                        }
                        else {
                            re = m.invoke(conn,a);
                        }
                        return re;
            });
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
