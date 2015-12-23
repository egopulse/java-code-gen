package com.egopulse.bson.gen;

import com.egopulse.bean.gen.BeanInfo;
import com.egopulse.bson.codecs.BeanCodec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import javax.annotation.processing.Filer;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BsonCodecGenerator implements Generator {
    private final List<String> generatedClassNames = new ArrayList<>();
    private final Models models;
    private final TypeMirror objectIdTypeMirror;
    private final TypeMirror bsonBinaryTypeMirror;
    private final TypeMirror dateTypeMirror;
    private final TypeMirror instantTypeMirror;
    private final TypeMirror stringTypeMirror;
    private final TypeMirror longTypeMirror;
    private final ClassName classClassName;
    private final ParameterSpec bsonWriterParamSpec;
    private final ParameterSpec bsonReaderParamSpec;
    private final ParameterSpec encoderCtxParamSpec;
    private final ParameterSpec decoderCtxParamSpec;

    public BsonCodecGenerator(Models models) {
        this.models = models;
        Elements elems = models.getElemsUtil();
        // Reusable things for multiple types
        objectIdTypeMirror = elems.getTypeElement(ObjectId.class.getCanonicalName()).asType();
        bsonBinaryTypeMirror = elems.getTypeElement(BsonBinary.class.getCanonicalName()).asType();
        dateTypeMirror = elems.getTypeElement(Date.class.getCanonicalName()).asType();
        instantTypeMirror = elems.getTypeElement(Instant.class.getCanonicalName()).asType();
        stringTypeMirror = elems.getTypeElement(String.class.getCanonicalName()).asType();
        longTypeMirror = elems.getTypeElement(Long.class.getCanonicalName()).asType();
        classClassName = ClassName.get(Class.class);
        bsonWriterParamSpec = ParameterSpec.builder(BsonWriter.class, "writer").addModifiers(Modifier.FINAL).build();
        bsonReaderParamSpec = ParameterSpec.builder(BsonReader.class, "reader").addModifiers(Modifier.FINAL).build();
        encoderCtxParamSpec = ParameterSpec.builder(EncoderContext.class, "ctx").addModifiers(Modifier.FINAL).build();
        decoderCtxParamSpec = ParameterSpec.builder(DecoderContext.class, "ctx").addModifiers(Modifier.FINAL).build();
    }

    private String readExp(TypeElement typeElem, BeanInfo.Property prop) {
        switch (prop.getType().getKind()) {
            case BOOLEAN:
                return "reader.readBoolean()";
            case BYTE:
                return "(byte) reader.readInt32()";
            case CHAR:
                return "reader.readString().charAt(0)";
            case SHORT:
                return "(short) reader.readInt32()";
            case INT:
                return "reader.readInt32()";
            case LONG:
                return "reader.readInt64()";
            case FLOAT:
                return "(float) reader.readDouble()";
            case DOUBLE:
                return "reader.readDouble()";
            case ARRAY:
            case DECLARED:
                return readObjTypeExp(prop);
            default:
                throw new GeneratorException(typeElem, "Not supported property type %s when decoding", typeElem.toString());
        }
    }

    private String readObjTypeExp(BeanInfo.Property prop) {
        TypeMirror type = prop.getType();
        Types types = models.getTypeUtils();
        if (types.isSameType(stringTypeMirror, type)) {
            JavaScript js = prop.getAnnotation(JavaScript.class);
            if (js != null) {
                return "reader.readJavaScript()";
            }
            return "reader.readString()";
        }
        if (types.isSameType(objectIdTypeMirror, type)) {
            return "reader.readObjectId()";
        }
        if (types.isSameType(bsonBinaryTypeMirror, type)) {
            return "reader.readBinaryData()";
        }
        if (types.isSameType(dateTypeMirror, type)) {
            return "new java.util.Date(reader.readDateTime())";
        }
        if (types.isSameType(instantTypeMirror, type)) {
            return "java.time.Instant.ofEpochMilli(reader.readDateTime())";
        }
        if (models.isEnum(type)) {
            return type.toString() + ".valueOf(reader.readString())";
        }
        if (models.isGenericList(type)) {
            TypeMirror itemType =  models.getParamType(type);
            return "this.decodeGenericList(reader, ctx, " + types.erasure(itemType).toString() + ".class)";
        }
        if (models.isGenericSet(type)) {
            TypeMirror itemType =  models.getParamType(type);
            return "this.decodeGenericSet(reader, ctx, " + types.erasure(itemType).toString() + ".class)";
        }
        if (models.isGenericMap(type)) {
            TypeMirror keyType =  models.getParamType(type, 0);
            TypeMirror valType =  models.getParamType(type, 1);
            if (models.getTypeUtils().isSameType(keyType, stringTypeMirror)) {
                return "this.decodeGenericStringMap(reader, ctx, " + types.erasure(valType).toString() + ".class)";
            }
            if (models.getTypeUtils().isSameType(keyType, longTypeMirror)) {
                return "this.decodeGenericLongMap(reader, ctx, " + types.erasure(valType).toString() + ".class)";
            }
        }
        return "this.decodeObject(reader, ctx, " + types.erasure(type).toString() + ".class)";
    }

    private String writeExp(TypeElement typeElem, BeanInfo.Property prop, String inner) {
        switch (prop.getType().getKind()) {
            case BOOLEAN:
                return "writer.writeBoolean(" + inner + ")";
            case BYTE:
                return "writer.writeInt32(" + inner + ")";
            case CHAR:
                return "writer.writeString(String.valueOf(" + inner + "))";
            case SHORT:
                return "writer.writeInt32(" + inner + ")";
            case INT:
                return "writer.writeInt32(" + inner + ")";
            case LONG:
                return "writer.writeInt64(" + inner + ")";
            case FLOAT:
                return "writer.writeDouble(" + inner + ")";
            case DOUBLE:
                return "writer.writeDouble(" + inner + ")";
            case ARRAY:
            case DECLARED:
                return writeObjTypeExp(prop, inner);
            default:
                throw new GeneratorException(typeElem, "Not supported property type %s when encoding", typeElem.toString());
        }
    }

    private String writeObjTypeExp(BeanInfo.Property prop, String inner) {
        TypeMirror type = prop.getType();
        Types types = models.getTypeUtils();
        if (types.isSameType(stringTypeMirror, type)) {
            JavaScript js = prop.getAnnotation(JavaScript.class);
            if (js != null) {
                return "writer.writeJavaScript(" + inner + ")";
            }
            return "writer.writeString(" + inner + ")";
        }
        if (types.isSameType(objectIdTypeMirror, type)) {
            return "writer.writeObjectId(" + inner + ")";
        }
        if (types.isSameType(bsonBinaryTypeMirror, type)) {
            return "writer.writeBinaryData(" + inner + ")";
        }
        if (types.isSameType(dateTypeMirror, type)) {
            return "writer.writeDateTime(" + inner + ".getTime())";
        }
        if (types.isSameType(instantTypeMirror, type)) {
            return "writer.writeDateTime(" + inner + ".toEpochMilli())";
        }
        if (models.isEnum(type)) {
            return "writer.writeString(" + inner + ".name())";
        }
        if (models.isGenericList(type)) {
            TypeMirror itemType =  models.getParamType(type);
            return "this.encodeGenericList(writer, "  + inner + ", ctx.getChildContext(), " + types.erasure(itemType).toString() + ".class)";
        }
        if (models.isGenericSet(type)) {
            TypeMirror itemType =  models.getParamType(type);
            return "this.encodeGenericSet(writer, " + inner + ", ctx.getChildContext(), " + types.erasure(itemType).toString() + ".class)";
        }
        if (models.isGenericMap(type)) {
            TypeMirror keyType =  models.getParamType(type, 0);
            TypeMirror valType =  models.getParamType(type, 1);
            if (models.getTypeUtils().isSameType(keyType, stringTypeMirror)) {
                return "this.encodeGenericStringMap(writer, " + inner + ", ctx.getChildContext(), " + types.erasure(valType).toString() + ".class)";
            }
            if (models.getTypeUtils().isSameType(keyType, longTypeMirror)) {
                return "this.encodeGenericLongMap(writer, " + inner + ", ctx.getChildContext(), " + types.erasure(valType).toString() + ".class)";
            }
        }
        return "this.encodeObject(writer, " + inner + ", ctx, " + types.erasure(type).toString() + ".class)";
    }

    @Override
    public void generate(final TypeElement typeElem, final Filer filer) throws IOException {
        PackageElement packageElem = Models.getPackage(typeElem);
        String packageName = packageElem.getQualifiedName().toString();
        String beanSimpleName = typeElem.getSimpleName().toString();
        String builderSimpleName = beanSimpleName + "Builder";
        ClassName beanClassName = ClassName.get(typeElem);
        TypeName beanClassTypeName = ParameterizedTypeName.get(classClassName, beanClassName);
        ParameterSpec pojoParamSpec = ParameterSpec.builder(beanClassName, "pojo").addModifiers(Modifier.FINAL).build();
        TypeSpec.Builder builder = initBuilder(beanSimpleName, beanClassName, beanClassTypeName);
        BeanInfo info = BeanInfo.fromType(typeElem, models);
        boolean hasId = false;
        boolean shouldHaveNonDefaultConstructor = info.shouldHaveNonDefaultConstructor();
        //public Bean decode(final BsonReader reader, DecoderContext ctx) {
        //}
        MethodSpec.Builder decodeMethodBuilder = MethodSpec.methodBuilder("decode")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(beanClassName)
                .addParameter(bsonReaderParamSpec)
                .addParameter(decoderCtxParamSpec);
        if (shouldHaveNonDefaultConstructor) {
            ClassName builderClassName = ClassName.get(packageName, builderSimpleName);
            //BeanBuilder builder = new BeanBuilder();
            decodeMethodBuilder.addStatement("$T builder = new $T()", builderClassName, builderClassName);
        } else {
            if (typeElem.getKind() == ElementKind.INTERFACE) {
                //BeanBean pojo = new BeanBean();
                ClassName implClassName = ClassName.get(packageName, beanSimpleName + "Bean");
                decodeMethodBuilder.addStatement("$T pojo = new $T()", implClassName, implClassName);
            } else {
                //Bean pojo = new Bean();
                decodeMethodBuilder.addStatement("$T pojo = new $T()", beanClassName, beanClassName);
            }
        }
        //while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
        //    switch (reader.readName()) {
        decodeMethodBuilder.addCode(
                "reader.readStartDocument();\n" +
                "while (reader.readBsonType() != org.bson.BsonType.END_OF_DOCUMENT) {\n" +
                "    switch (reader.readName()) {\n");
        //public void encode(final BsonWriter writer, Bean pojo, EncoderContext ctx) {
        //}
        MethodSpec.Builder encodeMethodBuilder = MethodSpec.methodBuilder("encode")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(bsonWriterParamSpec)
                .addParameter(pojoParamSpec)
                .addParameter(encoderCtxParamSpec)
                .addStatement("writer.writeStartDocument()");
        for (BeanInfo.Property prop: info.getProperties()) {
            final String propName = prop.getName();

            //Add encode method per property handling logic with support for @Id
            Id id = prop.getAnnotation(Id.class);
            String fieldName;
            if (id != null) {
                if (hasId) {
                    throw new GeneratorException(typeElem, "Duplicated Id annotations");
                }
                hasId = true;
                fieldName = "_id";
                if (!id.gen()) {
                    addPropWriteCode(encodeMethodBuilder, typeElem, fieldName, prop);
                }
            } else {
                fieldName = propName;
                addPropWriteCode(encodeMethodBuilder, typeElem, fieldName, prop);
            }

            //Add decode method per property handling logic
            String setTarget;
            String setMethodName;
            if (shouldHaveNonDefaultConstructor) {
                setTarget = "builder";
                setMethodName = Models.getBuilderMethodName(propName);
            } else {
                setTarget = "pojo";
                setMethodName = prop.getSetterName();
            }
            decodeMethodBuilder.addCode("        case $S:\n", fieldName);
            TypeKind propTypeKind = prop.getType().getKind();
            if (propTypeKind == TypeKind.ARRAY ||  propTypeKind == TypeKind.DECLARED) {
                decodeMethodBuilder.addCode("            if (reader.readBsonType() != org.bson.BsonType.NULL) {\n" +
                        "                $L.$L($L);\n" +
                        "            } else {\n" +
                        "                reader.readNull();\n" +
                        "                $L.$L(null);\n" +
                        "            }\n"
                        , setTarget, setMethodName, readExp(typeElem, prop), setTarget, setMethodName);
            } else {
                decodeMethodBuilder.addCode("            $L.$L($L);\n", setTarget, setMethodName, readExp(typeElem, prop));
            }
            decodeMethodBuilder.addCode("            break;\n");

        }

        decodeMethodBuilder.addCode("    }\n}\n");
        decodeMethodBuilder.addStatement("reader.readEndDocument()");

        if (shouldHaveNonDefaultConstructor) {
            decodeMethodBuilder.addStatement("return builder.build()");
        } else {
            decodeMethodBuilder.addStatement("return pojo");
        }
        encodeMethodBuilder.addStatement("writer.writeEndDocument()");

        builder.addMethod(encodeMethodBuilder.build());
        builder.addMethod(decodeMethodBuilder.build());
        JavaFile.builder(packageName, builder.build()).build().writeTo(filer);
        generatedClassNames.add(packageName + "." + beanSimpleName + "Codec");
    }

    @Override
    public void generateLast(Filer filer) throws IOException {
        writeServiceNames(BeanCodec.class, filer, generatedClassNames);
    }

    private void addPropWriteCode(MethodSpec.Builder builder, TypeElement typeElem, String fieldName, BeanInfo.Property prop) {
        TypeMirror propType = prop.getType();
        String propName = prop.getName();
        builder.addStatement("writer.writeName($S)", fieldName);
        if (propType.getKind() == TypeKind.ARRAY || propType.getKind() == TypeKind.DECLARED) {
            builder.addCode("$T _$L = pojo.$L();\n" +
                            "if (_$L == null) {\n" +
                            "    writer.writeNull();\n" +
                            "} else {\n" +
                            "    " + writeExp(typeElem, prop, "_$L") + ";\n" +
                            "}\n"
                    , propType, propName, prop.getGetterName(), propName, propName);
        } else {
            builder.addStatement(writeExp(typeElem, prop, "pojo.$L()"), prop.getGetterName());
        }
    }

    private TypeSpec.Builder initBuilder(final String beanName,
                                      final ClassName beanClassName,
                                      final TypeName beanClassTypeName) {
        ClassName pojoCodecClassName = ClassName.get(models.getElemsUtil().getTypeElement(BeanCodec.class.getCanonicalName()));
        //public BeanCodec extends BeanCodec<Bean> {
        TypeSpec.Builder builder = TypeSpec.classBuilder(beanName + "Codec")
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(pojoCodecClassName, beanClassName));
        //public Class<Bean> getEncoderClass() {
        //    return Bean.class;
        //}
        builder.addMethod(MethodSpec.methodBuilder("getEncoderClass")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(beanClassTypeName)
                .addStatement("return $T.class", beanClassName)
                .build()
        );
        return builder;
    }
}