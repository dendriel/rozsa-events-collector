package testutils;

import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import org.springframework.util.StopWatch;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class AsyncTestUtils {

    /**
     * Wait until the stub is called or timeout is reached.
     * @param count expected mock call count.
     * @param patternBuilder mock call pattern builder.
     * @param timeout timeout in milliseconds
     */
    public static void verifyAsync(final int count, final RequestPatternBuilder patternBuilder, final long timeout) throws InterruptedException {
        long elapsed = 0;
        StopWatch sw = new StopWatch();
        do {
            try {
                sw.start();
                verify(count, patternBuilder);
                break;
            } catch (Throwable e) {
                Thread.sleep(20);
                sw.stop();
                elapsed += sw.getTotalTimeMillis();
                if (elapsed >= timeout) {
                    throw e;
                }
            }
        } while(true);

        sw.stop();
        elapsed += sw.getTotalTimeMillis();
        System.out.printf("Elapsed time in verifyAsync is %s\n", elapsed);
    }
}
