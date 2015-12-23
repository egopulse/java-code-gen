package com.egopulse.bson.codecs;

import org.bson.BSONException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

public abstract class BeanCodec<T> implements Codec<T> {
    public static final ExtraCodecProvider EXTRA_CODEC_PROVIDER = new ExtraCodecProvider();
    private static final CodecRegistry DEFAULT_REGISTRY = fromProviders(asList(
            EXTRA_CODEC_PROVIDER,
            new BsonValueCodecProvider()));

    static {
        // Load all BeanCodec declared in META-INF/services
        ServiceLoader<BeanCodec> loader = ServiceLoader.load(BeanCodec.class);
        for (BeanCodec beanCodec : loader) {
            EXTRA_CODEC_PROVIDER.addCodec(beanCodec);
        }
    }

    private final CodecRegistry reg;

    protected BeanCodec() {
        this(DEFAULT_REGISTRY);
    }

    protected BeanCodec(CodecRegistry reg) {
        this.reg = reg;
    }

    protected <V> void encodeObject(final BsonWriter writer, V value, final EncoderContext ctx, Class<V> objType) {
        Codec<V> codec = reg.get(objType);
        if (codec == null) {
            throw new BSONException("No codec for class " + objType);
        }
        codec.encode(writer, value, ctx);
    }

    protected <V> V decodeObject(final BsonReader reader, final DecoderContext ctx, Class<V> objType) {
        Codec<V> codec = reg.get(objType);
        if (codec == null) {
            throw new BSONException("No codec for class " + objType);
        }
        return codec.decode(reader, ctx);
    }

    protected <I> void encodeGenericList(final BsonWriter writer, List<I> value, final EncoderContext ctx, Class<I> itemType) {
        Codec<I> codec = reg.get(itemType);
        writer.writeStartArray();
        for (I item : value) {
            codec.encode(writer, item, ctx);
        }
        writer.writeEndArray();
    }

    protected <I> List<I> decodeGenericList(final BsonReader reader, final DecoderContext ctx, Class<I> itemType) {
        Codec<I> codec = reg.get(itemType);
        if (codec == null) {
            throw new BSONException("No codec for class " + itemType);
        }
        List<I> list = new ArrayList<>();
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            list.add(codec.decode(reader, ctx));
        }
        reader.readEndArray();
        return list;
    }

    protected <I> void encodeGenericSet(final BsonWriter writer, Set<I> value, final EncoderContext ctx, Class<I> itemType) {
        Codec<I> codec = reg.get(itemType);
        writer.writeStartArray();
        for (I item : value) {
            codec.encode(writer, item, ctx);
        }
        writer.writeEndArray();
    }

    protected <I> Set<I> decodeGenericSet(final BsonReader reader, final DecoderContext ctx, Class<I> itemType) {
        Codec<I> codec = reg.get(itemType);
        if (codec == null) {
            throw new BSONException("No codec for class " + itemType);
        }
        Set<I> set = new HashSet<>();
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            set.add(codec.decode(reader, ctx));
        }
        reader.readEndArray();
        return set;
    }

    protected <I> void encodeGenericStringMap(final BsonWriter writer, Map<String, I> value, final EncoderContext ctx, Class<I> itemType) {
        Codec<I> codec = reg.get(itemType);
        writer.writeStartDocument();
        for (Map.Entry<String, I> entry : value.entrySet()) {
            writer.writeName(entry.getKey());
            codec.encode(writer, entry.getValue(), ctx);
        }
        writer.writeEndDocument();
    }

    protected <I> Map<String, I> decodeGenericStringMap(final BsonReader reader, final DecoderContext ctx, Class<I> itemType) {
        Codec<I> codec = reg.get(itemType);
        if (codec == null) {
            throw new BSONException("No codec for class " + itemType);
        }
        Map<String, I> map = new HashMap<>();
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            map.put(reader.readName(), codec.decode(reader, ctx));
        }
        reader.readEndDocument();
        return map;
    }

    protected <I> void encodeGenericLongMap(final BsonWriter writer, Map<Long, I> value, final EncoderContext ctx, Class<I> itemType) {
        Codec<I> codec = reg.get(itemType);
        writer.writeStartArray();
        for (Map.Entry<Long, I> entry : value.entrySet()) {
            writer.writeInt64(entry.getKey());
            codec.encode(writer, entry.getValue(), ctx);
        }
        writer.writeEndArray();
    }

    protected <I> Map<Long, I> decodeGenericLongMap(final BsonReader reader, final DecoderContext ctx, Class<I> itemType) {
        Codec<I> codec = reg.get(itemType);
        if (codec == null) {
            throw new BSONException("No codec for class " + itemType);
        }
        Map<Long, I> map = new HashMap<>();
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            map.put(reader.readInt64(), codec.decode(reader, ctx));
        }
        reader.readEndArray();
        return map;
    }
}
