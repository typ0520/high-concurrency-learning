package _1_threadbasic;

import org.junit.jupiter.api.Test;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author tong
 */
public class _3_Interrupt {
    @Test
    public void interrupt_isInterrupted() throws InterruptedException {
        Thread t = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            for (;;) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + " is interrupted");
                    System.out.println(Thread.currentThread().getName() + " Thread.currentThread().isInterrupted(): " + Thread.currentThread().isInterrupted());//true 不会重置标志位
                    break;
                }
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t.setName("test thread");
        t.start();
        Thread.sleep(3000L);
        System.out.println("invoke t.interrupt");
        t.interrupt();
        System.out.println(Thread.currentThread().getName() + " end");
    }

    @Test
    public void interrupt_Thread_interrupted() throws InterruptedException {
        Thread t = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            for (;;) {
                if (Thread.interrupted()) {
                    System.out.println(Thread.currentThread().getName() + " is interrupted");
                    System.out.println(Thread.currentThread().getName() + " Thread.interrupted(): " + Thread.interrupted());//false 会重置标志位
                    break;
                }
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t.setName("test thread");
        t.start();
        Thread.sleep(3000L);
        System.out.println("invoke t.interrupt");
        t.interrupt();
        System.out.println(Thread.currentThread().getName() + " end");
    }

    //可以打断
    @Test
    public void test_interrupt_sleep() throws InterruptedException {
        Thread t = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            try {
                Thread.sleep(10_000L);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " is interrupted");
                System.out.println("Thread.currentThread().isInterrupted(): " + Thread.currentThread().isInterrupted());
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t.setName("test thread");
        t.start();
        Thread.sleep(3000L);
        t.interrupt();
        System.out.println(Thread.currentThread().getName() + " end");
    }

    //可以打断
    @Test
    public void test_interrupt_wait() throws InterruptedException {
        Object lock = new Object();
        Thread t = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " is interrupted");
                    System.out.println("Thread.currentThread().isInterrupted(): " + Thread.currentThread().isInterrupted());
                }
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t.setName("test thread");
        t.start();
        Thread.sleep(2000L);
        t.interrupt();
    }

    //无法打断
    @Test
    public void test_interrupt_synchronized() throws InterruptedException {
        Object lock = new Object();
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            synchronized (lock) {
                try {
                    Thread.sleep(10_000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t1.setName("test thread1");
        Thread t2 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            //无法打断正在竞争锁的线程
            synchronized(lock) {
                System.out.println(Thread.currentThread().getName() + " get lock");
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t2.setName("test thread2");

        t1.start();
        Thread.sleep(1000L);
        t2.start();
        Thread.sleep(1000L);
        t2.interrupt();
        t1.join();
        t2.join();
        System.out.println(Thread.currentThread().getName() + " end");
    }

    //无法打断
    @Test
    public void test_interrupt_ReentrantLock_lock() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            try {
                lock.lock();
                try {
                    Thread.sleep(10_000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } finally {
                lock.unlock();
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t1.setName("test thread1");
        Thread t2 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            try {
                lock.lock();//等待锁过程中，不会理会外部的interrupt信号
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t2.setName("test thread2");

        t1.start();
        Thread.sleep(1000L);
        t2.start();
        Thread.sleep(1000L);
        t2.interrupt();
        t1.join();
        t2.join();
        System.out.println(Thread.currentThread().getName() + " end");
    }

    //可以打断
    @Test
    public void test_interrupt_ReentrantLock_lockInterruptibly() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            try {
                lock.lock();
                try {
                    Thread.sleep(10_000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } finally {
                lock.unlock();
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t1.setName("test thread1");
        Thread t2 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            try {
                lock.lockInterruptibly();//等待锁过程中，外部有interrupt信号时抛异常
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " is interrupted");
                System.out.println("Thread.currentThread().isInterrupted(): " + Thread.currentThread().isInterrupted());
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t2.setName("test thread2");

        t1.start();
        Thread.sleep(1000L);
        t2.start();
        Thread.sleep(1000L);
        t2.interrupt();
//        t1.join();
//        t2.join();
        System.out.println(Thread.currentThread().getName() + " end");
    }
}
