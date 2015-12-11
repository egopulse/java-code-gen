package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ArrayCodec<T> implements Codec<T[]> {
    private final Class<T[]> clazz;
    private final Codec<T> componentCodec;

    public ArrayCodec(Class<T[]> clazz, Codec<T> componentCodec) {
        this.clazz = clazz;
        this.componentCodec = componentCodec;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] decode(BsonReader reader, DecoderContext decoderContext) {
        List<T> list = new ArrayList<>();
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            list.add(componentCodec.decode(reader, decoderContext));
        }
        reader.readEndArray();
        int length = list.size();
        return list.toArray((T[]) Array.newInstance(clazz.getComponentType(), length));
    }

    @Override
    public void encode(BsonWriter writer, T[] value, EncoderContext encoderContext) {
        writer.writeStartArray();
        for (T aVal: value) {
            componentCodec.encode(writer, aVal, encoderContext);
        }
        writer.writeEndArray();
    }

    @Override
    public Class<T[]> getEncoderClass() {
        return clazz;
    }
}
