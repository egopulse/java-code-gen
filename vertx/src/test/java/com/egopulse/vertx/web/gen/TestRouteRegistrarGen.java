package com.egopulse.vertx.web.gen;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import java.util.Collections;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class TestRouteRegistrarGen {

    @Test
    public void testGen() {
        assert_().about(javaSources())
                .that(Collections.singletonList(JavaFileObjects.forResource("com/egopulse/vertx/web/gen/TestResource.java")))
                .processedWith(new RouteRegistrarProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(
                        JavaFileObjects.forResource("com/egopulse/vertx/web/gen/TestResourceRegistrar.java")
                )
                .and()
                .generatesFiles(
                        JavaFileObjects.forResource("com/egopulse/vertx/web/gen/GenRouteRegistrar")
                );
    }
}
