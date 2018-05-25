package ThreadSyncronized;

/**
 * 尝试停止一个线程
 */
public class ThreadStopping {
    public static void main(String[] args) {
        //继承Thread类
        //该程序运行的时候，每次都会检查stopped是否为true
        //主线程让thd调用start，然后又在thd身上调用stop，所以单处理器/单核机器，可以观察到程序停止

        //但是如果是多处理器/多核，由于每个处理器都可能有自己的拷贝，因此stopped被修改的时候，其他线程的拷贝并没有改变，
        //于是可能就不会停止
        class StopppableThread extends Thread {
            //使用volatile
            //每条线程都会访问主存该变量的拷贝，而不是缓存中的拷贝，因此多线程也会停止
            private volatile boolean stopped;
            //private boolean stopped;

            @Override
            public void run() {
                while(!stopped) {
                    System.out.println("Runnning");
                }
            }

            void stopThread() {
                stopped = true;
            }
        }

        StopppableThread thd = new StopppableThread();

        thd.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }
        thd.stopThread();
    }
}
