package com.egopulse.vertx.web.gen;

import com.egopulse.web.annotation.Blocking;
import com.egopulse.web.annotation.Consume;
import com.egopulse.web.annotation.CookieValue;
import com.egopulse.web.annotation.GET;
import com.egopulse.web.annotation.HttpMethod;
import com.egopulse.web.annotation.Method;
import com.egopulse.web.annotation.Ordered;
import com.egopulse.web.annotation.Path;
import com.egopulse.web.annotation.PathParam;
import com.egopulse.web.annotation.RequestParam;
import com.egopulse.web.annotation.Restful;
import com.egopulse.web.annotation.RouteMapping;
import com.egopulse.web.annotation.SessionValue;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

@Restful
@RouteMapping(path = "/api")
public class TestResource {

    @RouteMapping(path = "/test/:pathParam", method = {HttpMethod.GET, HttpMethod.POST})
    public String test(@PathParam String pathParam, @RequestParam int reqParam, @CookieValue boolean cookieValue) {
        return "aaa";
    }

    @Ordered(1000)
    @Blocking
    @RouteMapping(path = "/test2", method = HttpMethod.POST)
    public TestBean test2(Session session, Route route, RoutingContext ctx, HttpServerRequest req, HttpServerResponse resp) {
        return new TestBean("aaa");
    }

    @GET
    @Path("/test3")
    public String test3() {
        return "bbbb";
    }

    @Method(HttpMethod.DELETE)
    @Path("/test3")
    public String test3Delete(@SessionValue String sessionValue, String name, TestBean bean) {
        return "bbbb";
    }

    public static class TestBean {
        private final String name;

        public TestBean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
