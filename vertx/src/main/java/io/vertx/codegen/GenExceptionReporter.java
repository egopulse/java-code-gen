package io.vertx.codegen;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class GenExceptionReporter {
    public static void report(GenException exception, Messager messager) {
        messager.printMessage(Diagnostic.Kind.ERROR, "Could not generate model. " + exception.msg, exception.element);
    }
}
