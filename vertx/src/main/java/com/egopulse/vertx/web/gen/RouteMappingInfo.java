package com.egopulse.vertx.web.gen;

import com.egopulse.web.annotation.Blocking;
import com.egopulse.web.annotation.CONNECT;
import com.egopulse.web.annotation.Consume;
import com.egopulse.web.annotation.ContentType;
import com.egopulse.web.annotation.DELETE;
import com.egopulse.web.annotation.GET;
import com.egopulse.web.annotation.HEAD;
import com.egopulse.web.annotation.HttpMethod;
import com.egopulse.web.annotation.Method;
import com.egopulse.web.annotation.Ordered;
import com.egopulse.web.annotation.PATCH;
import com.egopulse.web.annotation.POST;
import com.egopulse.web.annotation.PUT;
import com.egopulse.web.annotation.Path;
import com.egopulse.web.annotation.Produce;
import com.egopulse.web.annotation.ResponseBody;
import com.egopulse.web.annotation.ResponseNext;
import com.egopulse.web.annotation.Restful;
import com.egopulse.web.annotation.RouteMapping;
import com.egopulse.web.annotation.TRACE;

import javax.lang.model.element.Element;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

class RouteMappingInfo {
    private final boolean responseBody;
    private final boolean blocking;
    private final Set<String> produces;
    private final Set<String> consumes;
    private final Set<HttpMethod> methods;
    private final int order;
    private final String path;
    private final String pathRegex;
    private final boolean responseNext;

    RouteMappingInfo(Element element) {
        responseBody = isResponseBody(element);
        blocking = isBlocking(element);
        produces = extractProduces(element);
        consumes = extractConsumes(element);
        methods = extractMethods(element);
        order = extractOrder(element);
        path = extractPath(element);
        pathRegex = extractPathRegEx(element);
        responseNext = extractResponseEnd(element);
    }

    boolean isResponseBody() {
        return responseBody;
    }

    boolean isBlocking() {
        return blocking;
    }

    Set<String> getProduces() {
        return produces;
    }

    Set<String> getConsumes() {
        return consumes;
    }

    Set<HttpMethod> getMethods() {
        return methods;
    }

    int getOrder() {
        return order;
    }

    String getPath() {
        return path;
    }

    String getPathRegex() {
        return pathRegex;
    }

    boolean isResponseNext() {
        return responseNext;
    }

    private static boolean isResponseBody(Element element) {
        return element.getAnnotation(Restful.class) != null || element.getAnnotation(ResponseBody.class) != null;
    }

    private static Set<HttpMethod> extractMethods(Element element) {
        Set<HttpMethod> ret = new HashSet<>();
        if (element.getAnnotation(GET.class) != null) {
            ret.add(HttpMethod.GET);
        }
        if (element.getAnnotation(PUT.class) != null) {
            ret.add(HttpMethod.PUT);
        }
        if (element.getAnnotation(POST.class) != null) {
            ret.add(HttpMethod.POST);
        }
        if (element.getAnnotation(DELETE.class) != null) {
            ret.add(HttpMethod.DELETE);
        }
        if (element.getAnnotation(PATCH.class) != null) {
            ret.add(HttpMethod.PATCH);
        }
        if (element.getAnnotation(HEAD.class) != null) {
            ret.add(HttpMethod.HEAD);
        }
        if (element.getAnnotation(TRACE.class) != null) {
            ret.add(HttpMethod.TRACE);
        }
        if (element.getAnnotation(CONNECT.class) != null) {
            ret.add(HttpMethod.CONNECT);
        }
        Method methods = element.getAnnotation(Method.class);
        if (methods != null) {
            Collections.addAll(ret, methods.value());
        }
        RouteMapping routeMapping = element.getAnnotation(RouteMapping.class);
        if (routeMapping != null) {
            Collections.addAll(ret, routeMapping.method());
        }
        return ret;
    }

    private static Set<String> extractProduces(Element element) {
        Produce produce = element.getAnnotation(Produce.class);
        Set<String> ret = produce == null ? new HashSet<>() : extractContentTypes(produce.value(),
                produce.type(), produce.custom());
        RouteMapping routeMapping = element.getAnnotation(RouteMapping.class);
        if (routeMapping != null) {
            for (ContentType type : routeMapping.produces()) {
                ret.add(type.toString());
            }
        }
        Restful restful = element.getAnnotation(Restful.class);
        if (restful != null) {
            ret.add(ContentType.APP_JSON.toString());
        }
        return ret;
    }

    private static Set<String> extractContentTypes(ContentType[] values, ContentType[] types, String[] customs) {
        Set<String> ret = new HashSet<>();
        for (ContentType type : values) {
            ret.add(type.toString());
        }
        for (ContentType type : types) {
            ret.add(type.toString());
        }
        Collections.addAll(ret, customs);
        return ret;
    }

    private static Set<String> extractConsumes(Element element) {
        Consume consume = element.getAnnotation(Consume.class);
        Set<String> ret = consume == null ? new HashSet<>() : extractContentTypes(consume.value(), 
                consume.type(), consume.custom());
        RouteMapping routeMapping = element.getAnnotation(RouteMapping.class);
        if (routeMapping != null) {
            for (ContentType type : routeMapping.consumes()) {
                ret.add(type.toString());
            }
        }
        return ret;
    }

    private static String extractPath(Element element) {
        Path path = element.getAnnotation(Path.class);
        if (path != null) {
            return path.value();
        }
        RouteMapping routeMapping = element.getAnnotation(RouteMapping.class);
        if (routeMapping != null) {
            return routeMapping.path();
        }
        return "";
    }

    private static String extractPathRegEx(Element element) {
        Path path = element.getAnnotation(Path.class);
        if (path != null) {
            return path.regex();
        }
        RouteMapping routeMapping = element.getAnnotation(RouteMapping.class);
        if (routeMapping != null) {
            return routeMapping.pathRegEx();
        }
        return "";
    }

    private static boolean isBlocking(Element element) {
        return element.getAnnotation(Blocking.class) != null;
    }

    private static int extractOrder(Element element) {
        Ordered ordered = element.getAnnotation(Ordered.class);
        if (ordered != null) {
            return ordered.value();
        }
        return 0;
    }

    private static boolean extractResponseEnd(Element element) {
        return element.getAnnotation(ResponseNext.class) != null;
    }
}
