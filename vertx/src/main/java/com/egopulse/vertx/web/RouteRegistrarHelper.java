package com.egopulse.vertx.web;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.sstore.SessionStore;
import rx.Observable;
import rx.Single;

public interface RouteRegistrarHelper {
    default <T> String objectToString(T val) {
        return Json.encode(val);
    }

    default <T> T stringToObject(Class<T> clazz, String val) {
        return Json.decodeValue(val, clazz);
    }

    default void throwMissingValue(String message) {
        throw new RuntimeException(message);
    }

    SessionStore getSessionStore();

    default <T> T getParam(Class<T> clazz, RoutingContext ctx, String name) {
        return null;
    }

    @SuppressWarnings("unchecked")
    default <T> T getPathParam(Class<T> clazz, RoutingContext ctx, String name) {
        String val = ctx.request().getParam(name);
        if (val == null) {
            throwMissingValue(String.format("No parameter %s", name));
        }
        if (String.class.equals(clazz)) {
            return (T) val;
        }
        return stringToObject(clazz, val);
    }

    @SuppressWarnings("unchecked")
    default <T> T getReqParam(Class<T> clazz, RoutingContext ctx, String name, boolean required, String defaultVal) {
        String val = ctx.request().getParam(name);
        if (required && val == null) {
            throwMissingValue(String.format("No parameter %s", name));
        }
        if (val == null) {
            val = defaultVal;
        }
        if (String.class.equals(clazz)) {
            return (T) val;
        }
        return stringToObject(clazz, val);
    }

    @SuppressWarnings("unchecked")
    default <T> T getCookieValue(Class<T> clazz, RoutingContext ctx, String name, boolean required, String defaultVal) {
        String val = ctx.getCookie(name).getValue();
        if (required && val == null) {
            throwMissingValue(String.format("No cookie value %s", name));
        }
        if (val == null) {
            val = defaultVal;
        }
        if (String.class.equals(clazz)) {
            return (T) val;
        }
        return stringToObject(clazz, val);
    }

    @SuppressWarnings("unchecked")
    default <T> T getSessionValue(Class<T> clazz, RoutingContext ctx, String name, boolean required, String defaultVal) {
        T val = ctx.session().get(name);
        if (required && val == null) {
            throwMissingValue(String.format("No session value %s", name));
        }
        if (val == null && defaultVal != null) {
            val = stringToObject(clazz, defaultVal);
        }
        return val;
    }

    @SuppressWarnings("unchecked")
    default <T> T getReqHeader(Class<T> clazz, RoutingContext ctx, String name, boolean required, String defaultVal) {
        String val = ctx.request().getHeader(name);
        if (required && val == null) {
            throwMissingValue(String.format("No request header %s", name));
        }
        if (val == null) {
            val = defaultVal;
        }
        if (String.class.equals(clazz)) {
            return (T) val;
        }
        return stringToObject(clazz, val);
    }

    @SuppressWarnings("unchecked")
    default <T> T getReqBody(Class<T> clazz, RoutingContext ctx) {
        if (String.class.equals(clazz)) {
            return (T) ctx.getBodyAsString();
        }
        if (JsonObject.class.equals(clazz)) {
            return (T) ctx.getBodyAsJson();
        }
        if (Buffer.class.equals(clazz)) {
            return (T) ctx.getBody();
        }
        return stringToObject(clazz, ctx.getBodyAsString());
    }

    @SuppressWarnings("unchecked")
    default <R> void handleResponse(RoutingContext ctx, Class<R> retType, R ret) {
        if (ret != null) {
            Single single = null;
            if (ret instanceof Observable) {
                single = ((Observable) ret).toList().toSingle();
            } else if (ret instanceof Single) {
                single = (Single) ret;
            }
            if (single != null) {
                single.subscribe(
                        r -> handleSingleResponse(ctx, (Class) r.getClass(), r),
                        e -> handleError(ctx, (Throwable) e));
            } else {
                handleSingleResponse(ctx, retType, ret);
            }
        } else {
            ctx.next();
        }
    }

    <R> void handleSingleResponse(RoutingContext ctx, Class<R> retType, R ret);

    void handleError(RoutingContext ctx, Throwable e);

    default <R> void handleSingleResponseBody(RoutingContext ctx, Class<R> clazz,  R ret) {
        HttpServerResponse resp = ctx.response();
        if (ret != null) {
            if (ret instanceof String) {
                resp.end((String) ret);
            } else if (ret instanceof Buffer) {
                resp.end((Buffer) ret);
            } else if (ret instanceof JsonObject) {
                resp.end(((JsonObject) ret).encode());
            } else if (ret instanceof JsonArray) {
                resp.end(((JsonArray) ret).encode());
            } else {
                resp.end(objectToString(ret));
            }
        }
    }

    @SuppressWarnings("unchecked")
    default <R> void handleResponseBody(RoutingContext ctx, Class<R> retType, R ret) {
        HttpServerResponse resp = ctx.response();
        if (ret != null) {
            resp.putHeader("content-type", ctx.getAcceptableContentType());
            Single single = null;
            if (ret instanceof Observable) {
                single = ((Observable) ret).toSingle();
            } else if (ret instanceof Single) {
                single = (Single) ret;
            }
            if (single != null) {
                single.subscribe(
                        r -> handleSingleResponseBody(ctx, (Class) r.getClass(), r),
                        e -> handleError(ctx, (Throwable) e));
            } else {
                handleSingleResponseBody(ctx, retType, ret);
            }
        } else {
            resp.end();
        }

    }
}
