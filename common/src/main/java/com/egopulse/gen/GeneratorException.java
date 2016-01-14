package com.egopulse.gen;

import javax.lang.model.element.Element;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception when generating files during processing annotated types
 */
public class GeneratorException extends RuntimeException {
    private final Element element;

    public GeneratorException(Element element, String format, Object... args) {
        super(String.format(format, args));
        this.element = element;
    }

    public GeneratorException(Element element, Throwable t) {
        super("Exception: " + toMessage(t));
        this.element = element;
    }

    private static String toMessage(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        t.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    public void printError(Models models) {
        models.error(element, this.getMessage());
    }
}
