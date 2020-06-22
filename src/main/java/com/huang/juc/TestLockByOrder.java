package com.huang.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/*
需求：多线程之间按照顺序调用，实现A->B->C
三个线程启动要求如下，A打印5次，B打印10次，C打印15次，依次循环
*/
public class TestLockByOrder {
    public static void main(String[] args) {

        Resource resource = new Resource();

        new Thread(()->{ for (int i = 0; i < 5; i++) { resource.print5(); }}, "A").start();
        new Thread(()->{ for (int i = 0; i < 5; i++) { resource.print10(); }}, "B").start();
        new Thread(()->{ for (int i = 0; i < 5; i++) { resource.print15(); }}, "C").start();
    }
}

// 资源类. 判断等待、干活、通知
// 判断等待、干活、通知
// 判断等待、干活、通知
// 判断等待、干活、通知
// 重要的事情说3遍
class Resource{
    private int number = 1; //标志位 1A 2B 3C
    private Lock lock = new ReentrantLock();
    private Condition condition1 = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();

    public void print5(){
        lock.lock();
        try {
            //判断等待
            while(number != 1){
                condition1.await();
            }

            // 干活
            for (int i = 1; i <= 5; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" + i);
            }

            // 通知
            number = 2;
            condition2.signal();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void print10(){
        lock.lock();
        try {
            //判断等待
            while(number != 2){
                condition2.await();
            }

            // 干活
            for (int i = 1; i <= 10; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" + i);
            }

            // 通知
            number = 3;
            condition3.signal();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void print15(){
        lock.lock();
        try {
            //判断等待
            while(number != 3){
                condition3.await();
            }

            // 干活
            for (int i = 1; i <= 15; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" + i);
            }

            // 通知
            number = 1;
            condition1.signal();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}