package com.egopulse.proxy;

public class DefaultProxyTarget implements ProxyTarget {
    private Class<?> type;
    private Object target;
    private final int methodIndex;
    private final Object[] paramValues;
    private final Invoker invoker;

    public DefaultProxyTarget(Class<?> type, Object target, int methodIndex, Invoker invoker, Object... paramValues) {
        this.type = type;
        this.target = target;
        this.methodIndex = methodIndex;
        this.invoker = invoker;
        this.paramValues = paramValues;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public int getMethodIndex() {
        return methodIndex;
    }

    @Override
    public Object[] getParamValues() {
        return paramValues;
    }

    @Override
    public Object invoke() {
        return invoker.invoke();
    }
}
