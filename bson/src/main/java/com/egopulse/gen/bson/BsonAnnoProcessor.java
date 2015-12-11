package com.egopulse.gen.bson;

import com.egopulse.gen.Generator;
import com.egopulse.gen.Models;
import com.egopulse.gen.TypeModelAnnotationProcessor;
public class BsonAnnoProcessor extends TypeModelAnnotationProcessor {
    protected BsonAnnoProcessor() {
        super(Bson.class);
    }

    @Override
    protected Generator createGenerator(Models models) {
        return new BsonCodecGenerator(models);
    }
}
