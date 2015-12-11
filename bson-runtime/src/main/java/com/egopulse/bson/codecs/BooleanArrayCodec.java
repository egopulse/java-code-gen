package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.ArrayList;
import java.util.List;

public class BooleanArrayCodec implements Codec<boolean[]> {
    @Override
    public boolean[] decode(BsonReader reader, DecoderContext decoderContext) {
        List<Boolean> list = new ArrayList<>();
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            list.add(reader.readBoolean());
        }
        reader.readEndArray();
        int length = list.size();
        boolean[] ret = new boolean[length];
        for (int i = 0; i < length; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    @Override
    public void encode(BsonWriter writer, boolean[] value, EncoderContext encoderContext) {
        writer.writeStartArray();
        for (boolean aValue : value) {
            writer.writeBoolean(aValue);
        }
        writer.writeEndArray();
    }

    @Override
    public Class<boolean[]> getEncoderClass() {
        return boolean[].class;
    }
}
