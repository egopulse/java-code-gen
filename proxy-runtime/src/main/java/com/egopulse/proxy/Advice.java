package com.egopulse.proxy;

public interface Advice {
    Object execute(ProxyTarget target);
}
