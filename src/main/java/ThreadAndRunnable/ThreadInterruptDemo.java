package ThreadAndRunnable;

/**
 * 操作更加高级的线程任务 —— 中断线程;
 *
 * method: 1: void interrupt() —— 调用此方法的线程被中断
 *         2: static boolean interrupted() —— 判断当前线程是否中断 ， 是 == true；
 *         3: boolean isInterrupted() —— 判断调用该方法的线程是否中断， 是 == true
 *         注意区分 2和3
 *
 * 1 ： 线程被中断的时候，抛出 ： java.lang.InterruptedException, 需要捕获该异常
 * 2 ： sleep或者join方法也需要捕获上述异常
 */
public class ThreadInterruptDemo{

    public static void main(String[] args) {
        //创建任务
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //获取当前线程名字
                String name = Thread.currentThread().getName();
                int count = 0;
                while(!Thread.interrupted()) {
                    System.out.println(name + " : " + count++);
                }
            }
        };
        //创建线程并交代线程执行任务
        Thread thdA = new Thread(r);
        Thread thdB = new Thread(r,"thdB");
        //开始执行
        thdA.start();
        thdB.start();

        //消耗时间 （相当于上述给上述任务分配执行时间，当n落入if判断范围，while中断，意味着线程执行时间到了）
        //这里可以使用 sleep函数替代： try{sleep(2000)}cath(InterruptedException e) {}
        //睡眠时间是大概的时间，打印出来的行数会有差异，但是差异不大
        while (true) {
            double n = Math.random();

            if(n >= 0.49999999 && n <= 0.50000001 ) {
                break;
            }
        }
        //中断线程（通过主线程在后台对象上执行interrupt方法）
        thdA.interrupt();
        //判断当前线程是否中断，当前线程应该是main主线程，正确的应该返回false——未中断
        System.out.println("1 : " +Thread.interrupted());
        //判断thdA线程是否已经中断 : true
        System.out.println("2 : " +thdA.isInterrupted());
        //判断thdB线程是否已经中断 : false
        System.out.println("3 : " +thdB.isInterrupted());
        thdB.interrupt();
        //判断thdB此时是否已经中断 ： true
        System.out.println("4 : " +thdB.isInterrupted());
        //再判断当前线程（main主线程只有程序结束时才会中断）是否已经中断 ： false
        System.out.println("5 : " +Thread.interrupted());
    }
    /**
     * 最后判断线程是否中断的时候，正确的结果应该是 1:fasle 2:true; 3:false 4:true; 5:false
     * 然而实际情况并非如此，因为这是个线程并行的程序，而且调用一个中断线程的方法时，被中断线程并不会立马中断
     */


}
