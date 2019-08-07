package cn.finull.framework.core.bean;

import cn.finull.framework.core.BeanRepertory;

/**
 * 标识一个bean
 */
public interface Bean {

    Class getClassKey();

    // 获得仓库中的bean
    default <T> T get(Class<T> key) {
        return BeanRepertory.getInstance().get(key);
    }

    // 初始化所有依赖
    default void init() {
    }
}
