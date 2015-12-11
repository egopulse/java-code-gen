package com.egopulse.bson.codecs;

import org.bson.codecs.AtomicBooleanCodec;
import org.bson.codecs.AtomicIntegerCodec;
import org.bson.codecs.AtomicLongCodec;
import org.bson.codecs.BinaryCodec;
import org.bson.codecs.BooleanCodec;
import org.bson.codecs.ByteArrayCodec;
import org.bson.codecs.CodeCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DateCodec;
import org.bson.codecs.DoubleCodec;
import org.bson.codecs.IntegerCodec;
import org.bson.codecs.LongCodec;
import org.bson.codecs.MaxKeyCodec;
import org.bson.codecs.MinKeyCodec;
import org.bson.codecs.ObjectIdCodec;
import org.bson.codecs.PatternCodec;
import org.bson.codecs.StringCodec;
import org.bson.codecs.SymbolCodec;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtraCodecProvider implements CodecProvider {
    private final Map<Class<?>, Codec<?>> codecs = new ConcurrentHashMap<>();

    public ExtraCodecProvider() {
        addCodecs();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        // Optimistic get to avoid using dynamic memory
        Codec<?> ret = codecs.get(clazz);
        if (ret != null) {
            return (Codec<T>) ret;
        }
        if (clazz.isEnum()) {
            return (Codec<T>) codecs.computeIfAbsent(clazz, c -> new EnumCodec<>((Class<? extends Enum>) c));
        }
        if (clazz.isArray()) {
            Class<?> componentType = clazz.getComponentType();
            Codec componentCodec = get(componentType, registry);
            if (componentCodec != null) {
                return (Codec<T>) codecs.computeIfAbsent(clazz, c -> new ArrayCodec(clazz, componentCodec));
            }
        }
        return registry.get(clazz);
    }

    public <T> void addCodec(final Codec<T> codec) {
        codecs.put(codec.getEncoderClass(), codec);
    }

    private void addCodecs() {
        addCodec(new BinaryCodec());
        addCodec(new BooleanCodec());
        addCodec(new ByteCodec());
        addCodec(new ShortCodec());
        addCodec(new CharacterCodec());
        addCodec(new FloatCodec());
        addCodec(new DoubleCodec());
        addCodec(new IntegerCodec());
        addCodec(new LongCodec());
        addCodec(new MinKeyCodec());
        addCodec(new MaxKeyCodec());
        addCodec(new CodeCodec());
        addCodec(new ObjectIdCodec());
        addCodec(new StringCodec());
        addCodec(new SymbolCodec());
        addCodec(new UuidCodec());
        addCodec(new PatternCodec());
        addCodec(new ByteArrayCodec());
        addCodec(new AtomicBooleanCodec());
        addCodec(new AtomicIntegerCodec());
        addCodec(new AtomicLongCodec());

        addCodec(new DateCodec());
        addCodec(new InstantCodec());
        addCodec(new LocalDateCodec());
        addCodec(new LocalDateTimeCodec());
        addCodec(new ZonedDateTimeCodec());

        addCodec(new BooleanArrayCodec());
        addCodec(new CharArrayCodec());
        addCodec(new ShortArrayCodec());
        addCodec(new IntArrayCodec());
        addCodec(new LongArrayCodec());
        addCodec(new FloatArrayCodec());
        addCodec(new DoubleArrayCodec());
    }
}
