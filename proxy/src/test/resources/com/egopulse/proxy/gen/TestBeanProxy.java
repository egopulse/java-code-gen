package com.egopulse.proxy.gen;

import com.egopulse.proxy.Advice;
import com.egopulse.proxy.DefaultProxyTarget;
import com.egopulse.proxy.Invoker;
import com.egopulse.proxy.ProxyCreatorRegistrar;
import com.egopulse.proxy.ProxyCreatorRegistry;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.List;

public class TestBeanProxy implements TestBean {
    private final TestBean delegate;

    private final Advice advice;

    public TestBeanProxy(final TestBean delegate, final Advice advice) {
        this.delegate = delegate;
        this.advice = advice;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void parentMethod(final boolean param1) {
        Invoker invoker = () -> {this.delegate.parentMethod(param1);return null;};
        DefaultProxyTarget proxyTarget = new DefaultProxyTarget(TestBean.class, this.delegate, "parentMethod(boolean)", invoker, param1);
        this.advice.execute(proxyTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String method1(final String param1, final int param2, final Object param3) {
        Invoker invoker = () -> this.delegate.method1(param1, param2, param3);;
        DefaultProxyTarget proxyTarget = new DefaultProxyTarget(TestBean.class, this.delegate, "method1(java.lang.String,int,java.lang.Object)", invoker, param1, param2, param3);
        return (String) this.advice.execute(proxyTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void method2(final boolean param1) {
        Invoker invoker = () -> {this.delegate.method2(param1);return null;};
        DefaultProxyTarget proxyTarget = new DefaultProxyTarget(TestBean.class, this.delegate, "method2(boolean)", invoker, param1);
        this.advice.execute(proxyTarget);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean method3(final List<String> param1) {
        Invoker invoker = () -> this.delegate.method3(param1);;
        DefaultProxyTarget proxyTarget = new DefaultProxyTarget(TestBean.class, this.delegate, "method3(java.util.List)", invoker, param1);
        return (boolean) this.advice.execute(proxyTarget);
    }

    public static class Registrar implements ProxyCreatorRegistrar {
        @Override
        public void register(final ProxyCreatorRegistry registry) {
            registry.register(TestBean.class, TestBeanProxy::new);
        }
    }
}