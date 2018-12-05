package cn.finull.framework.core.request;

import cn.finull.framework.core.BeanRepertory;

/**
 * uri -> method
 * 初始化路由器
 */
public abstract class RequestHandlerInitialize {

    public RequestHandlerInitialize() {
        addHandlers(Router.getInstance());
    }

    public <T> T get(Class<T> clz) {
        return BeanRepertory.getInstance().get(clz);
    }

    /**
     * 添加uri处理器
     * @param router 路由器
     */
    public abstract void addHandlers(Router router);
}
