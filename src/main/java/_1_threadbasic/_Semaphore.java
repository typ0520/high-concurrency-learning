package _1_threadbasic;

import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntConsumer;

/**
 * @author tong
 */
public class _Semaphore {
    /**
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/print-zero-even-odd
     */
    @Test
    public void test1() throws InterruptedException {
        ZeroEvenOdd zeroEvenOdd = new ZeroEvenOdd(4);

        AtomicBoolean done = new AtomicBoolean();
        IntConsumer intConsumer = new IntConsumer() {
            @Override
            public void accept(int value) {
                System.out.print(value);
                if (value >= zeroEvenOdd.n) {
                    System.out.println();
                    done.set(true);
                }
            }
        };
        Thread t1 = new Thread(() -> {
            try {
                zeroEvenOdd.zero(intConsumer);
            } catch (InterruptedException e) {
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                zeroEvenOdd.odd(intConsumer);
            } catch (InterruptedException e) {
            }
        });
        Thread t3 = new Thread(() -> {
            try {
                zeroEvenOdd.even(intConsumer);
            } catch (InterruptedException e) {
            }
        });
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
    }

    private static class ZeroEvenOdd {
        private int n;
        private Semaphore zeroSemaphore = new Semaphore(1);
        private Semaphore evenSemaphore = new Semaphore(0);
        private Semaphore oddSemaphore = new Semaphore(0);

        public ZeroEvenOdd(int n) {
            this.n = n;
        }

        // printNumber.accept(x) outputs "x", where x is an integer.
        public void zero(IntConsumer printNumber) throws InterruptedException {
            for (int i = 0; i < n; i++) {
                zeroSemaphore.acquire();
                printNumber.accept(0);
                if ((i + 1) % 2 == 0) {
                    evenSemaphore.release();
                } else {
                    oddSemaphore.release();
                }
            }
        }

        public void even(IntConsumer printNumber) throws InterruptedException {
            for (int i = 2; i <= n; i += 2) {
                evenSemaphore.acquire();
                printNumber.accept(i);
                zeroSemaphore.release();
            }
        }

        public void odd(IntConsumer printNumber) throws InterruptedException {
            for (int i = 1; i <= n; i += 2) {
                oddSemaphore.acquire();
                printNumber.accept(i);
                zeroSemaphore.release();
            }
        }
    }

    /**
     * https://segmentfault.com/a/1190000038971320#:~:text=%E4%BF%A1%E5%8F%B7%E9%87%8F%EF%BC%88Semaphore%EF%BC%89%E6%98%AFJava,%E8%B5%84%E6%BA%90%E6%97%B6%E9%83%BD%E4%BC%9A%E9%87%8A%E6%94%BE%E8%AE%B8%E5%8F%AF%E3%80%82
     * @throws InterruptedException
     */
    @Test
    public void test2() throws InterruptedException {
        Semaphore semaphore = new Semaphore(5);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + ": running");
                    Thread.sleep(5000L);
                    semaphore.release();
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.setName("thread " + i);
            thread.start();
        }
        countDownLatch.await();
    }
}
