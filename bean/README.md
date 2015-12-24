#bean
Annotation processor to generate implementation, builder class and property name extractor classes from just a single
getter only Bean interface

For example, given we have

```java
@Bean(propNameExtractor = true)
public interface TestPojo {
    boolean isBoolean();
}
```

The processor will generate 3 classes as following

```java
public class TestPojoBean implements TestPojo {
    private boolean _boolean;
    public TestPojoBean(boolean _boolean) {
        this._boolean = _boolean;
    }
    @Override
    public boolean isBoolean() {
        return this._boolean;
    }
    public static TestPojoBuilder builder() {
        return new TestPojoBuilder();
    }
}
```

```java
public final class TestPojoBuilder implements Supplier<TestPojo> {
    private boolean _boolean;
    public TestPojoBuilder withBoolean(boolean _boolean) {
        this._boolean = _boolean;
        return this;
    }
    public TestPojoBean build() {
        return new com.egopulse.bson.gen.bean.TestPojoBean(this._boolean);
    }
    public TestPojoBean get() {
        return build();
    }
}
```

```java
public final class TestPojoPropNameExtractor implements TestPojo, Supplier<String> {
    private String lastName;

    public String get() {
        return this.lastName;
    }

    @Override
    public boolean isBoolean() {
        this.lastName="boolean";
        return false;
    }
}
```

The property name extractor is useful when you want to build kind of strong-typed class mapping, by default it 
will not be generated.