package com.rozsa.events.collector.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CollectReturn {

    // Interchangeable with key.
    String value() default "";

    /**
     * The key referring to the captured value.
     * If not specified, the object type name will be used. But it is NOT RECOMMENDED due to coupling issues between the
     * object type used in the code (that can change for any reason) and the data key.
     * If returned value is null, collection won't be made.
     */
    String key() default "";

    /**
     * Instead of using the own return as the capture value, look for a field from this object marked
     * with @CollectField. Will collect all fields marked for collection inside the parameter.
     * @see CollectField
     */
    boolean scanFields() default false;

    /**
     * Target collection flow.
     * Must be specified if it is necessary to collect for a custom flow.
     * WARNING: it won't use the flow specified in Collect annotation (if any)!
     * @see Collect
     */
    String flow() default "";
}
