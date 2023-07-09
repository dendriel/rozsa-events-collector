package com.rozsa.events.collector.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method with this annotation to begin collecting data from events. The method marked with this annotation
 * should be the entry point of the current flow (eg.: an endpoint).
 * When the code execution leaves the method marked with this annotation, the collected that will be automatically sent
 * to the remote server. The @FinishColleting annotation may be used to interrupt the collection before getting back
 * to the initial method.
 *
 * @see Collect
 * @see FinishCollecting
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeginCollecting {

    /**
     * Should submit the event even if an exception is throw?
     * true - submit on error (default);
     * false - do not submit on error.
     */
    boolean submitOnError() default true;

    /**
     * Target collection flow.
     */
    String flow() default "";
}
