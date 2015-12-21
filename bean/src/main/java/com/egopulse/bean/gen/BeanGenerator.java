package com.egopulse.bean.gen;

import com.egopulse.bson.gen.Generator;
import com.egopulse.bson.gen.GeneratorException;
import com.egopulse.bson.gen.Models;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Generator to generate implementation and builder for annotated interface with {@link Bean}.
 * The name of the implementation will be the name of the bean + "Bean" and the builder class name will be
 * bean + "Builder"
 */
public class BeanGenerator implements Generator {
    private final Models models;

    public BeanGenerator(Models models) {
        this.models = models;
    }

    @Override
    public void generate(TypeElement clazz, Filer filer) throws IOException {
        if (clazz.getKind() != ElementKind.INTERFACE) {
            throw new GeneratorException(clazz, "Should be an interface");
        }
        Bean annotation = clazz.getAnnotation(Bean.class);
        PackageElement packageElem = Models.getPackage(clazz);
        String packageName = packageElem.getQualifiedName().toString();
        String simpleName = clazz.getSimpleName().toString();
        String beanName = simpleName + "Bean";
        String beanFullName = packageName + "." + beanName;
        TypeName beanTypeName = ClassName.get(packageName, beanName);
        String builderName = simpleName + "Builder";
        ClassName builderClassName = ClassName.get(packageName, builderName);
        String extractorName = simpleName + "PropNameExtractor";
        BeanInfo info = BeanInfo.fromType(clazz, models);
        boolean shouldHaveNonDefaultConstructor = info.shouldHaveNonDefaultConstructor();

        JavaFile.builder(packageName, genBean(info, beanName, shouldHaveNonDefaultConstructor, builderClassName)).build().writeTo(filer);
        JavaFile.builder(packageName, genBuilder(info, ClassName.get(packageName, builderName), builderName, beanTypeName, beanFullName)).build().writeTo(filer);
        if (annotation.propNameExtractor()) {
            JavaFile.builder(packageName, getPropNameExtractor(info, extractorName)).build().writeTo(filer);
        }
    }

    @Override
    public void generateLast(Filer filer) throws IOException {
        // Do nothing
    }

    private TypeSpec genBean(BeanInfo info, String beanName, boolean nonDefaultConstructor, ClassName builderClassName) {
        List<BeanInfo.Property> properties = info.getProperties();
        int propCount = properties.size();
        List<FieldSpec> fieldSpecs = new ArrayList<>(propCount);
        List<MethodSpec> methodSpecs = new ArrayList<>(propCount);

        for (BeanInfo.Property p: properties) {
            //Field
            FieldSpec fieldSpec = FieldSpec.builder(TypeName.get(p.getType()), "_" + p.getName())
                    .addModifiers(Modifier.PRIVATE)
                    .build();
            fieldSpecs.add(fieldSpec);

            //Getter
            methodSpecs.add(MethodSpec.methodBuilder(p.getGetterName())
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(TypeName.get(p.getType()))
                    .addStatement("return this.$N", fieldSpec)
                    .build());

            //Setter if possible
            if (p.hasSetter()) {
                ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.get(p.getType()), p.getName()).build();
                methodSpecs.add(MethodSpec.methodBuilder(p.getSetterName())
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .addParameter(parameterSpec)
                        .addStatement("this.$N = $N", fieldSpec, parameterSpec)
                        .build());
            }
        }

        //Non default constructor
        MethodSpec.Builder beanConstructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        for (FieldSpec fieldSpec : fieldSpecs) {
            ParameterSpec parameterSpec = ParameterSpec.builder(fieldSpec.type, fieldSpec.name).build();
            beanConstructorBuilder.addParameter(parameterSpec);
            beanConstructorBuilder.addStatement("this.$N = $N", fieldSpec, parameterSpec);
        }
        methodSpecs.add(beanConstructorBuilder.build());

        //Default constructor if possible
        if (!nonDefaultConstructor) {
            methodSpecs.add(MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .build());
        }

        //Static method for builder
        methodSpecs.add(MethodSpec.methodBuilder("builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(builderClassName)
                .addStatement("return new $T()", builderClassName)
                .build());

        return TypeSpec.classBuilder(beanName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(TypeName.get(info.getType().asType()))
                .addFields(fieldSpecs).addMethods(methodSpecs)
                .build();
    }

    private TypeSpec genBuilder(BeanInfo info, ClassName builderClassName, String builderName, TypeName beanTypeName, String beanFullName) {
        List<BeanInfo.Property> properties = info.getProperties();
        int propCount = properties.size();
        List<FieldSpec> fieldSpecs = new ArrayList<>(propCount);
        List<MethodSpec> methodSpecs = new ArrayList<>(propCount);
        StringBuilder paramListBuilder  = new StringBuilder();
        Object[] newStatementParams = new Object[propCount + 1];
        newStatementParams[0] = beanFullName;
        int propIdx = 0;

        for (BeanInfo.Property p: properties) {
            //Field
            FieldSpec fieldSpec = FieldSpec.builder(TypeName.get(p.getType()), "_" + p.getName())
                    .addModifiers(Modifier.PRIVATE)
                    .build();
            fieldSpecs.add(fieldSpec);

            //Builder method (withPropName)
            ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.get(p.getType()), "_" + p.getName()).build();
            methodSpecs.add(MethodSpec.methodBuilder(Models.getBuilderMethodName(p.getName()))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(builderClassName)
                    .addParameter(parameterSpec)
                    .addStatement("this.$N = $N", fieldSpec, parameterSpec)
                    .addStatement("return this")
                    .build());
            if (propIdx > 0) {
                paramListBuilder.append(", ");
            }
            propIdx ++;
            newStatementParams[propIdx] = fieldSpec;
            paramListBuilder.append("this.$N");
        }

        methodSpecs.add(MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC)
                .returns(beanTypeName)
                .addStatement("return new $N(" + paramListBuilder.toString() + ")", newStatementParams)
                .build());

        methodSpecs.add(MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(beanTypeName)
                .addStatement("return new $N(" + paramListBuilder.toString() + ")", newStatementParams)
                .build());

        return TypeSpec.classBuilder(builderName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Supplier.class), TypeName.get(info.getType().asType())))
                .addFields(fieldSpecs)
                .addMethods(methodSpecs)
                .build();
    }

    private TypeSpec getPropNameExtractor(BeanInfo info, String extractorName) {
        List<BeanInfo.Property> properties = info.getProperties();
        int propCount = properties.size();
        List<MethodSpec> methodSpecs = new ArrayList<>(propCount);
        FieldSpec lastNameFieldSpec = FieldSpec.builder(String.class, "lastName", Modifier.PRIVATE).build();

        methodSpecs.add(MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return this.$N", lastNameFieldSpec)
                .build());

        for (BeanInfo.Property p: properties) {
            methodSpecs.add(MethodSpec.methodBuilder(p.getGetterName())
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(TypeName.get(p.getType()))
                    .addStatement("this.$N=$S", lastNameFieldSpec, p.getName())
                    .addStatement("return $L", Models.getDefaultValueAsString(p.getType().getKind()))
                    .build());
            if (p.hasSetter()) {
                ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.get(p.getType()), p.getName()).build();
                methodSpecs.add(MethodSpec.methodBuilder(p.getSetterName())
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .addParameter(parameterSpec)
                        .build());
            }
        }

        return TypeSpec.classBuilder(extractorName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(lastNameFieldSpec)
                .addSuperinterface(TypeName.get(info.getType().asType()))
                .addSuperinterface(ParameterizedTypeName.get(Supplier.class, String.class))
                .addMethods(methodSpecs)
                .build();
    }
}