package cn.finull.framework.core.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 用于映射请求uri和处理方法的路由器
 */
public class Router {

    private static final Logger LOG = LoggerFactory.getLogger(Router.class);

    // 路由映射
    private Map<URI,Function> mapping = new HashMap<>();

    private Router() {}

    private static Router ROUTER;

    // 获得一个实例
    public static Router getInstance() {
        if (ROUTER == null) {
            ROUTER = new Router();
        }
        return ROUTER;
    }

    // 根据对应的地址获得一个处理器
    public Function handler(URI uri) {
        Function h = mapping.get(uri);
        if (h == null) {
            for (Map.Entry<URI,Function> entry : mapping.entrySet()) {
                if (entry.getKey().equals(uri)) {
                    return entry.getValue();
                }
            }
        }
        return h;
    }

    // 匹配路径不匹配方法
    public boolean matchPath(String path) {
        for (URI uri : mapping.keySet()) {
            if (uri.contains(path)) {
                return true;
            }
        }
        return false;
    }

    private String moduleUri = "";

    // 模块路径
    public Router module(String uri) {
        moduleUri = uri;
        return this;
    }

    private Router set(String method,String uri,Function function) {
        mapping.put(new URI(method,moduleUri + uri),function);
        LOG.info("load uri ==> [method: {}][uri: {}]",method,moduleUri + uri);
        return this;
    }

    // get方法
    public <R> Router get(String uri,Function<Parameter,R> function) {
        return set("GET",uri,function);
    }

    // post方法
    public <R> Router post(String uri,Function<Parameter,R> function) {
        return set("POST",uri,function);
    }

    // put方法
    public <R> Router put(String uri,Function<Parameter,R> function) {
        return set("PUT",uri,function);
    }

    // delete方法
    public <R> Router delete(String uri,Function<Parameter,R> function) {
        return set("DELETE",uri,function);
    }

    // head方法
    public <R> Router head(String uri,Function<Parameter,R> function) {
        return set("HEAD",uri,function);
    }

    // trace方法
    public <R> Router trace(String uri,Function<Parameter,R> function) {
        return set("TRACE",uri,function);
    }

    public void end() {
        moduleUri = "";
    }
}
