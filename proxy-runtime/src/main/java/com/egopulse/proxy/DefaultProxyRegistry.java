package com.egopulse.proxy;

import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultProxyRegistry implements ProxyCreatorRegistry {
    private final ConcurrentMap<Class, ProxyCreator> store = new ConcurrentHashMap<>();

    public void load() {
        for (ProxyCreatorRegistrar registrar : ServiceLoader.load(ProxyCreatorRegistrar.class)) {
            registrar.register(this);
        }
    }

    @Override
    public <T extends K, K> void register(Class<K> proxiedClass, ProxyCreator<T, K> creator) {
        store.put(proxiedClass, creator);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> proxiedClass, T proxied, Advice advice) {
        return (T) store.get(proxiedClass).create(proxied, advice);
    }
}
