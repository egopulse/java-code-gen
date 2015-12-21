package com.egopulse.bson.gen;

import com.egopulse.bean.gen.BeanAnnoProcessor;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import java.util.Collections;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class TestBsonGen {

    @Test
    public void testBsonGen() {
        assert_().about(javaSources())
                .that(Collections.singletonList(JavaFileObjects.forResource("com/egopulse/bson/gen/TestPojo.java")))
                .processedWith(new BeanAnnoProcessor(), new BsonAnnoProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects.forResource("com/egopulse/bson/gen/TestPojoCodec.java"));
    }
}
