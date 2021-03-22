import java.util.concurrent.locks.ReentrantLock;

public class DeadLock extends Thread{
    protected Object myDirect;
    static ReentrantLock south = new ReentrantLock();
    static ReentrantLock north = new ReentrantLock();

    public DeadLock(Object obj){
        this.myDirect = obj;
        if (myDirect == south) {
            this.setName("south");
        }
        if (myDirect == north) {
            this.setName("north");
        }

    }

    @Override
    public void run() {
        if (myDirect == south) {
            try {
                north.lockInterruptibly();   //占用north
                Thread.sleep(500);
                south.lockInterruptibly();   //占用south
                System.out.println("car to south has passed");
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("car to south is killed");
            }finally {
                if (north.isHeldByCurrentThread())
                    north.unlock();
                if (south.isHeldByCurrentThread())
                    south.unlock();
            }
        }
        if (myDirect == north) {
            try {
                south.lockInterruptibly();   //占用south
                Thread.sleep(500);
                north.lockInterruptibly();   //占用north
                System.out.println("car to south has passed");
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("car to south is killed");
            }finally {
                if (south.isHeldByCurrentThread())
                    south.unlock();
                if (north.isHeldByCurrentThread())
                    north.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DeadLock car2South = new DeadLock(south);
        DeadLock car2North = new DeadLock(north);
        car2South.start();
        car2North.start();
        Thread.sleep(1000);
    }
}

