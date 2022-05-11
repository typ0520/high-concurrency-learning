package _4_threadpool;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author tong
 */
public class _ExecutorService {
    @Test
    public void test_invokeAll() throws InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        List<Callable<String>> callables = new ArrayList<>();
        callables.add(() -> {
            throw new RuntimeException("test exception");
        });
        callables.add(() -> "success");
        callables.add(() -> {
            TimeUnit.SECONDS.sleep(8L);
            return "success sleep";
        });
        List<Future<String>> futures = executorService.invokeAll(callables, 2, TimeUnit.SECONDS);
        for (Future<String> future : futures) {
            try {
                String result = future.get();
                System.out.println(result);
            } catch (Throwable e) {
                if (e instanceof CancellationException) {
                    System.out.println("exec timeout: " + e.getMessage());
                } else {
                    System.out.println("exec error: " + e.getMessage());
                }
            }
//            catch (TimeoutException e) {
//                System.out.println("exec timeout: " + e.getMessage());
//            }
        }
    }
}
