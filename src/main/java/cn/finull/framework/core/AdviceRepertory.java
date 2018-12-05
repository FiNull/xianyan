package cn.finull.framework.core;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class AdviceRepertory {

    private AdviceRepertory() {}

    private static AdviceRepertory repertory;

    public static AdviceRepertory getInstance() {
        if (repertory == null) {
            repertory = new AdviceRepertory();
        }
        return repertory;
    }

    private Map<Class,BiFunction> exceptHandler = new HashMap<>();

    public <T,R> void add(Class<T> clz,BiFunction<T,HttpServletResponse,R> f) {
        exceptHandler.put(clz, f);
    }

    public <T,R> BiFunction<T,HttpServletResponse,R> handler(Class<T> clz) {
        return exceptHandler.get(clz);
    }

    public boolean contain(Class clz) {
        return exceptHandler.containsKey(clz);
    }
}
