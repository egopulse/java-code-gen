package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public final class FloatCodec implements Codec<Float> {
    @Override
    public Float decode(BsonReader reader, DecoderContext decoderContext) {
        return (float) reader.readDouble();
    }

    @Override
    public void encode(BsonWriter writer, Float value, EncoderContext encoderContext) {
        writer.writeDouble(value.doubleValue());
    }

    @Override
    public Class<Float> getEncoderClass() {
        return Float.class;
    }
}
