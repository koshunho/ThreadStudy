package com.huang.juc.lock;

import java.util.concurrent.atomic.AtomicReference;

public class SpinLock {
    private AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public void myLock(){
        Thread thread = Thread.currentThread();

        System.out.println(thread.getName() + "-> myLock");

        while(!atomicReference.compareAndSet(null, thread)){
            //自旋
            //采用循环的方式尝试获取锁
            System.out.println(thread.getName() + "-> is waiting the lock");
        }
    }

    public void myUnlock(){
        Thread thread = Thread.currentThread();

        atomicReference.compareAndSet(thread, null);

        System.out.println(thread.getName() + "-> myUnlock");
    }
}
