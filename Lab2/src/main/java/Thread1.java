import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by alexandr on 12.03.17.
 */
public class Thread1 extends Thread {
    private final CyclicBarrier barrier;
    public Thread1(CyclicBarrier barrier){
        this.barrier = barrier;
    }
    @Override
    public void run(){
        System.out.println("Thread1 thread started execution ");
        try {
            Thread.sleep(500);
            barrier.await();
        } catch (InterruptedException ex) {
            return;
        } catch (BrokenBarrierException ex) {
            return;
        }
    }
}
