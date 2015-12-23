package com.egopulse.vertx.web;

import io.vertx.ext.web.Router;

public interface RouteRegistrar<T> {
    void register(Router router, RouteRegistrarHelper helper, T target);


}
