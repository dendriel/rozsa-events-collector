package mocks;

import com.rozsa.events.collector.annotations.CollectParameter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JoinPointMockFactory {

    public enum JoinPointMockTypes {

        SINGLE_COLLECT_PARAMETER("singleCollectParameterAnnotation", List.of(String.class)),
        MISSING_PARAMETERS("missingParameters", List.of()),
        MISSING_COLLECT_PARAMETER("missingCollectParameterAnnotation",  List.of(String.class, Integer.class, Object.class)),
        ;

        private final String method;
        private final List<Class> parameters;

        JoinPointMockTypes(String method, List<Class> parameters) {
            this.method = method;
            this.parameters = parameters;
        }
    }

    public static JoinPoint mockJoinPoint(final JoinPointMockTypes mockType) throws NoSuchMethodException {
        return mockJoinPoint(mockType, List.of());
    }

    public static JoinPoint mockJoinPoint(final JoinPointMockTypes mockType, final List<Object> args) throws NoSuchMethodException {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(args.toArray());

        MethodSignature methodSignature = mock(MethodSignature.class);
        Method method = JoinPointMockFactory.class.getDeclaredMethod(mockType.method, mockType.parameters.toArray(new Class[0]));

        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(methodSignature);

        return joinPoint;
    }

    public static void singleCollectParameterAnnotation(@CollectParameter("abc") final String parameterName) {}

    public static void missingParameters() {}

    public static void missingCollectParameterAnnotation(String foo, Integer bar, Object xpto) {}

}
