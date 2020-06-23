package com.huang.juc;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

// SynchronousQueue:同步队列
// 每一个put操作必须要等待一个take操作，否则不能继续添加元素，反之亦然。 所以才叫同步
public class TestSynchronousQueue {
    public static void main(String[] args) {
        SynchronousQueue synchronousQueue = new SynchronousQueue();

        new Thread(()->{
            try {
                System.out.println(Thread.currentThread().getName() + " put 1");
                synchronousQueue.put("1");
                System.out.println(Thread.currentThread().getName() + " put 2");
                synchronousQueue.put("2");
                System.out.println(Thread.currentThread().getName() + " put 3");
                synchronousQueue.put("3");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "T1").start();

        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + " took "+ synchronousQueue.take());

                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + " took "+ synchronousQueue.take());

                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + " took "+ synchronousQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "T2").start();
    }
}
/*
输出结果：
        T1 put 1
        T2 took 1
        T1 put 2
        T2 took 2
        T1 put 3
        T2 took 3
*/
