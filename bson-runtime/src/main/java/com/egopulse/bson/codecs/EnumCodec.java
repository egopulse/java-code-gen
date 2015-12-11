package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class EnumCodec<T extends Enum<T>> implements Codec<T> {
    private final Class<T> clazz;

    public EnumCodec(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        return Enum.valueOf(clazz, reader.readString());
    }

    @Override
    public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
        writer.writeString(value.toString());
    }

    @Override
    public Class<T> getEncoderClass() {
        return clazz;
    }
}
