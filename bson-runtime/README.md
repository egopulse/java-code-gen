#bson-runtime
Runtime dependent classes for generated bson codec for a Bean class/interface, including various extra codecs for different
property time which are not supported by out-of-the-box codec like

* All primitive type like `byte`, `short`, `char` ...
* `java.lang.time.*` like `Instant`, `LocalDate`, `LocalDateTime`, `ZonedDateTime` and old `java.util.Date`
* enum and array types
* `com.egopulse.bson.codecs.BeanCodec<T>` class that all generated Bean Codec will inherit from
