package cn.finull.framework.core;

/**
 * 初始化仓库
 */
public abstract class BeanRepertoryInitialize {

    public BeanRepertoryInitialize() {
        addBeans(BeanRepertory.getInstance());
    }

    public abstract void addBeans(BeanRepertory repertory);
}
