package _1_threadbasic;

import java.util.concurrent.*;

/**
 * https://apple.stackexchange.com/questions/208762/now-that-el-capitan-is-rootless-is-there-any-way-to-get-dtrace-working
 *
 * strace -f -o ~/straceout.txt  java _1_threadbasic.HelloWorld
 * strace -ff -o strace.out java _1_threadbasic.HelloWorld
 * @author tong
 */
public class _1_HelloWorld {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
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
}
