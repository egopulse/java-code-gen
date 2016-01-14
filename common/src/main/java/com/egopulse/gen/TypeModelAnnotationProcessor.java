package com.egopulse.gen;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Convenient base class taking care all initialization and wiring needs for process
 * annotated {@link TypeElement}
 */
public abstract class TypeModelAnnotationProcessor extends AbstractProcessor {
    private final Class<? extends  Annotation>[] annotationTypes;
    private Models models;
    private Filer filer;
    private Generator generator;

    @SafeVarargs
    protected TypeModelAnnotationProcessor(Class<? extends Annotation>... annotationTypes) {
        this.annotationTypes = annotationTypes;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        models = new Models(processingEnv);
        filer = processingEnv.getFiler();
        generator = createGenerator(models);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> ret = new HashSet<>();
        for (Class<? extends Annotation> type : annotationTypes) {
            ret.add(type.getName());
        }
        return ret;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            try {
                generator.generateLast(filer);
            } catch (Throwable t) {
                models.error(null, t);
            }
        } else {
            for (Class<? extends Annotation> annotationType : annotationTypes) {
                List<TypeElement> typeElems = roundEnv.getElementsAnnotatedWith(annotationType).stream()
                        .filter(elem -> elem instanceof TypeElement)
                        .map(elem -> (TypeElement) elem)
                        .collect(Collectors.toList());

                for (TypeElement typeElem : typeElems) {
                    try {
                        generator.generate(typeElem, filer);
                    } catch (GeneratorException e) {
                        e.printError(models);
                    } catch (Throwable t) {
                        models.error(typeElem, t);
                    }
                }
            }
        }

        return true;
    }

    /**
     * Construct the main generator
     * @param models The Java Model utilities
     * @return The constructed {@link Generator}
     */
    protected abstract Generator createGenerator(Models models);
}