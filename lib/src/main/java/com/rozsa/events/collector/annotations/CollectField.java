package com.rozsa.events.collector.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a field for collection.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CollectField {

    @AliasFor("key")
    String value() default "";

    /**
     * The key referring to the value captured from this field.
     * If not specified, the field name will be used. But it is NOT RECOMMENDED due to coupling issues between the
     * variable name in the code (that can change for any reason) and the data key.
     */
    String key() default "";

    /**
     * Instead of using the field as the capture value, look for an inner-field marked with @CollectField.
     * WARNING: This is a recursive feature.
     * @see CollectField
     */
    boolean scanFields() default false;
}
