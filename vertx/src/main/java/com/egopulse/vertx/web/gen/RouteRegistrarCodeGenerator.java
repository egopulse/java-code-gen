package com.egopulse.vertx.web.gen;

import com.egopulse.bson.gen.Generator;
import com.egopulse.bson.gen.Models;
import com.egopulse.vertx.web.RouteRegistrarHelper;
import com.egopulse.vertx.web.RouteRegistrar;
import com.egopulse.web.annotation.Blocking;
import com.egopulse.web.annotation.ContentType;
import com.egopulse.web.annotation.CookieObject;
import com.egopulse.web.annotation.CookieValue;
import com.egopulse.web.annotation.HttpMethod;
import com.egopulse.web.annotation.Ordered;
import com.egopulse.web.annotation.PathParam;
import com.egopulse.web.annotation.RequestBody;
import com.egopulse.web.annotation.RequestHeader;
import com.egopulse.web.annotation.RequestParam;
import com.egopulse.web.annotation.ResponseBody;
import com.egopulse.web.annotation.Restful;
import com.egopulse.web.annotation.RouteMapping;
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
import java.util.ArrayList;
import java.util.List;

public class RouteRegistrarCodeGenerator implements Generator {
    private final List<String> generatedClassNames = new ArrayList<>();

    private final Models models;
    private final TypeMirror routingCtxType;
    private final TypeMirror reqType;
    private final TypeMirror respType;
    private final TypeMirror routeType;
    private final TypeMirror sessionType;

    public RouteRegistrarCodeGenerator(Models models) {
        this.models = models;
        Elements elementsUtil = models.getElemsUtil();
        routingCtxType = elementsUtil.getTypeElement(RoutingContext.class.getCanonicalName()).asType();
        reqType = elementsUtil.getTypeElement(HttpServerRequest.class.getCanonicalName()).asType();
        respType = elementsUtil.getTypeElement(HttpServerResponse.class.getCanonicalName()).asType();
        routeType = elementsUtil.getTypeElement(Route.class.getCanonicalName()).asType();
        sessionType = elementsUtil.getTypeElement(Session.class.getCanonicalName()).asType();
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

        Restful restful = typeElem.getAnnotation(Restful.class);
        RouteMapping classMapping = typeElem.getAnnotation(RouteMapping.class);
        Blocking defaultBlocking = typeElem.getAnnotation(Blocking.class);

        List<ExecutableElement> methods = models.getPublicNonStaticAnnotatedMethod(typeElem, RouteMapping.class);
        for (ExecutableElement method : methods) {
            TypeMirror methodType = method.getReturnType();
            TypeName methodTypeName = TypeName.get(methodType);
            RouteMapping mapping = method.getAnnotation(RouteMapping.class);

            // New scope to avoid local variable names clashing
            registerMethodBuilder.addCode("{\n");

            registerMethodBuilder.addStatement("  $T route = router.route()", Route.class);
            configMapping(registerMethodBuilder, classMapping, mapping, restful != null);
            Ordered ordered = method.getAnnotation(Ordered.class);
            if (ordered != null) {
                if (ordered.value() != 0) {
                    registerMethodBuilder.addStatement("  route.order($L)", ordered.value());
                }
            }

            // Handler method
            registerMethodBuilder.addCode("  $T handler = ctx -> {\n", ParameterizedTypeName.get(Handler.class, RoutingContext.class));
            registerMethodBuilder.addCode("    try {\n");

            // Target method call
            if (methodType.getKind() != TypeKind.VOID) {
                registerMethodBuilder.addCode("    $T ret = target.$L(", methodTypeName, method.getSimpleName().toString());
            } else {
                registerMethodBuilder.addCode("    target.$L(", method.getSimpleName().toString());
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
                ResponseBody responseBody = method.getAnnotation(ResponseBody.class);
                if (responseBody != null || restful != null) {
                    registerMethodBuilder.addStatement("    helper.handleResponseBody(ctx, $T.class, ret)", methodType);
                } else {
                    registerMethodBuilder.addStatement("    helper.handleResponse(ctx, $T.class, ret)", methodType);
                }
            } else {
                registerMethodBuilder.addStatement("    ctx.next()");
            }
            //End the handler method
            registerMethodBuilder.addCode(
                    "    } catch (Throwable t) {;\n" +
                    "      helper.handleError(ctx, t);\n" +
                    "    };\n" +
                    "  };\n");


            Blocking blocking = method.getAnnotation(Blocking.class);
            if (defaultBlocking != null || blocking != null) {
                registerMethodBuilder.addStatement("  route.blockingHandler(handler)");
            } else {
                registerMethodBuilder.addStatement("  route.handler(handler)");
            }

            //End the scope
            registerMethodBuilder.addCode("}\n");
        }

        registrarClassBuilder.addMethod(registerMethodBuilder.build());
        PackageElement packageElem = Models.getPackage(typeElem);
        String packageName = packageElem.getQualifiedName().toString();
        JavaFile.builder(packageName, registrarClassBuilder.build()).build().writeTo(filer);
        generatedClassNames.add(packageName + "." + registrarClassSimpleName);
    }

