package com.egopulse.vertx.web;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.sstore.SessionStore;

public interface RouteRegistrarHelper {
    <T> String objectToString(Class<T> clazz, T val);

    <T> T stringToObject(Class<T> clazz, String val);

    default void throwMissingValue(String message) {
        throw new RuntimeException(message);
    }

    SessionStore getSessionStore();

    <T> T getParam(Class<T> clazz, RoutingContext ctx);

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

    default <R> void handleResponseBody(RoutingContext ctx, Class<R> retType, R ret) {
        HttpServerResponse resp = ctx.response();
        resp.putHeader("content-type", ctx.getAcceptableContentType());
        if (ret != null) {
            if (ret instanceof String) {
                resp.write((String) ret);
            } else if (ret instanceof Buffer) {
                resp.write((Buffer) ret);
            } else if (ret instanceof JsonObject) {
                resp.write(((JsonObject) ret).encode());
            } else {
                resp.write(objectToString(retType, ret));
            }
        }
        resp.end();
    }
}