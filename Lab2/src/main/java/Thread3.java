import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by alexandr on 12.03.17.
 */
public class Thread3 extends Thread {
    private final CyclicBarrier barrier;
    private final CountDownLatch latch;
    public Thread3(CyclicBarrier barrier, CountDownLatch latch){
        this.barrier = barrier;
        this.latch = latch;
    }
    @Override
    public void run(){
        System.out.println("Thread3 thread started execution ");
        try {
            Thread.sleep(400);
            barrier.await();
            System.out.println("Thread3 makes countdown");
            latch.countDown();
        } catch (InterruptedException ex) {
            return;
        } catch (BrokenBarrierException ex) {
            return;
        }
    }
}
