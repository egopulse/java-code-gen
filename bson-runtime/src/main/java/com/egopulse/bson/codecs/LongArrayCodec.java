package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.ArrayList;
import java.util.List;

public class LongArrayCodec implements Codec<long[]> {
    @Override
    public long[] decode(BsonReader reader, DecoderContext decoderContext) {
        List<Long> list = new ArrayList<>();
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            list.add(reader.readInt64());
        }
        reader.readEndArray();
        int length = list.size();
        long[] ret = new long[length];
        for (int i = 0; i < length; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    @Override
    public void encode(BsonWriter writer, long[] value, EncoderContext encoderContext) {
        writer.writeStartArray();
        for (long aValue : value) {
            writer.writeInt64(aValue);
        }
        writer.writeEndArray();
    }

    @Override
    public Class<long[]> getEncoderClass() {
        return long[].class;
    }
}
