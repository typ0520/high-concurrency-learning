package _1_threadbasic;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;

/**
 * @author tong
 */
public class _openjdk_jol {
    private static class T {
        int m = 0;
        String str = "ss";
    }
    @Test
    public void test() {
        Object obj = new Object();
        System.out.println(ClassLayout.parseInstance(obj).toPrintable());
        obj.hashCode();
        synchronized (obj) {
            System.out.println(ClassLayout.parseInstance(obj).toPrintable());
        }
        System.out.println("-----------------------------------");
        Object[] array = new Object[]{new Object(), new Object()};
        System.out.println(ClassLayout.parseInstance(array).toPrintable());

        System.out.println("-----------------------------------");
        T t = new T();
        System.out.println(ClassLayout.parseInstance(t).toPrintable());
    }
}
