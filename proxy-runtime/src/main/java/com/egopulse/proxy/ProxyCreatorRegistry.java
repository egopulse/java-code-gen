package com.egopulse.proxy;

public interface ProxyCreatorRegistry {
    <T extends K, K> void register(Class<K> proxiedClass, ProxyCreator<T, K> creator);
    boolean isRegistered(Class<?> proxiedClass);
    <T> T create(Class<T> proxiedClass, T proxied, Advice advice);
}
