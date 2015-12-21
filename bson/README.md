#bson
Annotation processor to generate codec for a Bean class or an interface

For example, given we have

```java
@Bean
@Bson
public interface TestPojo {
    boolean isBoolean();
    Set<String> getSetString();
}
```

The processor will generate the codec class as

```java
package com.egopulse.bson;

import com.egopulse.bson.codecs.BeanCodec;

import java.util.Set;

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
        writer.writeName("setString");
        Set<String> _setString = pojo.getSetString();
        if (_setString == null) {
            writer.writeNull();
        } else {
            this.encodeGenericSet(writer, _setString, ctx.getChildContext(), java.lang.String.class);
        }
        writer.writeEndElement();
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
                case "setString":
                    if (reader.readBsonType() != org.bson.BsonType.NULL) {
                        builder.withSetString(this.decodeGenericSet(reader, ctx, java.lang.String.class));
                    } else {
                        reader.readNull();
                        builder.withSetString(null);
                    }
                    break;
            }
        }
        reader.readEndDocument();
        return builder.get();
    }
}
```

Note that the `TestPojo` class also need to be annotated with `@Bean` so the `TestPojoBuilder` class also is generated 
and used in the codec 

Currently support following property's types 
* Any primitive type
* Array
* enum (store as string)
* Following generic collection types: `List<T>`, `Set<T>`, `Map<String, T>`, `Map<Long, T>` however not supported `List`, `Set`, 
`Map`
* date/time related types: `java.time.Instant`, `java.time.LocalDate`, `java.time.LocalDateTime`, `java.time.ZonedDateTime`, `java.util.Date`

The generated codecs will be also registered into META-INF/services so they will be discovered and registered automatically 
into the `BeanCodec.DEFAULT_REGISTRY`