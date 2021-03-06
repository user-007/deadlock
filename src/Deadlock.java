import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class Deadlock {
    private static Object lock1 = new Object();
    private static Object lock2 = new Object();

    //more than one thread - competing for resource
    // T1 -> lock(X) -> wait_for(Y)
    //  T2 -> lock(Y) -> wait_for(X)
    //implementing class in other class
    public static void main(String[] args) throws InterruptedException {
        Worker1 worker1 = new Worker1();
        Worker2 worker2 = new Worker2();
        Thread thread1 = new Thread(worker1);
        Thread thread2 = new Thread(worker2);
        Thread thread3 = new Thread(new DeadLockDetect());
        thread1.start();
        thread2.start();
        thread3.start();
        // waiting for the other
        thread2.join();
    }
    private static class DeadLockDetect implements Runnable{
        @Override
        public void run() {
            while(true)

            {
                ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
                long[] threadIds = threadMXBean.findDeadlockedThreads();
                if(threadIds != null){
                    System.out.println("Thread lock");
                    for (long threadId: threadIds){
                        ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId);
                        System.out.println(threadInfo.getThreadName());
                    }
                    return;
                }

            }
        }
    }

    private static class Worker1 implements Runnable {
        @Override
        public void run() {
            synchronized (lock1) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2) {
                    System.out.println("Hello");
                }
            }
        }
    }

        private static class Worker2 implements Runnable {
            @Override
            public void run() {
                synchronized (lock2) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (lock1) {
                        System.out.println("Hello");
                    }
                }
            }
        }
    }

