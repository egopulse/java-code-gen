package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class CharArrayCodec implements Codec<char[]> {
    @Override
    public char[] decode(BsonReader reader, DecoderContext decoderContext) {
        return reader.readString().toCharArray();
    }

    @Override
    public void encode(BsonWriter writer, char[] value, EncoderContext encoderContext) {
        writer.writeString(String.valueOf(value));
    }

    @Override
    public Class<char[]> getEncoderClass() {
        return char[].class;
    }
}
