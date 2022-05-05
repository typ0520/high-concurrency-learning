package _1_threadbasic;

import org.junit.jupiter.api.Test;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author tong
 */
public class _2_ThreadState {
    @Test
    public void test_NEW_RUNNABLE_TERMINATED() throws InterruptedException {
        Thread t = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " state: " + Thread.currentThread().getState());
        });
        t.setName("test thread");
        System.out.println(t.getName() + " state: " + t.getState());
        t.start();
        t.join();
        System.out.println(t.getName() + " state: " + t.getState());
    }

    @Test
    public void test_BLOCKED() throws InterruptedException {
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
            System.out.println(Thread.currentThread().getName() + " state: " + Thread.currentThread().getState());
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
        System.out.println(t2.getName() + " state: " + t2.getState());
        t1.join();
        t2.join();
    }

    @Test
    public void test_WAITING_park() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " state: " + Thread.currentThread().getState() + " t_1");
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t1.setName("test thread1");
        t1.start();
        Thread.sleep(1_000L);
        System.out.println(t1.getName() + " state: " + t1.getState() + " m_1");
        LockSupport.unpark(t1);
        Thread.sleep(1_000L);
        System.out.println(t1.getName() + " state: " + t1.getState() + " m_2");
        t1.join();
    }

    //ReentrantLock底层调用了LockSupport.park()，因为本质还是因为LockSupport.park()会是线程进入WAINING，
    @Test
    public void test_WAITING_ReentrantLock() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " running");
            try {
                lock.lock();
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
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
            System.out.println(Thread.currentThread().getName() + " state: " + Thread.currentThread().getState());
            try {
                lock.lock();
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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
        System.out.println(t2.getName() + " state: " + t2.getState());
        t1.join();
        t2.join();
    }

    @Test
    public void test_WAITING_wait() throws InterruptedException {
        Object lock = new Object();
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " state: " + Thread.currentThread().getState() + " t_1");
            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t1.setName("test thread1");
        t1.start();
        Thread.sleep(1_000L);
        System.out.println(t1.getName() + " state: " + t1.getState() + " m_1");
        synchronized (lock) {
            //lock.notify();
            lock.notifyAll();//or lock.notify();
        }
        Thread.sleep(1_000L);
        System.out.println(t1.getName() + " state: " + t1.getState() + " m_2");
        t1.join();
    }

    @Test
    public void test_TIMED_WAITING_sleep() throws InterruptedException {
        Thread t = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " state: " + Thread.currentThread().getState());
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t.setName("test thread");
        System.out.println(t.getName() + " state: " + t.getState());
        t.start();
        Thread.sleep(2_000);
        System.out.println(t.getName() + " state: " + t.getState());
        t.join();
        System.out.println(t.getName() + " state: " + t.getState());
    }

    @Test
    public void test_TIMED_WAITING_park() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " state: " + Thread.currentThread().getState() + " t_1");
            LockSupport.park(10_1000);
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t1.setName("test thread1");
        t1.start();
        Thread.sleep(1_000L);
        System.out.println(t1.getName() + " state: " + t1.getState() + " m_1");
        LockSupport.unpark(t1);
        Thread.sleep(1_000L);
        System.out.println(t1.getName() + " state: " + t1.getState() + " m_2");
        t1.join();
    }

    @Test
    public void test_TIMED_WAITING_wait() throws InterruptedException {
        Object lock = new Object();
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " state: " + Thread.currentThread().getState() + " t_1");
            try {
                synchronized (lock) {
                    lock.wait(5_000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        t1.setName("test thread1");
        t1.start();
        Thread.sleep(1_000L);
        System.out.println(t1.getName() + " state: " + t1.getState() + " m_1");
        synchronized (lock) {
            //lock.notify();
            lock.notifyAll();//or lock.notify();
        }
        Thread.sleep(1_000L);
        System.out.println(t1.getName() + " state: " + t1.getState() + " m_2");
        t1.join();
    }
}
