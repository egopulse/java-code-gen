package com.egopulse.bson.gen;

public class BsonAnnoProcessor extends TypeModelAnnotationProcessor {
    protected BsonAnnoProcessor() {
        super(Bson.class);
    }

    @Override
    protected Generator createGenerator(Models models) {
        return new BsonCodecGenerator(models);
    }
}
