package rozsa.events.collector.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * (OPTIONAL) Mark a method with this annotation to finish collecting data from events and submit then to the remote
 * server.
 * Can be used if it is necessary to stop data collecting before getting back to the method marked
 * with @BeginCollecting.
 * @see rozsa.events.collector.annotations.BeginCollecting
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FinishCollecting {
}
