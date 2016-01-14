package com.egopulse.vertx.web.gen;

import com.egopulse.gen.Generator;
import com.egopulse.gen.Models;
import com.egopulse.vertx.web.RouteRegistrarHelper;
import com.egopulse.vertx.web.RouteRegistrar;
import com.egopulse.web.annotation.CONNECT;
import com.egopulse.web.annotation.Consume;
import com.egopulse.web.annotation.CookieObject;
import com.egopulse.web.annotation.CookieValue;
import com.egopulse.web.annotation.DELETE;
import com.egopulse.web.annotation.GET;
import com.egopulse.web.annotation.HEAD;
import com.egopulse.web.annotation.HttpMethod;
import com.egopulse.web.annotation.Method;
import com.egopulse.web.annotation.PATCH;
import com.egopulse.web.annotation.POST;
import com.egopulse.web.annotation.PUT;
import com.egopulse.web.annotation.Path;
import com.egopulse.web.annotation.PathParam;
import com.egopulse.web.annotation.Produce;
import com.egopulse.web.annotation.RequestBody;
import com.egopulse.web.annotation.RequestHeader;
import com.egopulse.web.annotation.RequestParam;
import com.egopulse.web.annotation.RouteMapping;
import com.egopulse.web.annotation.SessionValue;
import com.egopulse.web.annotation.TRACE;
import com.egopulse.web.annotation.ValueConstants;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.vertx.core.Handler;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.Route;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;


