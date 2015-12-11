package com.egopulse.bson.codecs;

import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.io.BasicOutputBuffer;
import org.bson.io.ByteBufferBsonInput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TestCodecs {
    private BasicOutputBuffer buffer;
    private BsonWriter writer;
    private BsonReader reader;

    @Before
    public void before() {
        buffer = new BasicOutputBuffer();
        writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeName("test");
    }

    @Test
    public void testBooleanArrayCodec() {
        BooleanArrayCodec codec = new BooleanArrayCodec();
        boolean[] val = {true, false, true, false};
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertArrayEquals("boolean[]", val, codec.decode(reader, DecoderContext.builder().build()));
    }

    @Test
    public void testByteCodec() {
        ByteCodec codec = new ByteCodec();
        final byte val = 33;
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertEquals("byte", val, (Object) codec.decode(reader, DecoderContext.builder().build()));
    }

    @Test
    public void testCharacterCodec() {
        CharacterCodec codec = new CharacterCodec();
        final char val = '\3';
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertEquals("char", val, (Object) codec.decode(reader, DecoderContext.builder().build()));
    }

    @Test
    public void testCharArrayCodec() {
        CharArrayCodec codec = new CharArrayCodec();
        final char[] val = "Test String".toCharArray();
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertArrayEquals("char[]", val, codec.decode(reader, DecoderContext.builder().build()));
    }

    @Test
    public void testDoubleArrayCodec() {
        DoubleArrayCodec codec = new DoubleArrayCodec();
        final double[] val = {1.0, 3.5, 6.7};
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertArrayEquals("double[]", val, codec.decode(reader, DecoderContext.builder().build()), 0.0);
    }

    @Test
    public void testEnumCodec() {
        EnumCodec<TestEnum> codec = new EnumCodec<>(TestEnum.class);
        final TestEnum val = TestEnum.BBB;
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertEquals("enum", val, codec.decode(reader, DecoderContext.builder().build()));
    }

    private void prepareReader() {
        writer.writeEndDocument();
        reader = new BsonBinaryReader(new ByteBufferBsonInput(buffer.getByteBuffers().get(0)));
        reader.readStartDocument();
        reader.readName();
    }

    @Test
    public void testFloatArrayCodec() {
        FloatArrayCodec codec = new FloatArrayCodec();
        final float[] val = {1.0f, 3.5f, 6.7f};
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertArrayEquals("float[]", val, codec.decode(reader, DecoderContext.builder().build()), 0.0f);
    }

    @Test
    public void testFloatCodec() {
        FloatCodec codec = new FloatCodec();
        final float val = 3.5f;
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertEquals("float", val, codec.decode(reader, DecoderContext.builder().build()), 0.0f);
    }

    @Test
    public void testInstantCodec() {
        InstantCodec codec = new InstantCodec();
        final Instant val = Instant.now();
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertEquals("java.time.Instant", val, codec.decode(reader, DecoderContext.builder().build()));
    }

    @Test
    public void testIntArrayCodec() {
        IntArrayCodec codec = new IntArrayCodec();
        final int[] val = {1, 3, 6};
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertArrayEquals("int[]", val, codec.decode(reader, DecoderContext.builder().build()));
    }

    @Test
    public void testLocalDateCodec() {
        LocalDateCodec codec = new LocalDateCodec();
        final LocalDate val = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate();
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertEquals("java.time.LocalDate", val, codec.decode(reader, DecoderContext.builder().build()));
    }


    @Test
    public void testLocalDateTimeCodec() {
        LocalDateTimeCodec codec = new LocalDateTimeCodec();
        final LocalDateTime val = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime();
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertEquals("java.time.LocalDateTime", val, codec.decode(reader, DecoderContext.builder().build()));
    }

    @Test
    public void testLongArrayCodec() {
        LongArrayCodec codec = new LongArrayCodec();
        final long[] val = {1L, 3L, 6L};
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertArrayEquals("long[]", val, codec.decode(reader, DecoderContext.builder().build()));
    }

    @Test
    public void testShortArrayCodec() {
        ShortArrayCodec codec = new ShortArrayCodec();
        final short[] val = {1, 3, 6};
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertArrayEquals("short[]", val, codec.decode(reader, DecoderContext.builder().build()));
    }

    @Test
    public void testShortCodec() {
        ShortCodec codec = new ShortCodec();
        final short val = 1;
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertEquals("short", val, (Object) codec.decode(reader, DecoderContext.builder().build()));
    }

    @Test
    public void testZonedDateTimeCodec() {
        ZonedDateTimeCodec codec = new ZonedDateTimeCodec();
        final ZonedDateTime val = ZonedDateTime.ofInstant(Instant.now(),ZoneId.systemDefault());
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertEquals("java.time.ZonedDateTime", val.toInstant(), codec.decode(reader, DecoderContext.builder().build()).toInstant());
    }

    @Test
    public void testPojoCodec() {
        TestBeanCodec codec = new TestBeanCodec();
        final TestBean val = new TestBean();
        val.setLongField(222);
        val.setStringField("aaa");
        val.setListStringField(Collections.singletonList("dddd"));
        Set<String> set = new HashSet<>();
        set.add("eee");
        val.setSetStringField(set);
        codec.encode(writer, val, EncoderContext.builder().build());
        prepareReader();
        Assert.assertEquals("pojo", val, codec.decode(reader, DecoderContext.builder().build()));
    }

    @SuppressWarnings("UnusedDeclaration")
    private enum TestEnum {
        AAA,
        BBB,
        CCC
    }

}
