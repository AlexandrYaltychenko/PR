
/**
 * Created by alexandr on 12.03.17.
 */
public class Thread5 extends Thread{
    private final Object waiter;
    public Thread5(Object waiter){
        this.waiter = waiter;
    }
    @Override
    public void run(){
        System.out.println("Thread5 thread started execution ");
        synchronized (waiter) {
            try {
                System.out.println("Thread5 waits");
                waiter.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Thread5 continued its execution ");
    }
}
