package com.egopulse.bson.gen;

public class BsonProcessor extends TypeModelAnnotationProcessor {
    protected BsonProcessor() {
        super(Bson.class);
    }

    @Override
    protected Generator createGenerator(Models models) {
        return new BsonCodecGenerator(models);
    }
}
