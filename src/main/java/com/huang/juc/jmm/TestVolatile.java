package com.huang.juc.jmm;

import java.util.concurrent.TimeUnit;

public class TestVolatile {

    private volatile static int num = 0;

    public static void main(String[] args) {
        /*这里有两个线程。一个是new Thread，一个main线程
        测试加没加volatile前后的效果*/
        // volatile可保证数据的同步，也就是可见性
        // testVisibility();

        testAtomicity();
    }

    private static void testVisibility(){
        new Thread(()->{
            while(num == 0){
            //故意死循环
            }
        }).start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        num = 1;

        System.out.println(num);
    }

    private static void testAtomicity(){

        for (int i = 1; i <= 20; i++) {
            //正常的话结果应该是2w，但是实际却小于2w，即使加了volatile
            new Thread(()->{
                for (int j = 0; j < 1000; j++) {
                    unsafeAdd();
                }
            }, String.valueOf(i)).start();
        }

        // 需要等待上面20个线程都计算完毕，看最终结果
        while(Thread.activeCount() > 2){   //默认一个main线程 一个gc线程
            Thread.yield();
        }

        System.out.println(Thread.currentThread().getName() + " " + num);
    }

    private static void unsafeAdd(){
        //num++是非原子性操作！！
        /*num++实际上是3个指令：
        * 1.执行getfield拿到原始num
        * 2.执行iadd进行加1操作
        * 3.执行putfield写把累加后的值写回*/
        num++;
    }
}
