package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public final class LocalDateTimeCodec implements Codec<LocalDateTime> {
    @Override
    public LocalDateTime decode(BsonReader reader, DecoderContext decoderContext) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(reader.readDateTime()), ZoneId.of("UTC"));
    }

    @Override
    public void encode(BsonWriter writer, LocalDateTime value, EncoderContext encoderContext) {
        writer.writeDateTime(value.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    @Override
    public Class<LocalDateTime> getEncoderClass() {
        return LocalDateTime.class;
    }
}
