package com.egopulse.proxy;

public interface ProxyTarget {
    Object getTarget();
    int getMethodIndex();
    Object[] getParamValues();
    Object invoke();
}
