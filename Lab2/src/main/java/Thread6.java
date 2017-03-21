import java.util.concurrent.CountDownLatch;

/**
 * Created by alexandr on 12.03.17.
 */
public class Thread6 extends Thread {
    private final CountDownLatch latch;
    public Thread6(CountDownLatch latch){
        this.latch = latch;
    }
    @Override
    public void run(){
        System.out.println("Thread6 thread started execution ");
        try {
            System.out.println("Thread6 waits");
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread6 continued its execution ");

    }
}
