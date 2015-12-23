package com.egopulse.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface RouteMapping {
    ContentType[] produces() default {};
    ContentType[] consumes() default {};
    HttpMethod[] method() default {HttpMethod.GET};
    String path() default "";
    String pathRegEx() default "";
}
