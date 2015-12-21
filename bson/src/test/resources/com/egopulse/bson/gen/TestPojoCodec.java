package com.egopulse.bson.gen;

import com.egopulse.bson.codecs.BeanCodec;

import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class TestPojoCodec extends BeanCodec<TestPojo> {
    @Override
    public Class<TestPojo> getEncoderClass() {
        return TestPojo.class;
    }

    @Override
    public void encode(final BsonWriter writer, final TestPojo pojo, final EncoderContext ctx) {
        writer.writeStartDocument();
        writer.writeName("boolean");
        writer.writeBoolean(pojo.isBoolean());
        writer.writeName("byte");
        writer.writeInt32(pojo.getByte());
        writer.writeName("short");
        writer.writeInt32(pojo.getShort());
        writer.writeName("int");
        writer.writeInt32(pojo.getInt());
        writer.writeName("long");
        writer.writeInt64(pojo.getLong());
        writer.writeName("float");
        writer.writeDouble(pojo.getFloat());
        writer.writeName("double");
        writer.writeDouble(pojo.getDouble());
        writer.writeName("string");
        String _string = pojo.getString();
        if (_string == null) {
            writer.writeNull();
        } else {
            writer.writeString(_string);
        }
        writer.writeName("listString");
        List<String> _listString = pojo.getListString();
        if (_listString == null) {
            writer.writeNull();
        } else {
            this.encodeGenericList(writer, _listString, ctx.getChildContext(), java.lang.String.class);
        }
        writer.writeName("setString");
        Set<String> _setString = pojo.getSetString();
        if (_setString == null) {
            writer.writeNull();
        } else {
            this.encodeGenericSet(writer, _setString, ctx.getChildContext(), java.lang.String.class);
        }
        writer.writeName("mapStringString");
        Map<String, String> _mapStringString = pojo.getMapStringString();
        if (_mapStringString == null) {
            writer.writeNull();
        } else {
            this.encodeGenericStringMap(writer, _mapStringString, ctx.getChildContext(), java.lang.String.class);
        }
        writer.writeEndDocument();
    }

    @Override
    public TestPojo decode(final BsonReader reader, final DecoderContext ctx) {
        TestPojoBuilder builder = new TestPojoBuilder();
        reader.readStartDocument();
        while (reader.readBsonType() != org.bson.BsonType.END_OF_DOCUMENT) {
            switch (reader.readName()) {
                case "boolean":
                    builder.withBoolean(reader.readBoolean());
                    break;
                case "byte":
                    builder.withByte((byte) reader.readInt32());
                    break;
                case "short":
                    builder.withShort((short) reader.readInt32());
                    break;
                case "int":
                    builder.withInt(reader.readInt32());
                    break;
                case "long":
                    builder.withLong(reader.readInt64());
                    break;
                case "float":
                    builder.withFloat((float) reader.readDouble());
                    break;
                case "double":
                    builder.withDouble(reader.readDouble());
                    break;
                case "string":
                    if (reader.readBsonType() != org.bson.BsonType.NULL) {
                        builder.withString(reader.readString());
                    } else {
                        reader.readNull();
                        builder.withString(null);
                    }
                    break;
                case "listString":
                    if (reader.readBsonType() != org.bson.BsonType.NULL) {
                        builder.withListString(this.decodeGenericList(reader, ctx, java.lang.String.class));
                    } else {
                        reader.readNull();
                        builder.withListString(null);
                    }
                    break;
                case "setString":
                    if (reader.readBsonType() != org.bson.BsonType.NULL) {
                        builder.withSetString(this.decodeGenericSet(reader, ctx, java.lang.String.class));
                    } else {
                        reader.readNull();
                        builder.withSetString(null);
                    }
                    break;
                case "mapStringString":
                    if (reader.readBsonType() != org.bson.BsonType.NULL) {
                        builder.withMapStringString(this.decodeGenericStringMap(reader, ctx, java.lang.String.class));
                    } else {
                        reader.readNull();
                        builder.withMapStringString(null);
                    }
                    break;
            }
        }
        reader.readEndDocument();
        return builder.build();
    }
}