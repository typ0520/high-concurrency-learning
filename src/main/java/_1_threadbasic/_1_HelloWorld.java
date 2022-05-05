package _1_threadbasic;

import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

/**
 * https://apple.stackexchange.com/questions/208762/now-that-el-capitan-is-rootless-is-there-any-way-to-get-dtrace-working
 *
 * strace -f -o ~/straceout.txt  java _1_threadbasic._1_HelloWorld
 * strace -ff -o strace.out java _1_threadbasic._1_HelloWorld
 * @author tong
 */
public class _1_HelloWorld {
    public static void main(String[] args) throws Throwable {
        new _1_HelloWorld().testLockSupport();
    }

    @Test
    public void test_create_thread() throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> future = service.submit(() -> Thread.currentThread().getName() + ": done");
        System.out.println(future.get());

        FutureTask<String> futureTask = new FutureTask<>(() -> Thread.currentThread().getName() + ": done");
        Thread thread = new Thread(futureTask);
        System.out.println(thread.getState());
        thread.start();
        System.out.println(thread.getState());
        System.out.println(futureTask.get());
    }

    @Test
    public void testLockSupport() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " start");
            LockSupport.park(10_1000);
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t1.setName("test thread1");
        t1.start();
        Thread.sleep(1_000L);
        LockSupport.unpark(t1);
        t1.join();
    }
}
