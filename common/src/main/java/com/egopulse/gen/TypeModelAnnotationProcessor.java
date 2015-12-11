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
    private final Class<? extends Annotation> annotationType;
    private Models models;
    private Filer filer;

    protected TypeModelAnnotationProcessor(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        models = new Models(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> ret = new HashSet<>();
        ret.add(annotationType.getName());
        return ret;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<TypeElement> typeElems = roundEnv.getElementsAnnotatedWith(annotationType).stream()
                .filter(elem -> elem instanceof TypeElement)
                .map(elem -> (TypeElement) elem)
                .collect(Collectors.toList());
        Generator generator = createGenerator(models);
        for (TypeElement typeElem : typeElems) {
            try {
                generator.generate(typeElem, filer);
            } catch (GeneratorException e) {
                e.printError(models);
            } catch (Throwable t) {
                models.error(typeElem, t);
            }
        }
        try {
            generator.generateLast(filer);
        } catch (Throwable t) {
            models.error(null, t);
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