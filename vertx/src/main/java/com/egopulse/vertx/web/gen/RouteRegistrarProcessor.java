package com.egopulse.vertx.web.gen;

import com.egopulse.bson.gen.Generator;
import com.egopulse.bson.gen.Models;
import com.egopulse.bson.gen.TypeModelAnnotationProcessor;
import com.egopulse.web.annotation.Restful;
import com.egopulse.web.annotation.RouteHandlers;

public class RouteRegistrarProcessor extends TypeModelAnnotationProcessor {
    protected RouteRegistrarProcessor() {
        super(RouteHandlers.class, Restful.class);
    }

    @Override
    protected Generator createGenerator(Models models) {
        return new RouteRegistrarCodeGenerator(models);
    }
}
