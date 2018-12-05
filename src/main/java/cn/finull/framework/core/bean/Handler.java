package cn.finull.framework.core.bean;

/**
 * 修饰一个controller类
 */
public interface Handler extends Bean {

    // 请求重定向
    default String redirect(String path) {
        return "redirect:" + path;
    }

    // 请求转发
    default String forward(String path) {
        return "forward:" + path;
    }
}
