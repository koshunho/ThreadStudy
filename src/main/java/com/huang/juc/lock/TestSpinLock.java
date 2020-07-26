package com.huang.juc.lock;

import java.util.concurrent.TimeUnit;

public class TestSpinLock {
    public static void main(String[] args) throws InterruptedException {
        SpinLock spinLock = new SpinLock();

        new Thread(()->{
            spinLock.myLock();
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                spinLock.myUnlock();
            }
        },"T1").start();


        //T2会一直自旋，直到T1释放锁
        new Thread(()->{
            spinLock.myLock();
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                spinLock.myUnlock();
            }
        },"T2").start();
    }
}
