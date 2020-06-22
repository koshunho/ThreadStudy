package com.huang.juc;

import java.util.concurrent.CountDownLatch;
//线程执行速度不一样，有的快有的慢，部分业务需要等所有线程结束后才能处理后面的逻辑。所以就用CountdownLatch
//countDownLatch.countDown() 和 countDownLatch.await()配套使用
public class TestCountDownLatch {

    //需求：有6个人要从一个房间出门。六个人走后才能关门
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(6); //总数是6，必须要执行任务的时候才使用

        for (int i = 1; i <= 6; i++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName() + "left");
                countDownLatch.countDown(); //数量-1
            }, String.valueOf(i)).start();
        }

        countDownLatch.await(); //等待计数器归零，然后再向下执行！！！！！

        System.out.println("All people already left! Close door");
    }
}
