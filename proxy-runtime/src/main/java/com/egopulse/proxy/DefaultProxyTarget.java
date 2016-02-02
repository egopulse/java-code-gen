package com.egopulse.proxy;

public class DefaultProxyTarget implements ProxyTarget {
    private final Class<?> type;
    private final Object target;
    private final String methodName;
    private final Object[] paramValues;
    private final Invoker invoker;

    public DefaultProxyTarget(Class<?> type, Object target, String methodName, Invoker invoker, Object... paramValues) {
        this.type = type;
        this.target = target;
        this.methodName = methodName;
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
    public String getMethodName() {
        return methodName;
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
