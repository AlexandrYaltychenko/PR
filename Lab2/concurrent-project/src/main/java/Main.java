import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by alexandr on 12.03.17.
 */
public class Main {
    private final Object thread5Waiter = new Object();

    public void runLab(){
        final CyclicBarrier barrier = new CyclicBarrier(3, new Runnable() {
            public void run() {
                System.out.println("BARRIER!");
                synchronized (thread5Waiter){
                    thread5Waiter.notify();
                }
            }
        });
        CountDownLatch latch = new CountDownLatch(3);
        Thread1 thread1 = new Thread1(barrier);
        Thread2 thread2 = new Thread2(barrier,latch);
        Thread3 thread3 = new Thread3(barrier, latch);
        Thread4 thread4 = new Thread4(latch);
        Thread5 thread5 = new Thread5(thread5Waiter);
        Thread6 thread6 = new Thread6(latch);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
    }
    public static void main(String[] args) {
        Main main = new Main();
        main.runLab();
    }

}
