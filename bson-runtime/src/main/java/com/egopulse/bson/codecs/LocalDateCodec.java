package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.LocalDate;

public final class LocalDateCodec implements Codec<LocalDate> {
    private static final long MILLISECONDS_IN_A_DAY = 1000 * 60 * 60 * 24;
    @Override
    public LocalDate decode(BsonReader reader, DecoderContext decoderContext) {
        return LocalDate.ofEpochDay(reader.readDateTime() / MILLISECONDS_IN_A_DAY);
    }

    @Override
    public void encode(BsonWriter writer, LocalDate value, EncoderContext encoderContext) {
        writer.writeDateTime(value.toEpochDay() * MILLISECONDS_IN_A_DAY);
    }

    @Override
    public Class<LocalDate> getEncoderClass() {
        return LocalDate.class;
    }
}
