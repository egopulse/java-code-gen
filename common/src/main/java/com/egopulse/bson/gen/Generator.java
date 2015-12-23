package com.egopulse.bson.gen;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    default void writeServiceNames(Class<?> serviceClass, Filer filer, List<String> generatedClassNames) throws IOException {
        String resourceFile = "META-INF/services/" + serviceClass.getName();
        FileObject existingFile = filer.getResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
        List<String> lines = new ArrayList<>();
        try (Reader reader = existingFile.openReader(false)) {
            try (BufferedReader bufferedReader = new BufferedReader(reader)) {
                lines.addAll(bufferedReader.lines().collect(Collectors.toList()));
            }
        } catch (IOException e) {
            // Just ignore because reading from a non-existing file and there is no way to detect its existence
            // than open the input stream/reader.
        }

        lines.addAll(generatedClassNames);

        FileObject newFile = filer.createResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
        try (Writer writer = newFile.openWriter()) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                for (String line : lines) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
            }
        }
    }
}
