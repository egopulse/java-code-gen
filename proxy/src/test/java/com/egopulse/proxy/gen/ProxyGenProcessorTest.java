package com.egopulse.proxy.gen;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import java.util.Arrays;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class ProxyGenProcessorTest {
    @Test
    public void testGen() {
        assert_().about(javaSources())
                .that(Arrays.asList(
                        JavaFileObjects.forResource("com/egopulse/proxy/gen/TestParent.java"),
                        JavaFileObjects.forResource("com/egopulse/proxy/gen/TestBean.java")))
                .processedWith(new ProxyGenProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(
                        JavaFileObjects.forResource("com/egopulse/proxy/gen/TestBeanProxy.java")
                );
    }
}
