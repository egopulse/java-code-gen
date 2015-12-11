package com.egopulse.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.junit.Assert;

class TestBeanCodec extends BeanCodec<TestBean> {

    @Override
    public TestBean decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        TestBean pojo = new TestBean();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            switch (reader.readName()) {
                case "stringField":
                    pojo.setStringField(reader.getCurrentBsonType() != BsonType.NULL ? reader.readString() : null);
                    break;
                case "longField":
                    pojo.setLongField(reader.readInt64());
                    break;
                case "listStringField":
                    pojo.setListStringField(this.decodeGenericList(reader, decoderContext, String.class));
                    break;
                case "setStringField":
                    pojo.setSetStringField(this.decodeGenericSet(reader, decoderContext, String.class));
                    break;
                default:
                    Assert.fail("Unexpected name " + reader.getCurrentName());
            }
        }
        reader.readEndDocument();
        return pojo;
    }

    @Override
    public void encode(BsonWriter writer, TestBean value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("stringField", value.getStringField());
        writer.writeInt64("longField", value.getLongField());
        writer.writeName("setStringField");
        this.encodeGenericSet(writer, value.getSetStringField(), encoderContext.getChildContext(), String.class);
        writer.writeName("listStringField");
        this.encodeGenericList(writer, value.getListStringField(), encoderContext.getChildContext(), String.class);
        writer.writeEndDocument();
    }

    @Override
    public Class<TestBean> getEncoderClass() {
        return TestBean.class;
    }
}
