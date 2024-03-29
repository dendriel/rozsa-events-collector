package com.rozsa.events.collector.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to mark a method that has data to be collected.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Collect {
    /**
     * Target collection flow.
     * May be overridden if specified on parameter or field annotations.
     * WARNING: the flow defined by this annotation won't affect the @CollectReturn annotation (if specified).
     * @see CollectParameter
     * @see CollectField
     * @see CollectReturn
     */
    String flow() default "";
}
