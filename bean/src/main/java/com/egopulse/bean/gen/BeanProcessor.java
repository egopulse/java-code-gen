package com.egopulse.bean.gen;

import com.egopulse.bson.gen.Generator;
import com.egopulse.bson.gen.Models;
import com.egopulse.bson.gen.TypeModelAnnotationProcessor;

public class BeanProcessor extends TypeModelAnnotationProcessor {
    public BeanProcessor() {
        super(Bean.class);
    }

    @Override
    protected Generator createGenerator(Models models) {
        return new BeanGenerator(models);
    }
}
