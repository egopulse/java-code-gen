package com.egopulse.gen;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.lang.model.type.TypeKind.BOOLEAN;
import static javax.lang.model.type.TypeKind.VOID;

/**
 * Utility class to deal with Java Model
 */
public class Models {
    private final Elements elemsUtil;
    private final Types typeUtils;
    private final Messager messager;
    private final TypeMirror listType;
    private final TypeMirror setType;
    private final TypeMirror mapType;
    private final TypeMirror objectType;
    private final TypeMirror enumType;

    public Models(ProcessingEnvironment pe) {
        this.elemsUtil = pe.getElementUtils();
        this.typeUtils = pe.getTypeUtils();
        this.messager = pe.getMessager();
        this.listType =  typeUtils.erasure(elemsUtil.getTypeElement(List.class.getCanonicalName()).asType());
        this.setType =  typeUtils.erasure(elemsUtil.getTypeElement(Set.class.getCanonicalName()).asType());
        this.mapType =  typeUtils.erasure(elemsUtil.getTypeElement(Map.class.getCanonicalName()).asType());
        this.objectType = elemsUtil.getTypeElement(Object.class.getCanonicalName()).asType();
        this.enumType = elemsUtil.getTypeElement(Enum.class.getCanonicalName()).asType();
    }

    public Types getTypeUtils() {
        return typeUtils;
    }

    public Elements getElemsUtil() {
        return elemsUtil;
    }

    public static boolean isMethod(Element element) {
        return element.getKind() == ElementKind.METHOD;
    }

    public static boolean isPublicNonStatic(Element element) {
        Set<Modifier> modifiers = element.getModifiers();
        return modifiers.contains(Modifier.PUBLIC) && !modifiers.contains(Modifier.STATIC);
    }

    public static boolean isAnnotatedWithOneOf(Element element, Iterable<Class<? extends Annotation>> annotationClasses) {
        for (Class<? extends Annotation> clazz : annotationClasses) {
            if (element.getAnnotation(clazz) != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isField(Element element) {
        return element.getKind() == ElementKind.FIELD;
    }

    public boolean isNotBelongToObject(Element element) {
        Element enclosingElement = element.getEnclosingElement();
        return (enclosingElement.getKind() != ElementKind.CLASS) || !isObjectType((TypeElement) enclosingElement);
    }

    public static PackageElement getPackage(Element element) {
        Element result = element;
        while (result != null && result.getKind() != ElementKind.PACKAGE) {
            result = result.getEnclosingElement();
        }
        return (PackageElement) result;
    }

    public static String getPropNameFromGetter(ExecutableElement elem) {
        TypeMirror returnType = elem.getReturnType();
        if (returnType.getKind() == VOID) {
            return null;
        }
        if (elem.getParameters().size() != 0) {
            return null;
        }
        String simpleName = elem.getSimpleName().toString();
        if (returnType.getKind() == BOOLEAN) {
            if (simpleName.startsWith("is") && simpleName.length() > 2) {
                return Character.toLowerCase(simpleName.charAt(2)) + simpleName.substring(3);
            }
            return null;
        }
        if (simpleName.startsWith("get") && simpleName.length() > 3) {
            return Character.toLowerCase(simpleName.charAt(3)) + simpleName.substring(4);
        }
        return null;
    }

    public static String getPropNameFromSetter(ExecutableElement elem) {
        TypeMirror returnType = elem.getReturnType();
        if (returnType.getKind() != VOID) {
            return null;
        }
        if (elem.getParameters().size() != 1) {
            return null;
        }
        String simpleName = elem.getSimpleName().toString();
        if (simpleName.startsWith("set") && simpleName.length() > 3) {
            return Character.toLowerCase(simpleName.charAt(3)) + simpleName.substring(4);
        }
        return null;
    }

    public static boolean isGetter(ExecutableElement element) {
        TypeMirror returnType = element.getReturnType();
        if (returnType.getKind() == VOID) {
            return false;
        }
        if (element.getParameters().size() != 0) {
            return false;
        }
        String simpleName = element.getSimpleName().toString();
        if (returnType.getKind() == BOOLEAN) {
            return simpleName.startsWith("is") && simpleName.length() > 2 && Character.isUpperCase(simpleName.charAt(2));
        }
        return simpleName.startsWith("get") && simpleName.length() > 3 && Character.isUpperCase(simpleName.charAt(3));
    }

    public static boolean isSetter(ExecutableElement element) {
        TypeMirror returnType = element.getReturnType();
        if (returnType.getKind() != VOID) {
            return false;
        }
        if (element.getParameters().size() != 1) {
            return false;
        }
        if (element.getParameters().size() != 0) {
            return false;
        }
        String simpleName = element.getSimpleName().toString();
        return simpleName.startsWith("set") && simpleName.length() > 3 && Character.isUpperCase(simpleName.charAt(3));
    }

    public static String getBuilderMethodName(String propName) {
        return "with" + Character.toUpperCase(propName.charAt(0)) + (propName.length() > 1 ? propName.substring(1): "");
    }

    public boolean isObjectType(TypeElement element) {
        return typeUtils.isSameType(objectType, element.asType());
    }

    public static String getDefaultValueAsString(TypeKind kind) {
        switch (kind) {
            case BOOLEAN:
                return "false";
            case CHAR:
                return "'\\0'";
            case BYTE:
            case SHORT:
            case INT:
                return "0";
            case LONG:
                return "0L";
            case FLOAT:
                return "0F";
            case DOUBLE:
                return "0D";
        }
        return "null";
    }

    public boolean isGenericList(TypeMirror type) {
        return typeUtils.isSubtype(type, listType)
                && (type instanceof DeclaredType)
                && (((DeclaredType) type).getTypeArguments().size() == 1);
    }

    public boolean isGenericSet(TypeMirror type) {
        return typeUtils.isSubtype(type, setType)
                && (type instanceof DeclaredType)
                && (((DeclaredType) type).getTypeArguments().size() == 1);
    }

    public boolean isGenericMap(TypeMirror type) {
        return typeUtils.isSubtype(type, mapType)
                && (type instanceof DeclaredType)
                && (((DeclaredType) type).getTypeArguments().size() == 2);
    }

    public TypeMirror getParamType(TypeMirror type) {
        return ((DeclaredType) type).getTypeArguments().get(0);
    }

    public TypeMirror getParamType(TypeMirror type, int idx) {
        return ((DeclaredType) type).getTypeArguments().get(idx);
    }

    public boolean isEnum(TypeMirror type) {
        return typeUtils.isSubtype(type, enumType);
    }

    public List<ExecutableElement> getMethods(TypeElement typeElement) {
        return elemsUtil.getAllMembers(typeElement).stream()
                .filter(Models::isMethod).filter(this::isNotBelongToObject)
                .map(elem -> (ExecutableElement) elem)
                .collect(Collectors.toList());
    }

    public <T extends Annotation> List<ExecutableElement> getPublicNonStaticMethods(TypeElement typeElement) {
        return elemsUtil.getAllMembers(typeElement).stream()
                .filter(Models::isMethod).filter(m -> isNotBelongToObject(m) && isPublicNonStatic(m))
                .map(elem -> (ExecutableElement) elem)
                .collect(Collectors.toList());
    }

    public List<VariableElement> getFields(TypeElement typeElement) {
        return elemsUtil.getAllMembers(typeElement).stream()
                .filter(Models::isField).filter(this::isNotBelongToObject)
                .map(elem -> (VariableElement) elem)
                .collect(Collectors.toList());
    }

    public void error(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }

    public void error(Element e, Throwable t) {
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        error(e, "Exception: %s", writer.toString());
    }
}
