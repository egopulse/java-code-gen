package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.ArrayList;
import java.util.List;

public class ShortArrayCodec implements Codec<short[]> {
    @Override
    public short[] decode(BsonReader reader, DecoderContext decoderContext) {
        List<Short> list = new ArrayList<>();
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            list.add((short) reader.readInt32());
        }
        reader.readEndArray();
        int length = list.size();
        short[] ret = new short[length];
        for (int i = 0; i < length; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    @Override
    public void encode(BsonWriter writer, short[] value, EncoderContext encoderContext) {
        writer.writeStartArray();
        for (int aValue : value) {
            writer.writeInt32(aValue);
        }
        writer.writeEndArray();
    }

    @Override
    public Class<short[]> getEncoderClass() {
        return short[].class;
    }
}
