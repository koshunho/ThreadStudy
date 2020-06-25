package com.huang.juc.jmm;

import java.util.concurrent.atomic.AtomicInteger;

// num++在多线程下是非线程安全的，如何不加synchronized解决？
// 使用原子包下的类
public class TestAtomicInteger {
    private static AtomicInteger num = new AtomicInteger();

    private static void safeAdd(){
        num.getAndIncrement();   //等价于 num++
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 20; i++) {
            new Thread(()->{
                for (int j = 0; j < 1000; j++) {
                    safeAdd();
                }
            }, String.valueOf(i)).start();
        }

        //需要等待上面20个线程都执行完毕后，看最终结果
        //默认一个main线程 一个gc线程
        while(Thread.activeCount() > 2){
            Thread.yield();
        }

        System.out.println(Thread.currentThread().getName() + " " + num);
    }
}
