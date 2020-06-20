package com.huang.demo;

// 不一定礼让成功，看CPU心情
public class TestYield {
    public static void main(String[] args) {
        MyYield myYield = new MyYield();

        new Thread(myYield,"a").start();
        new Thread(myYield,"b").start();
    }
}

class MyYield implements Runnable{
    public void run() {
        System.out.println(Thread.currentThread().getName() +" starting");
        Thread.yield();
        System.out.println(Thread.currentThread().getName() +" stop");
    }
}
