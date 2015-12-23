package com.egopulse.vertx.web.gen;

import com.egopulse.vertx.web.RouteRegistrar;
import com.egopulse.vertx.web.RouteRegistrarHelper;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
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
                TestResource.TestBean ret = target.test2(ctx.session(), ctx.currentRoute(), ctx, ctx.request(), ctx.response());
                helper.handleResponseBody(ctx, TestResource.TestBean.class, ret);
            };
            route.blockingHandler(handler);
        }
    }
}