package com.egopulse.vertx.web;


import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.sstore.SessionStore;
import rx.Observable;
import rx.Subscriber;

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

    default <T> T getParam(Class<T> clazz, RoutingContext ctx) {
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
            return (T) ctx.getBodyAsJson();
        }
        return stringToObject(clazz, ctx.getBodyAsString());
    }

    <R> void handleResponse(RoutingContext ctx, Class<R> retType, R ret);

    void handleError(RoutingContext ctx, Throwable e);

    default <R> void handleSingleResponseBody(RoutingContext ctx, Class<R> clazz,  R ret) {
        HttpServerResponse resp = ctx.response();
        if (ret != null) {
            if (ret instanceof String) {
                resp.write((String) ret);
            } else if (ret instanceof Buffer) {
                resp.write((Buffer) ret);
            } else if (ret instanceof JsonObject) {
                resp.write(((JsonObject) ret).encode());
            } else if (ret instanceof JsonArray) {
                resp.write(((JsonArray) ret).encode());
            } else {
                resp.write(objectToString(ret));
            }
        }
    }

    @SuppressWarnings("unchecked")
    default <R> void handleResponseBody(RoutingContext ctx, Class<R> clazz, R ret) {
        HttpServerResponse resp = ctx.response();
        if (ret != null) {
            resp.putHeader("content-type", ctx.getAcceptableContentType());
            if (ret instanceof Observable) {
                ((Observable) ret).subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        resp.end();
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleError(ctx, e);
                    }

                    @Override
                    public void onNext(Object o) {
                        if (o != null) {
                            handleSingleResponseBody(ctx, (Class) o.getClass(), o);
                        }
                    }
                });
            } else {
                handleSingleResponseBody(ctx, clazz, ret);
                resp.end();
            }
        } else {
            resp.end();
        }

    }
}
