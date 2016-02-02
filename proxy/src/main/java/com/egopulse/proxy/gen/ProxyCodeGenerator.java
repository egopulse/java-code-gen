package com.egopulse.proxy.gen;

import com.egopulse.gen.Generator;
import com.egopulse.gen.GeneratorException;
import com.egopulse.gen.Models;
import com.egopulse.proxy.Advice;
import com.egopulse.proxy.DefaultProxyTarget;
import com.egopulse.proxy.Invoker;
import com.egopulse.proxy.ProxyCreatorRegistrar;
import com.egopulse.proxy.ProxyCreatorRegistry;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ProxyCodeGenerator implements Generator {
    private final Models models;
    private final List<String> generatedRegistrarClassNames = new ArrayList<>();

    public ProxyCodeGenerator(Models models) {
        this.models = models;
    }

    @Override
    public void generate(TypeElement typeElem, Filer filer) throws IOException {
        if (typeElem.getKind() != ElementKind.INTERFACE) {
            throw new GeneratorException(typeElem, "Should be an interface");
        }

        models.note("Generating Proxy class for %s", typeElem.getQualifiedName().toString());

        PackageElement packageElem = Models.getPackage(typeElem);
        String packageName = packageElem.getQualifiedName().toString();
        String proxiedClassFullName = typeElem.getQualifiedName().toString();
        String proxiedClassSimpleName = proxiedClassFullName.substring(packageName.length() + 1).replace(".", "_");
        String proxyClassSimpleName = proxiedClassSimpleName + "Proxy";
        TypeName proxiedClassTypeName = TypeName.get(typeElem.asType());
        ClassName adviceClassName = ClassName.get(Advice.class);
        ClassName proxyClassName = ClassName.get(packageName, proxyClassSimpleName);

        TypeSpec.Builder proxyClassBuilder = TypeSpec.classBuilder(proxyClassSimpleName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(proxiedClassTypeName);

        proxyClassBuilder.addField(
                FieldSpec.builder(proxiedClassTypeName, "delegate", Modifier.PRIVATE, Modifier.FINAL)
                        .build());
        proxyClassBuilder.addField(
                FieldSpec.builder(adviceClassName, "advice", Modifier.PRIVATE, Modifier.FINAL)
                        .build());

        proxyClassBuilder.addMethod(
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(proxiedClassTypeName, "delegate", Modifier.FINAL)
                        .addParameter(adviceClassName, "advice", Modifier.FINAL)
                        .addStatement("this.delegate = delegate")
                        .addStatement("this.advice = advice")
                        .build());

        StringBuilder paramListBuilder = new StringBuilder(100);
        ClassName defaultProxyTargetClassName = ClassName.get(DefaultProxyTarget.class);
        ClassName invokerClassName = ClassName.get(Invoker.class);
        int methodIdx = -1;
        for (ExecutableElement method : models.getPublicNonStaticMethods(typeElem)) {
            methodIdx++;
            String methodName = method.getSimpleName().toString();
            TypeMirror methodRetType = method.getReturnType();
            TypeName methodRetTypeName = TypeName.get(methodRetType);
            boolean isVoidType = methodRetType.getKind() == TypeKind.VOID;

            MethodSpec.Builder methodBuilder = MethodSpec
                    .methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "\"unchecked\"").build());
            paramListBuilder.setLength(0);


            if (!isVoidType) {
                methodBuilder.returns(TypeName.get(method.getReturnType()));
            }

            for (VariableElement paramElem : method.getParameters()) {
                String paramName = paramElem.getSimpleName().toString();
                TypeName paramTypeName = TypeName.get(paramElem.asType());
                methodBuilder.addParameter(paramTypeName, paramName, Modifier.FINAL);
                if (paramListBuilder.length() > 0) {
                    paramListBuilder.append(", ");
                }
                paramListBuilder.append(paramElem);
            }

            String paramList = paramListBuilder.toString();
            if (method.getAnnotation(ProxyGenIgnored.class) != null) {
                if (isVoidType) {
                    methodBuilder.addStatement("this.delegate.$L($L)", methodName, paramList);
                } else {
                    methodBuilder.addStatement("return ($T) this.delegate.$L($L)", methodRetTypeName, methodName, paramList);
                }
            } else {
                if (isVoidType) {
                    methodBuilder.addStatement("$T invoker = () -> {this.delegate.$L($L);return null;}", invokerClassName, methodName, paramList);
                } else {
                    methodBuilder.addStatement("$T invoker = () -> this.delegate.$L($L);", invokerClassName, methodName, paramList);
                }

                methodBuilder.addStatement("$T proxyTarget = new $T($T.class, this.delegate, $S, invoker$L)",
                        defaultProxyTargetClassName, defaultProxyTargetClassName,
                        proxiedClassTypeName, method.toString(), paramList.length() == 0 ? "" : ", " + paramList);

                if (isVoidType) {
                    methodBuilder.addStatement("this.advice.execute(proxyTarget)");
                } else {
                    methodBuilder.addStatement("return ($T) this.advice.execute(proxyTarget)", methodRetTypeName);
                }
            }
            proxyClassBuilder.addMethod(methodBuilder.build());
        }

        MethodSpec registerMethod = MethodSpec.methodBuilder("register")
                .addParameter(ProxyCreatorRegistry.class, "registry", Modifier.FINAL)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("registry.register($T.class, $T::new)", proxiedClassTypeName, proxyClassName)
                .build();
        proxyClassBuilder.addType(TypeSpec.classBuilder("Registrar")
                .addSuperinterface(ProxyCreatorRegistrar.class)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addMethod(registerMethod).build());
        generatedRegistrarClassNames.add(packageName + "." + proxyClassSimpleName + "$Registrar");
        JavaFile.builder(packageName, proxyClassBuilder.build()).build().writeTo(filer);
    }

    @Override
    public void generateLast(Filer filer) throws IOException {
        writeServiceNames(ProxyCreatorRegistrar.class, filer, generatedRegistrarClassNames);
    }

}