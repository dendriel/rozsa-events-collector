package com.rozsa.events.collector.aspects.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationUtils {

    public static <T extends Annotation> T getAnnotationFrom(final Class<T> clazz, final JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        if (signature == null) {
            return null;
        }

        Method method = signature.getMethod();
        return method.getAnnotation(clazz);
    }
}
