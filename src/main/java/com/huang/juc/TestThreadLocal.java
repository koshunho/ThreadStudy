package com.huang.juc;

import java.util.concurrent.TimeUnit;

// ThreadLocal的作用为每个使用该变量的线程提供独立的变量副本（也就是局部变量）。
// 所以每一个线程都可以独立地改变自己的副本，而不会影响其他线程所对应的副本（-->数据隔离）
// 经典使用场景：数据库连接，session管理
public class TestThreadLocal {

    private static final ThreadLocal<Integer> local = ThreadLocal.withInitial(() -> 0);

    static class MyThread implements Runnable{

        private int index;

        public MyThread(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            System.out.println("线程"+index+"的初始化值为："+local.get());

            for (int i = 0; i < 10; i++) {
                local.set(local.get()+i);
            }

            System.out.println("线程"+index+"最后累加的值位："+local.get());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 1; i <= 3; i++) {
            Thread thread = new Thread(new MyThread(i),"Thread"+ i);
            thread.start();
            TimeUnit.SECONDS.sleep(3);
        }
    }
}
/*
输出结果：
        线程1的初始化值为：0
        线程1最后累加的值位：45
        线程2的初始化值为：0
        线程2最后累加的值位：45
        线程3的初始化值为：0
        线程3最后累加的值位：45*/
//可以看到，各个线程的value值是相互独立的，本线程的累加操作不会影响到其他线程的值，真正达到了线程内部隔离的效果。
