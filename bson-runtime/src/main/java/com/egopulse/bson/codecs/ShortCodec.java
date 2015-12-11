package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public final class ShortCodec implements Codec<Short> {
    @Override
    public Short decode(BsonReader reader, DecoderContext decoderContext) {
        return (short) reader.readInt32();
    }

    @Override
    public void encode(BsonWriter writer, Short value, EncoderContext encoderContext) {
        writer.writeInt32(value.intValue());
    }

    @Override
    public Class<Short> getEncoderClass() {
        return Short.class;
    }
}
