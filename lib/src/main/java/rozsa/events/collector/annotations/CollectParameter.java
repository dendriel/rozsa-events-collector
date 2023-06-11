package rozsa.events.collector.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a parameter for collection. Must be used only on methods marked with @Collect
 * @see rozsa.events.collector.annotations.Collect
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CollectParameter {

    /**
     * The key referring to the value captured from this parameter.
     */
    String key();

    /**
     * Instead of using the own parameter as the capture value, looker for a field from this parameter marked
     * with @CollectField.
     * @see rozsa.events.collector.annotations.CollectField
     */
    boolean scanFields() default false;
}
