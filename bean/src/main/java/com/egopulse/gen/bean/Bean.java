package com.egopulse.gen.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark an interface that {@link BeanAnnoProcessor} should generate implementation and builder class for it
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Bean {
    /**
     * Generate the optional property name extractor or not
     * @return false by default
     */
    boolean propNameExtractor() default false;
}
