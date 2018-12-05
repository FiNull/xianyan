package cn.finull.framework.core;

public abstract class AdviceInitialize {
    public AdviceInitialize() {
        addExceptionHandler(AdviceRepertory.getInstance());
    }

    public <T> T get(Class<T> clz) {
        return BeanRepertory.getInstance().get(clz);
    }

    public abstract void addExceptionHandler(AdviceRepertory repertory);
}
