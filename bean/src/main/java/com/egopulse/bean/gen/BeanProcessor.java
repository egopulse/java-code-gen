package com.egopulse.bean.gen;

import com.egopulse.gen.Generator;
import com.egopulse.gen.Models;
import com.egopulse.gen.TypeModelAnnotationProcessor;

public class BeanProcessor extends TypeModelAnnotationProcessor {
    public BeanProcessor() {
        super(Bean.class);
    }

    @Override
    protected Generator createGenerator(Models models) {
        return new BeanGenerator(models);
    }
}
