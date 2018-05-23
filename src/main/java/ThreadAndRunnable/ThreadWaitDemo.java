package ThreadAndRunnable;

import java.math.BigDecimal;

/**
 * 操作更加高级的线程任务 —— 等待线程
 *
 * 过程：
 *      线程A（比如默认的主线程）会偶尔启动另一个线程B去操作单调的计算、下载等耗时任务。
 *      这里线程A为启动线程，线程B为工作线程。
 *      工作线程结束任务后，启动线程着手处理返回的结果，同时等待工作线程结束。
 *
 * 方法（Thread类提供）： 允许启动线程等待工作线程执行完毕
 *
 *                   1 ： void join() —— A无限期等待任务结束直至B死亡
 *
 *                   2 ： void join(long ms) —— A最多等待ms毫秒（0则无限等待），
 *                        为负抛出IllegalArgumentException
 *
 *                   3 ： void join(long ms, long ns) —— 最多等待ms毫秒加ns纳秒，
 *                        为负或者ns>999999抛出IllegalArgumentException异常
 */
public class ThreadWaitDemo {
    /**
     * 这里计算pi小数点后5000位来演示不含参数的等待线程方法 void join()
     */
    private static final BigDecimal FOUR = BigDecimal.valueOf(4);

    private static final int roundingMode = BigDecimal.ROUND_HALF_EVEN;

    private static BigDecimal result;

    /**
     * 默认的主线程首先创建了runnable任务，随后主线程创建了一个Thread对象 t 去执行该任务，
     * 最后，主线程在t对象上调用了join方法等待t死亡；
     * t死亡后，主线程打印结果；
     * @param args
     */
    public static void main(String[] args) {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                result = computePi(50000);
            }
        };

        Thread t =new Thread(r, "son");
        t.start();

        try {
            //主线程在t上调用join函数，等待t死亡
            t.join();
        } catch (InterruptedException e) {
        }
        //主线程打印
        /*
        while (t.isAlive()) {

            //耗时间，t死亡的时候跳出循坏
        }*/
        System.out.println(t.isAlive());
        System.out.println(Thread.currentThread().getName());
        System.out.println(result);

    }

    public static BigDecimal computePi(int digit) {
        int scale = digit + 5;
        BigDecimal arctan1_5 = arctan(5, scale);
        BigDecimal arctan1_239 = arctan(239, scale);
        BigDecimal pi = arctan1_5.multiply(FOUR).subtract(arctan1_239).multiply(FOUR);

        return pi.setScale(digit, BigDecimal.ROUND_HALF_UP);

    }

    public static BigDecimal arctan(int inverseX, int scale) {
        BigDecimal result, numer, term;
        BigDecimal invX = BigDecimal.valueOf(inverseX);
        BigDecimal invX2 = BigDecimal.valueOf(inverseX * inverseX);
        numer = BigDecimal.ONE.divide(invX, scale, roundingMode);
        result = numer;

        int i = 1;

        do {
            numer = numer.divide(invX2, scale, roundingMode);
            int denom = 2 * i +1 ;
            term = numer.divide(BigDecimal.valueOf(denom), scale, roundingMode);
            if ((i % 2) != 0) {
                result = result.subtract(term);
            } else {
                result = result.add(term);
            }
            i++;
        } while (term.compareTo(BigDecimal.ZERO) != 0);

        return result;
    }

}
/**
 * 在这里，打印pi是放在main函数里执行的，子线程t并不会执行到这里；
 *
 * 如果主线程不等待t线程（注释掉 t.join()），那么就是两个线程并行运行，
 * t去计算pi的时候，主线程已经打印结果了，然而t的结果并没有返回，所以输出为null；
 *
 * 同时，猜测子线程完成任务会自动终止么？
 *
 * 在这里注释掉t.join后，采用while(t.isAlive)循环来测试，结果：
 *                                              run结束后，t结束；
 */
