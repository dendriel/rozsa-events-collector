package com.rozsa.events.collector.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a parameter for collection. Must be used only on methods marked with @Collect
 * @see Collect
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CollectParameter {

    // Interchangeable with key.
    String value() default "";

    /**
     * The key referring to the value captured from this parameter.
     * If not specified, the parameter name will be used. But it is NOT RECOMMENDED due to coupling issues between the
     * variable name in the code (that can change for any reason) and the data key.
     */
    String key() default "";

    /**
     * Instead of using the own parameter as the capture value, look for a field from this parameter marked
     * with @CollectField. Will collect all fields marked for collection inside the parameter.
     * @see CollectField
     */
    boolean scanFields() default false;

    /**
     * Defines a customized collector bean to fetch data from this parameter.
     */
    String collector() default "";

    /**
     * Target collection flow.
     * Takes precedence over @Collect annotation
     * @see Collect
     */
    String flow() default "";
}
