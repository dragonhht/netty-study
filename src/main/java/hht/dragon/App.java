package hht.dragon;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Hello world!
 *
 */
public class App {

    static int[] index = {0};

    public static void main( String[] args ) throws InterruptedException {
        App app = new App();
        Lock lock = new ReentrantLock();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {

                    for(int j = 0; j < 10; j++) {
                        lock.lock();
                        index[0]++;
                        System.out.println(index[0]);
                        lock.unlock();
                    }
            }).start();
        }
    }
}
