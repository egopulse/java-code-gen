package com.egopulse.vertx.web.gen;

import com.egopulse.web.annotation.Blocking;
import com.egopulse.web.annotation.CONNECT;
import com.egopulse.web.annotation.Consume;
import com.egopulse.web.annotation.ContentType;
import com.egopulse.web.annotation.CookieValue;
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
import com.egopulse.web.annotation.Restful;
import com.egopulse.web.annotation.RouteMapping;
import com.egopulse.web.annotation.TRACE;

import javax.lang.model.element.Element;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class RouteMappingInfo {
    private final boolean responseBody;
    private final boolean blocking;
    private final Set<String> produces;
    private final Set<String> consumes;
    private final Set<HttpMethod> methods;
    private final int order;
    private final String path;
    private final String pathRegex;

    public RouteMappingInfo(Element element) {
        responseBody = isResponseBody(element);
        blocking = isBlocking(element);
        produces = extractProduces(element);
        consumes = extractConsumes(element);
        methods = extractMethods(element);
        order = extractOrder(element);
        path = extractPath(element);
        pathRegex = extractPathRegEx(element);
    }

    public boolean isResponseBody() {
        return responseBody;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public Set<String> getProduces() {
        return produces;
    }

    public Set<String> getConsumes() {
        return consumes;
    }

    public Set<HttpMethod> getMethods() {
        return methods;
    }

    public int getOrder() {
        return order;
    }

    public String getPath() {
        return path;
    }

    public String getPathRegex() {
        return pathRegex;
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
        Set<String> ret = new HashSet<>();
        Produce produce = element.getAnnotation(Produce.class);
        if (produce != null) {
            for (ContentType type : produce.value()) {
                ret.add(type.toString());
            }
            for (ContentType type : produce.type()) {
                ret.add(type.toString());
            }

            Collections.addAll(ret, produce.custom());
            RouteMapping routeMapping = element.getAnnotation(RouteMapping.class);
            for (ContentType type: routeMapping.produces()) {
                ret.add(type.toString());
            }
        }
        Restful restful = element.getAnnotation(Restful.class);
        if (restful != null) {
            ret.add(ContentType.APP_JSON.toString());
        }
        return ret;
    }

    private static Set<String> extractConsumes(Element element) {
        Set<String> ret = new HashSet<>();
        Consume consume = element.getAnnotation(Consume.class);
        if (consume != null) {
            for (ContentType type : consume.value()) {
                ret.add(type.toString());
            }
            for (ContentType type : consume.type()) {
                ret.add(type.toString());
            }
            Collections.addAll(ret, consume.custom());
            RouteMapping routeMapping = element.getAnnotation(RouteMapping.class);
            for (ContentType type : routeMapping.consumes()) {
                ret.add(type.toString());
            }
        }
        Restful restful = element.getAnnotation(Restful.class);
        if (restful != null) {
            ret.add(ContentType.APP_JSON.toString());
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
}
