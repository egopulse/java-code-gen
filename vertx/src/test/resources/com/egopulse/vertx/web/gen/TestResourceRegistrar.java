package com.egopulse.vertx.web.gen;

import com.egopulse.vertx.web.RouteRegistrar;
import com.egopulse.vertx.web.RouteRegistrarHelper;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Session;
import io.vertx.rxjava.ext.web.Route;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import java.lang.String;

public class TestResourceRegistrar implements RouteRegistrar<TestResource> {
    public void register(final Router router, final RouteRegistrarHelper helper, final TestResource target) {
        {
            Route route = router.route();
            route.path("/api/test/:pathParam");
            route.method(HttpMethod.GET);
            route.method(HttpMethod.POST);
            route.produces("application/json");
            route.consumes("application/json");
            Handler<RoutingContext> handler = ctx -> {
                String ret = target.test(helper.getPathParam(String.class, ctx, "pathParam"), helper.getReqParam(int.class, ctx, "reqParam", true, null), helper.getCookieValue(boolean.class, ctx, "cookieValue", true, null));
                helper.handleResponseBody(ctx, String.class, ret);
            };
            route.handler(handler);
        }
        {
            Route route = router.route();
            route.path("/api/test2");
            route.method(HttpMethod.POST);
            route.produces("application/json");
            route.consumes("application/json");
            route.order(1000);
            Handler<RoutingContext> handler = ctx -> {
                TestResource.TestBean ret = target.test2(helper.getParam(Session.class, ctx), helper.getParam(io.vertx.ext.web.Route.class, ctx), helper.getParam(io.vertx.ext.web.RoutingContext.class, ctx), helper.getParam(HttpServerRequest.class, ctx), helper.getParam(HttpServerResponse.class, ctx));
                helper.handleResponseBody(ctx, TestResource.TestBean.class, ret);
            };
            route.blockingHandler(handler);
        }
    }
}
