package hht.dragon;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Hello world!
 *
 */
public class App {

    int index = 0;

    public static void main( String[] args ) throws InterruptedException {
        Lock lock = new ReentrantLock();
        App app = new App();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                    app.add();
            }).start();
        }
        System.out.println(app.index);
    }

    public synchronized void add() {
        synchronized (this) {
            index++;
            System.out.println(index);
        }

    }
}
