package com.egopulse.proxy;

public interface ProxyCreator<T extends K, K> {
    T create(K proxied, Advice advice);
}
