package com.egopulse.gen.bean;

import com.egopulse.gen.Generator;
import com.egopulse.gen.Models;
import com.egopulse.gen.TypeModelAnnotationProcessor;

public class BeanAnnoProcessor extends TypeModelAnnotationProcessor {
    public BeanAnnoProcessor() {
        super(Bean.class);
    }

    @Override
    protected Generator createGenerator(Models models) {
        return new BeanGenerator(models);
    }
}
