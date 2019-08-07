package cn.finull.framework.core;

import cn.finull.framework.core.bean.Bean;
import cn.finull.framework.core.bean.Service;
import cn.finull.framework.db.TransactionManager;
import cn.finull.framework.db.annotation.Transactional;
import cn.finull.framework.db.orm.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * 仓库
 * 存放bean
 */
public class BeanRepertory {

    private static final Logger LOG = LoggerFactory.getLogger(BeanRepertory.class);

    private BeanRepertory() {
    }

    private static BeanRepertory beanRepertory;

    public static BeanRepertory getInstance() {
        if (beanRepertory == null) {
            beanRepertory = new BeanRepertory();
        }
        return beanRepertory;
    }

    private Map<Class, Bean> repertory = new HashMap<>();

    private TransactionManager transactionManager;

    public void init() {
        repertory.forEach((k, v) -> v.init());
        transactionManager = (TransactionManager) repertory.get(TransactionManager.class);
    }

    /**
     * 向仓库中添加bean
     *
     * @param value bean
     */
    public void add(Bean value) {
        repertory.put(value.getClassKey(), value);
    }

    /**
     * 向仓库中添加bean
     *
     * @param clz   Class类型
     * @param value bean
     */
    public void put(Class clz, Bean value) {
        repertory.put(clz, value);
    }

    /**
     * 从仓库中获取bean
     *
     * @param key key
     * @param <T> 类型
     * @return bean
     */
    @SuppressWarnings("all")
    public <T> T get(Class<T> key) {
        T t = (T) repertory.get(key);
        if (t instanceof Service) {
            return (T) Proxy.newProxyInstance(t.getClass().getClassLoader(),
                    t.getClass().getInterfaces(), (p, m, a) -> {
                        // 判断该方法是否需要事务控制
                        Transactional tran = m.getAnnotation(Transactional.class);
                        Object re = null;
                        try {
                            if (tran != null) {
                                // 开启事务
                                transactionManager.transaction();
                            }
                            re = m.invoke(t, a);
                        } catch (Throwable e) {
                            if (tran != null) {
                                // 事务回滚
                                transactionManager.rollback();
                            }
                            e.printStackTrace();
                            throw new Throwable(e);
                        } finally {
                            if (tran != null) {
                                // 事务提交
                                transactionManager.commit();
                            }
                            // 关闭连接
                            transactionManager.close();
                            PageHelper.remove();
                            return re;
                        }
                    });
        }
        return t;
    }
}
