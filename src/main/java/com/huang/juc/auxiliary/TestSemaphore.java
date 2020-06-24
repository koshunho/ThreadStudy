package com.huang.juc.auxiliary;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

//Semaphore就像内部维护了一个计数器，每个线程计数开始+1，maxまで会阻塞接下来的这个线程，直到有空位释放再进入。
//作用：多个共享资源互斥的使用。并发限流，控制最大的线程数
public class TestSemaphore {
    public static void main(String[] args) {
        //有3个停车位
        Semaphore semaphore = new Semaphore(3);

        //有6个车。想抢3个停车位
        for (int i = 1; i <= 6; i++) {
            new Thread(()->{
                try {
                    semaphore.acquire();    //得到

                    System.out.println(Thread.currentThread().getName() + "获得了车位");

                    TimeUnit.SECONDS.sleep(2);

                    System.out.println(Thread.currentThread().getName() + "离开车位");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();  //释放
                }
            }, String.valueOf(i)).start();
        }
    }
}
