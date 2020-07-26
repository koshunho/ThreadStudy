package com.huang.juc.auxiliary;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class TestCyclicBarrier {
    /*集齐7颗龙珠召唤神龙 -> 等待7个线程执行完毕后，然后才执行卸载CyclicBarrier的线程*/
    //cyclicBarrier.await(); 等待
    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7, () -> System.out.println("召唤神龙成功！"));

        for (int i = 1; i <= 7; i++) {
            final int temp = i; //Lambda中取不到i，所以用一个final的变量来存储i的值
            new Thread(()->{
                System.out.println("收集到了第" + temp + "颗龙珠");
                try {
                    cyclicBarrier.await(); //等待
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("第" + temp + "线程结束了");
            }).start();
        }
    }
}
