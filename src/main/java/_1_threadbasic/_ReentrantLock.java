package _1_threadbasic;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

/**
 * @author tong
 */
public class _ReentrantLock {
    @Test
    public void test1() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(() -> {
            try {
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " running");
                    Thread.sleep(5_000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } finally {
                lock.unlock();
            }
        });
        t1.setName("test thread1");
        Thread t2 = new Thread(() -> {
            try {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + " running");
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        });
        t2.setName("test thread2");
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t2.start();
        t1.join();
        t2.join();
    }

    /**
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/print-zero-even-odd
     */
    @Test
    public void test2() throws InterruptedException {
        ZeroEvenOdd zeroEvenOdd = new ZeroEvenOdd(4);

        IntConsumer intConsumer = new IntConsumer() {
            @Override
            public void accept(int value) {
                System.out.print(value);
                if (value >= zeroEvenOdd.n) {
                    System.out.println();
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
        public int n;
        ReentrantLock lock = new ReentrantLock();
        Condition zeroCondition = lock.newCondition();
        Condition oddCondition = lock.newCondition();
        Condition evenCondition = lock.newCondition();
        volatile int flag = 0;

        public ZeroEvenOdd(int n) {
            this.n = n;
        }

        // printNumber.accept(x) outputs "x", where x is an integer.
        public void zero(IntConsumer printNumber) throws InterruptedException {
            try {
                lock.lock();
                for (int i = 0; i < n; i++) {
                    if (flag != 0) {
                        zeroCondition.await();
                    }
                    printNumber.accept(0);
                    if ((i + 1) % 2 == 0) {
                        flag = 1;
                        evenCondition.signal();
                    } else {
                        flag = 2;
                        oddCondition.signal();
                    }
                }

            } finally {
                lock.unlock();
            }
        }

        public void even(IntConsumer printNumber) throws InterruptedException {
            try {
                lock.lock();
                for (int i = 2; i <= n; i += 2) {
                    if (flag != 1) {
                        evenCondition.await();
                    }
                    printNumber.accept(i);
                    flag = 0;
                    zeroCondition.signal();
                }
            } finally {
                lock.unlock();
            }
        }

        public void odd(IntConsumer printNumber) throws InterruptedException {
            try {
                lock.lock();
                for (int i = 1; i <= n; i += 2) {
                    if (flag != 2) {
                        oddCondition.await();
                    }
                    printNumber.accept(i);
                    flag = 0;
                    zeroCondition.signal();
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
