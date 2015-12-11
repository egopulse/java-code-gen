package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public final class ByteCodec implements Codec<Byte> {
    @Override
    public Byte decode(BsonReader reader, DecoderContext decoderContext) {
        return (byte) reader.readInt32();
    }

    @Override
    public void encode(BsonWriter writer, Byte value, EncoderContext encoderContext) {
        writer.writeInt32(value.intValue());
    }

    @Override
    public Class<Byte> getEncoderClass() {
        return Byte.class;
    }
}
