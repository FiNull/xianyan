package cn.finull.framework.db;

import cn.finull.framework.core.bean.Bean;
import java.sql.Connection;

public interface TransactionManager extends Bean {

    @Override
    default Class getClassKey() {
        return TransactionManager.class;
    }

    /**
     * 获得数据库连接
     * @return 数据库连接
     */
    Connection getConnection();

    /**
     * 开启事务
     */
    void transaction();

    /**
     * 事务回滚
     */
    void rollback();

    /**
     * 提交事务
     */
    void commit();

    /**
     * 关闭连接
     */
    void close();
}
