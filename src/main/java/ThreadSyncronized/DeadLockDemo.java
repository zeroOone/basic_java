package ThreadSyncronized;

import javax.swing.plaf.synth.SynthEditorPaneUI;

/**
 * 死锁场景 ： syncronized关键字带来的过多同步上。
 *
 * 描述： 锁同时被多条线程竞争，即线程自身缺失继续执行的锁，却持有其他线程需要的锁，同时由于其他线程持有临界区的锁，
 *      最后没有一条线程能够通过临界区，进而释放自己所持的锁。
 */
public class DeadLockDemo {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void instanceMethod1() {
        //嵌套同步块锁住对象
        synchronized(lock1){
            synchronized (lock2) {
             System.out.println("First thread in instanceMethod1");
            }
        }
    }

    public void instanceMethod2() {
        synchronized (lock2) {
            synchronized (lock1) {
                System.out.println("Second thread in instanceMethod2");
            }
        }
    }

    public static void main(String[] args) {
        final DeadLockDemo dld = new DeadLockDemo();

        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    dld.instanceMethod1();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {

                    }
                }
            }
        };

        Thread thdA = new Thread(r1);

        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    dld.instanceMethod2();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {

                    }
                }
            }
        };

        Thread thdB = new Thread(r2);
        thdA.start();
        thdB.start();
    }
}

/**
 * 分析 ：
 *         线程A执行r1，首先获取了lock1的锁，锁住lock1后，去获取lock2的锁（打印完毕并后释放）；
 *         线程B执行r2，首先获取了lock2的锁，锁住lock2后，去获取lock1的锁（同上）；
 *
 *         问题出在，这是个多线程并发运行的，而线程睡眠并不会释放已经获得的锁；
 *
 *         在某一个时刻，会出现这样的情况，当A调用方法1后，获取lock1但是仍旧在临界区外，
 *         这个时候它需要获取到lock2的锁，才可以进入临界区；
 *
 *         然而，此时B调用了方法2，获取到了lock2的锁，但是它处在临界区外，只有获取到lock1后，
 *         才可以访问临界区。
 *
 *         于是，A拥有lock1，在等待lock2，没有lock2它就无法通过临界区，没有办法释放锁；
 *         同样的，B拥有lock2，在等待lock1，没有lock1，也一样无法穿过临界区。
 *         造成了这样的结果：
 *                          A持有lock1，在等lock2；B持有lock2，在等lcok1；
 *                          互相等待，结果谁也没办法通过临界区，于是程序死锁，冻结；
 *
 *         在这里，睡眠的作用是为了能在某一时刻，线程A与B同时进行方法调用；
 *         尽管线程并发，但是并不是掐着时间点同时启动的。
 *
 **/