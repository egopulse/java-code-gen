package com.egopulse.gen;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class TestModels {

    @Test
    public void testGenericList() throws Exception
    {
        assert_().about(javaSources())
                .that(Collections.singletonList(JavaFileObjects.forResource("com/egopulse/gen/TestGenericList.java")))
                .processedWith(new TestProcessor((elem, models) -> {
                    List<VariableElement> fields = models.getFields(elem);
                    Assert.assertEquals("Number of fields", 1, fields.size());
                    VariableElement listField = fields.get(0);
                    Assert.assertTrue("Is generic list ", models.isGenericList(listField.asType()));
                    List<ExecutableElement> methods = models.getMethods(elem);
                    Assert.assertEquals("Number of methods", 1, methods.size());
                    ExecutableElement listMethod = methods.get(0);
                    Assert.assertTrue("Is generic list ", models.isGenericList(listMethod.getReturnType()));
                }))
                .compilesWithoutError();
    }

    @FunctionalInterface
    public interface AnnotatedTypeElemHandle {
        void handle(TypeElement elem, Models models);
    }

    public static class TestProcessor extends AbstractProcessor {
        private final AnnotatedTypeElemHandle handle;
        public TestProcessor(AnnotatedTypeElemHandle handle) {
            this.handle = handle;
        }

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            Models models = new Models(processingEnv);
            for (TypeElement typeElement : roundEnv.getElementsAnnotatedWith(TestAnnotation.class).stream()
                    .filter(elem -> elem instanceof TypeElement)
                    .map(elem -> (TypeElement) elem)
                    .collect(Collectors.toList())) {
                try {
                    handle.handle(typeElement, models);
                } catch (Throwable t) {
                    models.error(typeElement, t);
                }
            }
            return false;
        }

        @Override
        public Set<String> getSupportedAnnotationTypes() {
            Set<String> ret = new HashSet<>();
            ret.add(TestAnnotation.class.getName());
            return ret;
        }

        @Override
        public SourceVersion getSupportedSourceVersion() {
            return SourceVersion.latestSupported();
        }
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface TestAnnotation {
    }
}
