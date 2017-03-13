import java.util.concurrent.CountDownLatch;

/**
 * Created by alexandr on 12.03.17.
 */
public class Thread4 extends Thread {
    private final CountDownLatch latch;
    public Thread4(CountDownLatch latch){
        this.latch = latch;
    }
    @Override
    public void run(){
        System.out.println("Thread4 thread started execution ");
        try {
            Thread.sleep(1250);
            latch.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
