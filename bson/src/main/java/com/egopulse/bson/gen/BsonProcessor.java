package com.egopulse.bson.gen;

import com.egopulse.gen.Generator;
import com.egopulse.gen.Models;
import com.egopulse.gen.TypeModelAnnotationProcessor;

public class BsonProcessor extends TypeModelAnnotationProcessor {
    public BsonProcessor() {
        super(Bson.class);
    }

    @Override
    protected Generator createGenerator(Models models) {
        return new BsonCodecGenerator(models);
    }
}
