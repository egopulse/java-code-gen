package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.ArrayList;
import java.util.List;

public class FloatArrayCodec implements Codec<float[]> {
    @Override
    public float[] decode(BsonReader reader, DecoderContext decoderContext) {
        List<Float> list = new ArrayList<>();
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            list.add((float) reader.readDouble());
        }
        reader.readEndArray();
        int length = list.size();
        float[] ret = new float[length];
        for (int i = 0; i < length; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    @Override
    public void encode(BsonWriter writer, float[] value, EncoderContext encoderContext) {
        writer.writeStartArray();
        for (double aValue : value) {
            writer.writeDouble(aValue);
        }
        writer.writeEndArray();
    }

    @Override
    public Class<float[]> getEncoderClass() {
        return float[].class;
    }
}
