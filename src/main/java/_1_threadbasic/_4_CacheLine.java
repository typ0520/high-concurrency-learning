package _1_threadbasic;

import org.junit.jupiter.api.Test;

/**
 * @author tong
 */
public class _4_CacheLine {
    private static class Test1 {
        public long x = 0L;
    }

    private static class Test2 {
        public long p1, p2, p3, p4, p5, p6, p7;
        //@Contended
        public long x = 0L;
        public long p9, p10, p11, p12, p13, p14, p15;
    }

    static final Test1[] test1s = new Test1[]{new Test1(), new Test1()};
    static final Test2[] test2s = new Test2[]{new Test2(), new Test2()};

    long count = 30_0000_0000L;

    @Test
    public void test_one_cacheline() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            for (long i = 0; i < count; i++) {
                test1s[0].x = i;
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t1.setName("test thread1");
        Thread t2 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            for (long i = 0; i < count; i++) {
                test1s[1].x = i;
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t2.setName("test thread2");
        t1.start();
        t2.start();
        long start = System.currentTimeMillis();
        t1.join();
        t2.join();
        long end = System.currentTimeMillis();
        System.out.println("use: " + (end - start) + "ms");
    }

    @Test
    public void test_multiple_cacheline() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            for (long i = 0; i < count; i++) {
                test2s[0].x = i;
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t1.setName("test thread1");
        Thread t2 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            for (long i = 0; i < count; i++) {
                test2s[1].x = i;
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t2.setName("test thread2");
        t1.start();
        t2.start();
        long start = System.currentTimeMillis();
        t1.join();
        t2.join();
        long end = System.currentTimeMillis();
        System.out.println("use: " + (end - start) + "ms");
    }
}
