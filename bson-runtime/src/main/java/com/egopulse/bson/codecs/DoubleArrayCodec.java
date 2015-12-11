package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.ArrayList;
import java.util.List;

public class DoubleArrayCodec implements Codec<double[]> {
    @Override
    public double[] decode(BsonReader reader, DecoderContext decoderContext) {
        List<Double> list = new ArrayList<>();
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            list.add(reader.readDouble());
        }
        reader.readEndArray();
        int length = list.size();
        double[] ret = new double[length];
        for (int i = 0; i < length; i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    @Override
    public void encode(BsonWriter writer, double[] value, EncoderContext encoderContext) {
        writer.writeStartArray();
        for (double aValue : value) {
            writer.writeDouble(aValue);
        }
        writer.writeEndArray();
    }

    @Override
    public Class<double[]> getEncoderClass() {
        return double[].class;
    }
}
