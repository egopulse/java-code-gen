#java-code-gen
A collection of source code generators for Java based on [Java Annotation Processor](https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/Processor.html)

#Sub projects
* [common](https://github.com/egopulse/gen/tree/master/common): utilities for writing annotation processors.
* [bean](https://github.com/egopulse/gen/tree/master/bean) implementation, builder and property name extractor class generator for getter only or getter/setter interfaces
* [bson](https://github.com/egopulse/gen/tree/master/bson) Bson Java Bean codec generator.
* [bson-runtime](https://github.com/egopulse/gen/tree/master/bson-runtime) Runtime dependency for generated Bean codec by bson-gen, including extra codecs for primitive types like short, byte ... array, enum and generic list type.
