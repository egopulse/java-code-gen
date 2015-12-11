package com.egopulse.gen.bson;

import com.egopulse.gen.bean.BeanAnnoProcessor;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import java.util.Collections;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class TestBsonGen {

    @Test
    public void testBsonGen() {
        assert_().about(javaSources())
                .that(Collections.singletonList(JavaFileObjects.forResource("com/egopulse/gen/bson/TestPojo.java")))
                .processedWith(new BeanAnnoProcessor(), new BsonAnnoProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects.forResource("com/egopulse/gen/bson/TestPojoCodec.java"));
    }
}
