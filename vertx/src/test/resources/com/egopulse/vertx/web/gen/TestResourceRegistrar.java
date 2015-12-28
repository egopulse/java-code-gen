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
import java.lang.Class;
import java.lang.String;

public class TestResourceRegistrar implements RouteRegistrar<TestResource> {
    public void register(final Router router, final RouteRegistrarHelper helper, final TestResource target) {
        {
            Route route = router.route();
            route.path("/api/test/:pathParam");
            route.method(HttpMethod.GET);
            route.method(HttpMethod.POST);
            route.produces("application/json");
            Handler<RoutingContext> handler = ctx -> {
                try {
                    String ret = target.test(helper.getPathParam(String.class, ctx, "pathParam"), helper.getReqParam(int.class, ctx, "reqParam", true, null), helper.getCookieValue(boolean.class, ctx, "cookieValue", true, null));
                    helper.handleResponseBody(ctx, String.class, ret);
                } catch (Throwable t) {;
                    helper.handleError(ctx, t);
                };
            };
            route.handler(handler);
        }
        {
            Route route = router.route();
            route.path("/api/test2");
            route.method(HttpMethod.POST);
            route.produces("application/json");
            route.order(1000);
            Handler<RoutingContext> handler = ctx -> {
                try {
                    TestResource.TestBean ret = target.test2((Session) ctx.session().getDelegate(), (io.vertx.ext.web.Route) ctx.currentRoute().getDelegate(), (io.vertx.ext.web.RoutingContext) ctx, (HttpServerRequest) ctx.request().getDelegate(), (HttpServerResponse) ctx.response().getDelegate());
                    helper.handleResponseBody(ctx, TestResource.TestBean.class, ret);
                } catch (Throwable t) {;
                    helper.handleError(ctx, t);
                };
            };
            route.blockingHandler(handler);
        }
        {
            Route route = router.route();
            route.path("/api/test3");
            route.method(HttpMethod.GET);
            route.produces("application/json");
            Handler<RoutingContext> handler = ctx -> {
                try {
                    String ret = target.test3();
                    helper.handleResponseBody(ctx, String.class, ret);
                } catch (Throwable t) {;
                    helper.handleError(ctx, t);
                };
            };
            route.handler(handler);
        }
        {
            Route route = router.route();
            route.path("/api/test3");
            route.method(HttpMethod.DELETE);
            route.produces("application/json");
            Handler<RoutingContext> handler = ctx -> {
                try {
                    String ret = target.test3Delete(helper.getSessionValue(String.class, ctx, "sessionValue", true, null), helper.getReqParam(String.class, ctx, "name", true, null), helper.getParam(TestResource.TestBean.class, ctx, "bean"));
                    helper.handleResponseBody(ctx, String.class, ret);
                } catch (Throwable t) {;
                    helper.handleError(ctx, t);
                };
            };
            route.handler(handler);
        }
    }

    public Class<TestResource> getTargetType() {
        return TestResource.class;
    }
}