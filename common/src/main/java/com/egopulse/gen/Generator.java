package com.egopulse.gen;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.lang.model.element.TypeElement;
import java.io.IOException;

/**
 * Code/files generator for annotated types
 */
public interface Generator {
    /**
     * Generate files for an annotated type
     * @param typeElem The type element passed from {@link Processor}
     * @param filer The filer to write files content out
     * @throws IOException When failed to write to files because any reason
     */
    void generate(TypeElement typeElem, Filer filer) throws IOException;

    /**
     * Generate files those aggregated from calls to {@see generate}
     * @param filer The filer to write files content out
     * @throws IOException When failed to write to files because any reason
     */
    void generateLast(Filer filer) throws IOException;
}
