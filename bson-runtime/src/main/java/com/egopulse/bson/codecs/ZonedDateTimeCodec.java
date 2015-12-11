package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class ZonedDateTimeCodec implements Codec<ZonedDateTime> {
    private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

    @Override
    public ZonedDateTime decode(BsonReader reader, DecoderContext decoderContext) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(reader.readDateTime()), UTC_ZONE_ID);
    }

    @Override
    public void encode(BsonWriter writer, ZonedDateTime value, EncoderContext encoderContext) {
        writer.writeDateTime(value.toInstant().toEpochMilli());
    }

    @Override
    public Class<ZonedDateTime> getEncoderClass() {
        return ZonedDateTime.class;
    }
}