    @Override
    public void generateLast(Filer filer) throws IOException {
        writeServiceNames(RouteRegistrar.class, filer, generatedClassNames);
    }

    private static void configMapping(MethodSpec.Builder builder, RouteMapping defaultMapping, RouteMapping mapping, boolean restful) {
        String path = defaultMapping != null && !defaultMapping.path().isEmpty() ? defaultMapping.path() + mapping.path() : mapping.path();
        String pathRegEx = defaultMapping != null && !defaultMapping.pathRegEx().isEmpty() ? defaultMapping.pathRegEx() + mapping.pathRegEx() : mapping.pathRegEx();
        if (!path.isEmpty()) {
            builder.addStatement("  route.path($S)", path);
        } else if (!pathRegEx.isEmpty()) {
            builder.addStatement("  route.pathRegex($S)", pathRegEx);
        }

        HttpMethod[] methods = mapping.method().length > 0 ? mapping.method() : defaultMapping != null ? defaultMapping.method() : new HttpMethod[0];
        for (HttpMethod httpMethod : methods) {
            builder.addStatement("  route.method($T.$L)", io.vertx.core.http.HttpMethod.class, httpMethod);
        }

        ContentType[] produces = mapping.produces().length > 0 ? mapping.produces() : defaultMapping != null ? defaultMapping.produces() : new ContentType[0];
        if (produces.length == 0 && restful) {
            produces = new ContentType[]{ContentType.APP_JSON};
        }
        for (ContentType contentType : produces) {
            builder.addStatement("  route.produces($S)", contentType.toString());
        }

        ContentType[] consumes = mapping.consumes().length > 0 ? mapping.consumes() : defaultMapping != null ? defaultMapping.consumes() : new ContentType[0];
        if (consumes.length == 0 && restful) {
            consumes = new ContentType[]{ContentType.APP_JSON};
        }
        for (ContentType contentType : consumes) {
            builder.addStatement("  route.consumes($S)", contentType.toString());
        }
    }

    private void addParamValue(MethodSpec.Builder builder, VariableElement param) {
        TypeMirror paramType = param.asType();
        Types typesUtil = models.getTypeUtils();

        CookieValue cookieValue = param.getAnnotation(CookieValue.class);
        CookieObject cookieObject = param.getAnnotation(CookieObject.class);
        PathParam pathParam = param.getAnnotation(PathParam.class);
        RequestHeader requestHeader = param.getAnnotation(RequestHeader.class);
        RequestParam requestParam = param.getAnnotation(RequestParam.class);
        RequestBody requestBody = param.getAnnotation(RequestBody.class);

        if (cookieValue != null) {
            String name = cookieValue.name() != null ? cookieValue.name() : cookieValue.value();
            if (name.isEmpty()) {
                name = param.getSimpleName().toString();
            }
            builder.addCode("helper.getCookieValue($T.class, ctx, $S, $L, $S)", paramType, name,
                    cookieValue.required(), noneToNull(cookieValue.defaultValue()));
        } else if (cookieObject != null) {
            String name = cookieObject.name() != null ? cookieObject.name() : cookieObject.value();
            if (name.isEmpty()) {
                name = param.getSimpleName().toString();
            }
            builder.addCode("ctx.getCookie($S)", name);
        } else if (pathParam != null) {
            String name = pathParam.value();
            if (name.isEmpty()) {
                name = param.getSimpleName().toString();
            }
            builder.addCode("helper.getPathParam($T.class, ctx, $S)", paramType, name);
        } else if (requestHeader != null) {
            String name = requestHeader.name() != null ? requestHeader.name() : requestHeader.value();
            if (name.isEmpty()) {
                name = param.getSimpleName().toString();
            }
            builder.addCode("helper.getReqHeader($T.class, ctx, $S, $L, $S)", paramType, name,
                    requestHeader.required(), noneToNull(requestHeader.defaultValue()));
        } else if (requestParam != null) {
            String name = requestParam.name() != null ? requestParam.name() : requestParam.value();
            if (name.isEmpty()) {
                name = param.getSimpleName().toString();
            }
            builder.addCode("helper.getReqParam($T.class, ctx, $S, $L, $S)", paramType, name,
                    requestParam.required(), noneToNull(requestParam.defaultValue()));
        } else if (requestBody != null) {
            builder.addCode("helper.getReqBody($T.class, ctx)", paramType);
        } else if (typesUtil.isSameType(routeType, paramType)) {
            builder.addCode("ctx.currentRoute()");
        } else if (typesUtil.isSameType(routingCtxType, paramType)) {
            builder.addCode("ctx");
        } else if (typesUtil.isSameType(reqType, paramType)) {
            builder.addCode("ctx.request()");
        } else if (typesUtil.isSameType(respType, paramType)) {
            builder.addCode("ctx.response()");
        } else if (typesUtil.isSameType(sessionType, paramType)) {
            builder.addCode("ctx.session()");
        } else {
            builder.addCode("helper.getParam($T.class, ctx)", paramType);
        }
    }

    private static String noneToNull(String defaultValue) {
        if (ValueConstants.DEFAULT_NONE.equals(defaultValue)) {
            return null;
        }
        return defaultValue;
    }
}
