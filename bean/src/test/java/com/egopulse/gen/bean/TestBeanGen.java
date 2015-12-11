package com.egopulse.gen.bean;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import java.util.Collections;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class TestBeanGen {

    @Test
    public void testGen() {
        assert_().about(javaSources())
                .that(Collections.singletonList(JavaFileObjects.forResource("com/egopulse/gen/bean/TestPojo.java")))
                .processedWith(new BeanAnnoProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects.forResource("com/egopulse/gen/bean/TestPojoBean.java"),
                        JavaFileObjects.forResource("com/egopulse/gen/bean/TestPojoBuilder.java"),
                        JavaFileObjects.forResource("com/egopulse/gen/bean/TestPojoPropNameExtractor.java"));
    }
}
