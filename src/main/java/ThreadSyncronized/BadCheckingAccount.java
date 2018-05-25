package ThreadSyncronized;



public class BadCheckingAccount {
    private volatile int balance;

    public BadCheckingAccount(int initialBalance) {
        balance = initialBalance;
    }

    /**
     * 这里没有做同步控制，导致竞态条件；
     * 并且balance没有声明volitile关键字，导致缓存变量问题
     *
     * 最后结果： 数据不一致。两条线程某个情况下会读取到脏数据
     *
     * 修改： balance 添加volitile保证可见性
     *       withdraw 使用同步关键字保持互斥性
     * @param amount
     * @return
     */
   public synchronized boolean withdraw(int amount) {
        if (amount <= balance) {
            try{
                Thread.sleep((int)(Math.random() * 200));
            }catch (InterruptedException e) {

            }
            balance -= amount;
            return true;
        }
        return false;
   }

   public static void main(String[] args) {
        final BadCheckingAccount bca = new BadCheckingAccount(100);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                for (int i = 0; i<10; i++) {
                    System.out.println(name + " withdraw $10: " + bca.withdraw(10));
                }
            }
        };

        Thread thdA = new Thread(r, "Husband");
        Thread thdB = new Thread(r, "wife");

        thdA.start();
        thdB.start();
   }
}
