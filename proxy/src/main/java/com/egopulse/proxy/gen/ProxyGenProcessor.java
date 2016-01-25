package com.egopulse.proxy.gen;

import com.egopulse.gen.Generator;
import com.egopulse.gen.Models;
import com.egopulse.gen.TypeModelAnnotationProcessor;

public class ProxyGenProcessor  extends TypeModelAnnotationProcessor {
    public ProxyGenProcessor() {
        super(ProxyGen.class);
    }

    @Override
    protected Generator createGenerator(Models models) {
        return new ProxyCodeGenerator(models);
    }
}
