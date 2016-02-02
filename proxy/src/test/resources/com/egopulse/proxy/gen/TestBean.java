package com.egopulse.proxy.gen;

import java.util.List;

@ProxyGen
public interface TestBean extends TestParent {
    String method1(String param1, int param2, Object param3);
    void method2(boolean param1);
    boolean method3(List<String> param1);
}