import javax.annotation.processing.Filer;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class RouteRegistrarCodeGenerator implements Generator {
    private static final List<Class<? extends Annotation>> ALL_MAPPING_TYPES;

    static  {
        ALL_MAPPING_TYPES = new ArrayList<>();
        ALL_MAPPING_TYPES.add(RouteMapping.class);
        ALL_MAPPING_TYPES.add(Path.class);
        ALL_MAPPING_TYPES.add(Consume.class);
        ALL_MAPPING_TYPES.add(Produce.class);
        ALL_MAPPING_TYPES.add(Method.class);
        ALL_MAPPING_TYPES.add(CONNECT.class);
        ALL_MAPPING_TYPES.add(DELETE.class);
        ALL_MAPPING_TYPES.add(GET.class);
        ALL_MAPPING_TYPES.add(HEAD.class);
        ALL_MAPPING_TYPES.add(PATCH.class);
        ALL_MAPPING_TYPES.add(POST.class);
        ALL_MAPPING_TYPES.add(PUT.class);
        ALL_MAPPING_TYPES.add(TRACE.class);
    }


    private final List<String> generatedClassNames = new ArrayList<>();


    private final Models models;
    private final TypeMirror stringType;
    private final TypeMirror routingCtxType;
    private final TypeMirror routingCtxTypeCore;
    private final TypeMirror reqType;
    private final TypeMirror reqTypeCore;
    private final TypeMirror respType;
    private final TypeMirror respTypeCore;
    private final TypeMirror routeType;
    private final TypeMirror routeTypeCore;
    private final TypeMirror routerType;
    private final TypeMirror routerTypeCore;
    private final TypeMirror sessionType;
    private final TypeMirror sessionTypeCore;

    public RouteRegistrarCodeGenerator(Models models) {
        this.models = models;
        Elements elementsUtil = models.getElemsUtil();
        stringType = elementsUtil.getTypeElement(String.class.getCanonicalName()).asType();
        routingCtxType = elementsUtil.getTypeElement(RoutingContext.class.getCanonicalName()).asType();
        routingCtxTypeCore = elementsUtil.getTypeElement(io.vertx.ext.web.RoutingContext.class.getCanonicalName()).asType();
        reqType = elementsUtil.getTypeElement(HttpServerRequest.class.getCanonicalName()).asType();
        reqTypeCore = elementsUtil.getTypeElement(io.vertx.core.http.HttpServerRequest.class.getCanonicalName()).asType();
        respType = elementsUtil.getTypeElement(HttpServerResponse.class.getCanonicalName()).asType();
        respTypeCore = elementsUtil.getTypeElement(io.vertx.core.http.HttpServerResponse.class.getCanonicalName()).asType();
        routeType = elementsUtil.getTypeElement(Route.class.getCanonicalName()).asType();
        routeTypeCore = elementsUtil.getTypeElement(io.vertx.ext.web.Route.class.getCanonicalName()).asType();
        routerType = elementsUtil.getTypeElement(Router.class.getCanonicalName()).asType();
        routerTypeCore = elementsUtil.getTypeElement(io.vertx.ext.web.Router.class.getCanonicalName()).asType();
        sessionType = elementsUtil.getTypeElement(Session.class.getCanonicalName()).asType();
        sessionTypeCore = elementsUtil.getTypeElement(io.vertx.ext.web.Session.class.getCanonicalName()).asType();
    }

    @Override
    public void generate(TypeElement typeElem, Filer filer) throws IOException {
        String handlersClassSimpleName = typeElem.getSimpleName().toString();
        String registrarClassSimpleName = handlersClassSimpleName + "Registrar";
        ClassName routeHandlersRegistrarClassName = ClassName.get(models.getElemsUtil().getTypeElement(RouteRegistrar.class.getCanonicalName()));
        TypeName targetTypeName = TypeName.get(typeElem.asType());


        TypeSpec.Builder registrarClassBuilder = TypeSpec.classBuilder(registrarClassSimpleName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(routeHandlersRegistrarClassName, targetTypeName));

        MethodSpec.Builder registerMethodBuilder = MethodSpec.methodBuilder("register")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Router.class, "router", Modifier.FINAL)
                .addParameter(RouteRegistrarHelper.class, "helper", Modifier.FINAL)
                .addParameter(targetTypeName, "target", Modifier.FINAL);

        RouteMappingInfo defaultInfo = new RouteMappingInfo(typeElem);

        for (ExecutableElement method : models.getPublicNonStaticMethods(typeElem)) {
            if (!Models.isAnnotatedWithOneOf(method, ALL_MAPPING_TYPES)) {
                continue;
            }

            TypeMirror methodType = method.getReturnType();
            TypeMirror erasureMethodType = models.getTypeUtils().erasure(methodType);
            TypeName methodTypeName = TypeName.get(methodType);
            RouteMappingInfo methodRouteMappingInfo =  new RouteMappingInfo(method);

            // New scope to avoid local variable names clashing
            registerMethodBuilder.addCode("{\n");

            registerMethodBuilder.addStatement("  $T route = router.route()", Route.class);
            configMapping(registerMethodBuilder, defaultInfo, methodRouteMappingInfo);

            // Handler method
            registerMethodBuilder.addCode("  $T handler = ctx -> {\n", ParameterizedTypeName.get(Handler.class, RoutingContext.class));
            registerMethodBuilder.addCode("    try {\n");

            // Target method call
            if (methodType.getKind() != TypeKind.VOID) {
                registerMethodBuilder.addCode("      $T ret = target.$L(", methodTypeName, method.getSimpleName().toString());
            } else {
                registerMethodBuilder.addCode("      target.$L(", method.getSimpleName().toString());
            }
            boolean firstParam = true;
            for (VariableElement param : method.getParameters()) {
                if (firstParam) {
                    firstParam = false;
                } else {
                    registerMethodBuilder.addCode(", ");
                }
                addParamValue(registerMethodBuilder, param);
            }
            // End target method call
            registerMethodBuilder.addCode(");\n");

            // Process response
            if (methodType.getKind() != TypeKind.VOID) {
                boolean responseBody = defaultInfo.isResponseBody() || methodRouteMappingInfo.isResponseBody();
                if (responseBody) {
                    registerMethodBuilder.addStatement("      helper.handleResponseBody(ctx, $T.class, ret)", erasureMethodType);
                } else {
                    registerMethodBuilder.addStatement("      helper.handleResponse(ctx, $T.class, ret)", erasureMethodType);
                }
            } else {
                registerMethodBuilder.addStatement("      ctx.next()");
            }
            //End the handler method
            registerMethodBuilder.addCode(
                    "    } catch (Throwable t) {;\n" +
                    "      helper.handleError(ctx, t);\n" +
                    "    };\n" +
                    "  };\n");

            boolean blocking = defaultInfo.isBlocking() || methodRouteMappingInfo.isBlocking();
            if (blocking) {
                registerMethodBuilder.addStatement("  route.blockingHandler(handler)");
            } else {
                registerMethodBuilder.addStatement("  route.handler(handler)");
            }

            //End the scope
            registerMethodBuilder.addCode("}\n");
        }

        registrarClassBuilder.addMethod(registerMethodBuilder.build());


        registrarClassBuilder.addMethod(MethodSpec.methodBuilder("getTargetType")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(Class.class), targetTypeName))
                .addStatement("return $T.class", typeElem.asType())
                .build());

        PackageElement packageElem = Models.getPackage(typeElem);
        String packageName = packageElem.getQualifiedName().toString();
        JavaFile.builder(packageName, registrarClassBuilder.build()).build().writeTo(filer);
        generatedClassNames.add(packageName + "." + registrarClassSimpleName);
    }

    @Override
    public void generateLast(Filer filer) throws IOException {
        writeServiceNames(RouteRegistrar.class, filer, generatedClassNames);
    }

    private static void configMapping(MethodSpec.Builder builder, RouteMappingInfo defaultInfo, RouteMappingInfo methodInfo) {
        String path = defaultInfo.getPath().isEmpty() ? methodInfo.getPath() : defaultInfo.getPath() + methodInfo.getPath();
        String pathRegEx = defaultInfo.getPathRegex().isEmpty() ? methodInfo.getPathRegex() : defaultInfo.getPathRegex() + methodInfo.getPathRegex();
        if (!path.isEmpty()) {
            builder.addStatement("  route.path($S)", path);
        } else if (!pathRegEx.isEmpty()) {
            builder.addStatement("  route.pathRegex($S)", pathRegEx);
        }

        Set<HttpMethod> methods = methodInfo.getMethods().size() > 0 ? methodInfo.getMethods() : defaultInfo.getMethods();
        for (HttpMethod httpMethod : methods) {
            builder.addStatement("  route.method($T.$L)", io.vertx.core.http.HttpMethod.class, httpMethod);
        }

        Set<String> produces = methodInfo.getProduces().size() > 0 ? methodInfo.getProduces() : defaultInfo.getProduces();
        for (String produce : produces) {
            builder.addStatement("  route.produces($S)", produce);
        }

        Set<String> consumes = methodInfo.getConsumes().size() > 0 ? methodInfo.getConsumes() : defaultInfo.getConsumes();
        for (String consume : consumes) {
            builder.addStatement("  route.consumes($S)", consume);
        }

        int order = methodInfo.getOrder();
        if (order != 0) {
            builder.addStatement("  route.order($L)", order);
        }
    }

    private void addParamValue(MethodSpec.Builder builder, VariableElement param) {
        TypeMirror paramType = param.asType();
        Types typesUtil = models.getTypeUtils();
        String paramName = param.getSimpleName().toString();

        CookieValue cookieValue = param.getAnnotation(CookieValue.class);
        CookieObject cookieObject = param.getAnnotation(CookieObject.class);
        SessionValue sessionValue = param.getAnnotation(SessionValue.class);
        PathParam pathParam = param.getAnnotation(PathParam.class);
        RequestHeader requestHeader = param.getAnnotation(RequestHeader.class);
        RequestParam requestParam = param.getAnnotation(RequestParam.class);
        RequestBody requestBody = param.getAnnotation(RequestBody.class);


        if (cookieValue != null) {
            String name = cookieValue.name() != null ? cookieValue.name() : cookieValue.value();
            if (name.isEmpty()) {
                name = paramName;
            }
            builder.addCode("helper.getCookieValue($T.class, ctx, $S, $L, $S)", paramType, name,
                    cookieValue.required(), noneToNull(cookieValue.defaultValue()));
        } else if (cookieObject != null) {
            String name = cookieObject.name() != null ? cookieObject.name() : cookieObject.value();
            if (name.isEmpty()) {
                name = paramName;
            }
            builder.addCode("ctx.getCookie($S)", name);
        } else if (sessionValue != null) {
            String name = sessionValue.name() != null ? sessionValue.name() : sessionValue.value();
            if (name.isEmpty()) {
                name = paramName;
            }
            builder.addCode("helper.getSessionValue($T.class, ctx, $S, $L, $S)", paramType, name,
                    sessionValue.required(), noneToNull(sessionValue.defaultValue()));
        }
        else if (pathParam != null) {
            String name = pathParam.value();
            if (name.isEmpty()) {
                name = paramName;
            }
            builder.addCode("helper.getPathParam($T.class, ctx, $S)", paramType, name);
        } else if (requestHeader != null) {
            String name = requestHeader.name() != null ? requestHeader.name() : requestHeader.value();
            if (name.isEmpty()) {
                name = paramName;
            }
            builder.addCode("helper.getReqHeader($T.class, ctx, $S, $L, $S)", paramType, name,
                    requestHeader.required(), noneToNull(requestHeader.defaultValue()));
        } else if (requestParam != null) {
            String name = requestParam.name() != null ? requestParam.name() : requestParam.value();
            if (name.isEmpty()) {
                name = paramName;
            }
            builder.addCode("helper.getReqParam($T.class, ctx, $S, $L, $S)", paramType, name,
                    requestParam.required(), noneToNull(requestParam.defaultValue()));
        } else if (requestBody != null) {
            builder.addCode("helper.getReqBody($T.class, ctx)", paramType);
        } else if (typesUtil.isSameType(routeType, paramType)) {
            builder.addCode("ctx.currentRoute()");
        } else if (typesUtil.isSameType(routeTypeCore, paramType)) {
            builder.addCode("($T) ctx.currentRoute().getDelegate()", TypeName.get(io.vertx.ext.web.Route.class));
        } else if (typesUtil.isSameType(routerType, paramType)) {
            builder.addCode("router");
        } else if (typesUtil.isSameType(routerTypeCore, paramType)) {
            builder.addCode("($T) router.getDelegate()", TypeName.get(io.vertx.ext.web.Router.class));
        } else if (typesUtil.isSameType(routingCtxType, paramType)) {
            builder.addCode("ctx");
        } else if (typesUtil.isSameType(routingCtxTypeCore, paramType)) {
            builder.addCode("($T) ctx", TypeName.get(io.vertx.ext.web.RoutingContext.class));
        } else if (typesUtil.isSameType(reqType, paramType)) {
            builder.addCode("ctx.request()");
        } else if (typesUtil.isSameType(reqTypeCore, paramType)) {
            builder.addCode("($T) ctx.request().getDelegate()", TypeName.get(io.vertx.core.http.HttpServerRequest.class));
        } else if (typesUtil.isSameType(respType, paramType)) {
            builder.addCode("ctx.response()");
        } else if (typesUtil.isSameType(respTypeCore, paramType)) {
            builder.addCode("($T) ctx.response().getDelegate()", TypeName.get(io.vertx.core.http.HttpServerResponse.class));
        } else if (typesUtil.isSameType(sessionType, paramType)) {
            builder.addCode("ctx.session()");
        } else if (typesUtil.isSameType(sessionTypeCore, paramType)) {
            builder.addCode("($T) ctx.session().getDelegate()", TypeName.get(io.vertx.ext.web.Session.class));
        } else if (paramType.getKind().isPrimitive() || typesUtil.isSameType(stringType, paramType)) {
            builder.addCode("helper.getReqParam($T.class, ctx, $S, true, null)", paramType, paramName);
        } else {
            builder.addCode("helper.getParam($T.class, ctx, $S)", typesUtil.erasure(paramType), paramName);
        }
    }

    private static String noneToNull(String defaultValue) {
        if (ValueConstants.DEFAULT_NONE.equals(defaultValue)) {
            return null;
        }
        return defaultValue;
    }

}
