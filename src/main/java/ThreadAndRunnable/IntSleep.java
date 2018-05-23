package ThreadAndRunnable;

/**
 * 该程序是主线程与子线程并行的程序
 * 由于该程序在主线程沉睡2s后中断子线程，
 * 所以打印Hello是2s内打印出的次数
 */
public class IntSleep {
    public static void main(String[] args) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                int count =0;
                while(true) {
                    count ++;

                    System.out.println("Hello : " + count );
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted");
                        break;
                    }
                }
            }
        };

        Thread t1 = new Thread(r);
        t1.start();
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.interrupt();
    }

}
