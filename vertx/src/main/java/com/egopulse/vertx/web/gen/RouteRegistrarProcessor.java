package com.egopulse.vertx.web.gen;

import com.egopulse.gen.Generator;
import com.egopulse.gen.Models;
import com.egopulse.gen.TypeModelAnnotationProcessor;
import com.egopulse.web.annotation.Restful;
import com.egopulse.web.annotation.RouteHandlers;

public class RouteRegistrarProcessor extends TypeModelAnnotationProcessor {
    public RouteRegistrarProcessor() {
        super(RouteHandlers.class, Restful.class);
    }

    @Override
    protected Generator createGenerator(Models models) {
        return new RouteRegistrarCodeGenerator(models);
    }
}
