package rozsa.events.collector.annotations;

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

    /**
     * Instead of using the field as the capture value, look for an inner-field from this field marked
     * with @CollectField.
     * @see rozsa.events.collector.annotations.CollectField
     */
    boolean scanFields() default false;
}
