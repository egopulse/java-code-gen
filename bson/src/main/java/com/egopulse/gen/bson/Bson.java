package com.egopulse.gen.bson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a bean class or interface so {@link BsonAnnoProcessor} will generate BSON codec for it, in the case of
 * interface the processor assumes there are implementation and builder classes ready
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Bson {
}
