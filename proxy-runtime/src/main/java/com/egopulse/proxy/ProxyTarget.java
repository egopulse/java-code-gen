package com.egopulse.proxy;

public interface ProxyTarget {
    Object getTarget();
    String getMethodName();
    Object[] getParamValues();
    Object invoke();
}
