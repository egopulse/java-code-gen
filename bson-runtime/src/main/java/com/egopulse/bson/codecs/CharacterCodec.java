package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public final class CharacterCodec implements Codec<Character> {
    @Override
    public Character decode(BsonReader reader, DecoderContext decoderContext) {
        return reader.readString().charAt(0);
    }

    @Override
    public void encode(BsonWriter writer, Character value, EncoderContext encoderContext) {
        writer.writeString(String.valueOf(value.charValue()));
    }

    @Override
    public Class<Character> getEncoderClass() {
        return Character.class;
    }
}
