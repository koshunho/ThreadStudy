package com.huang.juc.lock;
/*
* 可重入锁：指的是同一线程外层函数获得锁之后，内层函数仍然能获取该锁。在同一个线程在外层方法获取锁的时候，在进入内层方法会自动获取锁
* 也就是说，线程可以进入任何一个它已经拥有的锁，所同步着的代码块。
* 好比家里进入大门之后，就可以进入里面的房间了*/

/*输出结果：
T1 发短信
T1 打电话
T2 发短信
T2 打电话*/

import java.util.concurrent.CountDownLatch;

// 可重入锁的最大作用就是避免死锁
public class TestReentrantLock {
    public static void main(String[] args) throws InterruptedException {
        Phone phone = new Phone();

        CountDownLatch countDownLatch = new CountDownLatch(1);



        // T1在外层获取锁的时候，也会自动获取里面的锁

        // 2020.7.26修改：这例子不对吧！！！！calling不加synchronized也可以啊！！！
        new Thread(()->{
            try {
                countDownLatch.await();
                phone.SMS();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"T1").start();

        new Thread(()->{
            try {
                countDownLatch.await();
                phone.calling();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"T2").start();

        Thread.sleep(3000);
        countDownLatch.countDown();
    }
}


class Phone{
    public synchronized void SMS(){
        System.out.println(Thread.currentThread().getName() + " 发短信");

        // key
        calling();
    }

    public synchronized void calling(){
        System.out.println(Thread.currentThread().getName() + " 打电话");
    }
}
