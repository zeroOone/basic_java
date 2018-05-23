package ThreadAndRunnable;

/**
 * ClassName : ThreadDemo
 * @Description : 线程demo
 * @Author ： zero.one
 * @Date : 2018-05-22
 *
 * 实现线程机制的主要类与接口 ： java.lang.Thread类与java.lang.Runnable接口
 *
 * 线程主要涉及的三个部分：
 *
 * 1： 操作系统 —— 创建和管理线程
 * 2： 一个Thread对象 —— 关联一个线程
 * 3： Runnable接口 —— 为关联到Thread对象的线程提供执行代码 (void run方法)
 *
 * 注 ： void run 不接受任何参数不返回任何值，不过有可能抛异常
 *
 */

public class ThreadDemo {
    /**
     * 每一个java程序都会有一个执行main函数的默认的主线程
     * 而进程相当于一个容器，负责提供装在资源的空间，资源调度由线程完成
     * 一旦main函数开始，进程就启动了
     * 因此，你可以说一个java程序既是一个进程，也是一个线程
     * thread和runnable开启的线程，都是主线程的子线程，属于父子关系，此时java程序是多线程的
     * @param args
     */
    public static void main(String[] args) {
        //isDaemon作用是是否守护线程；
        //如果有参数传进来，为true，守护；否则为false，不守护；
        //守护线程的作用是辅助非守护线程；
        //当最后一个非守护线程结束的时候，守护线程会自动死亡，此时程序才终止；
        boolean isDaemon = args.length != 0;

        //Runnable负责执行线程代码，所以必须要重写run函数，而这里就是个匿名函数；
        Runnable r = new Runnable() {
            @Override
            //线程执行的代码
            public void run() {

                //currentThread：获得当前执行线程关联的Thread对象的引用;
                //通俗一点的是，当t1启动start的时候，thd指向t1，thd就等于t1；
                //也可以理解为thd为t1的别名，改动thd相当于改动t1或者t2
                Thread thd = Thread.currentThread();

                while (true) {
                    System.out.println(thd.getName()
                            + " is "
                            + (thd.isAlive()?"":"not")
                            + " alive and in"
                            + thd.getState()
                            + " state");
                }

            }
        };

        //这里实例化一个Thread类对象，Thread类对象关联一个单独的系统线程；
        //r：告诉该线程要执行的任务;
        //thd1: 为该线程命名；
        Thread t1 = new Thread(r, "thd1");

        if(isDaemon) {
            //调用setDaemon方法将t1设置为守护线程
            //下面就要看守护线程如何辅助非守护线程了
            t1.setDaemon(true);
        }

        //打印线程信息： 线程名、线程是否存活、当前线程状态
        //只有start方法启动后，线程才处于存活状态 —— isAlive = true；
        //当线程走出run函数后，死亡 —— isAlive = false；
        System.out.println(t1.getName()
                + " is "
                + (t1.isAlive()?"":"not")
                + " alive and in"
                + t1.getState()
                + " state");

        //创建第一个线程类对象，这次不在初始化的时候给它命名
        //结合t1，不难看出Thread有两个构造函数
        Thread t2 = new Thread(r);
        //使用调用方法的方式命名
        t2.setName("thd2");
        if(isDaemon) {
            t2.setDaemon(true);
        }
        System.out.println(t2.getName()
                + " is "
                + (t2.isAlive()?"":"not")
                + " alive and in"
                + t2.getState()
                + " state");

        //启动线程
        t1.start();
        t2.start();
    }
}

/**
 * 执行结果 ：
 *              1 ： 当运行的时候，不带参数，那么所有线程都是非守护线程，于是会进入while的死循环，不断打印线程信息
 *              2 ： 当传入参数的时候，所有线程都作为守护线程来执行，于是执行到主线程就终止了
 *              3 ： 至于守护线程何时执行到主线程，这个就比较玄学了，属于操作系统层面的，暂时无需深究
 *
 *  最后 ： 可以看到 thd1和thd2是交替打印的，说明两个线程有属于自己的执行路径；
 *
 *  从表层来看，线程就是把这个java程序复制了一份，然后把打印信息的代码替换成各自的代码；
 *
 *  从原理上理解，JVM给每条线程都分配了独立的栈空间，避免了互相干扰。以便于追踪自己的下一条指令；
 *
 *  画图显示：
 *
 *      多个CPU上 ：
 *              线程1 ： ———————————————————————————————————————————————————— 一路执行互不干扰
 *              线程2 ： ———————————————————————————————————————————————————— 一路执行互不干扰
 *              ......
 *              线程N ： ———————————————————————————————————————————————————— 一路执行互不干扰
 *
 *      单个CPU上 ：
 *              线程1 ： —— 暂停 —— 暂停 ——
 *              线程2 ： 暂停 —— 暂停 —— 暂停
 *
 *      当有多个线程运行在单个CPU上，线程是共享CPU时间的，当其中一个线程运行的时候，其它线程必须等待；
 *      CPU时间分配由操作系统负责调度；
 *      能调度的原因是：CPU大多数时间里都属于空闲状态，比如你在输入的时候，它就什么活都不干，这个属于操作系统层面，暂时无需深究；
 */
