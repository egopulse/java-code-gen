package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.Instant;

public final class InstantCodec implements Codec<Instant> {
    @Override
    public Instant decode(BsonReader reader, DecoderContext decoderContext) {
        return Instant.ofEpochMilli(reader.readDateTime());
    }

    @Override
    public void encode(BsonWriter writer, Instant value, EncoderContext encoderContext) {
        writer.writeDateTime(value.toEpochMilli());
    }

    @Override
    public Class<Instant> getEncoderClass() {
        return Instant.class;
    }
}
