package com.huang.juc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
* 多个线程同时读一个资源类OK。
* 只有一个线程写共享资源。
* 所以用读写锁ReadWriteLock
*
* */


public class TestReadWriteLock {
    public static void main(String[] args) {
        MyCache myCache = new MyCache();

        //写
        for (int i = 1; i <= 5; i++) {
            final int temp = i; //注意final
            new Thread(()->{
                myCache.put(String.valueOf(temp), Thread.currentThread().getName());
            }, String.valueOf(i)).start();
        }

        //读
        for (int i = 1; i <= 5; i++) {
            final int temp = i; //注意final
            new Thread(()->{
                myCache.get(String.valueOf(temp));
            }, String.valueOf(i)).start();
        }
    }
}

class MyCache{
    private volatile Map<String, Object> map = new HashMap<>();

    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public void put(String key, Object value){

        readWriteLock.writeLock().lock();

        try {
            System.out.println(Thread.currentThread().getName() + " 写入" + key);
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + " 写入成功！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void get(String key){
        readWriteLock.readLock().lock();

        try {
            System.out.println(Thread.currentThread().getName() + " 读取" + key);
            Object value = map.get(key);
            System.out.println(Thread.currentThread().getName() + " 读取成功！结果：" + value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.readLock().unlock();
        }

    }
}

/*
测试结果：
        1 写入1
        1 写入成功！
        2 写入2
        2 写入成功！
        4 写入4
        4 写入成功！
        5 写入5
        5 写入成功！
        3 写入3
        3 写入成功！
        5 读取5
        5 读取成功！结果：5
        3 读取3
        3 读取成功！结果：3
        2 读取2
        2 读取成功！结果：2
        1 读取1
        4 读取4
        1 读取成功！结果：1
        4 读取成功！结果：4*/
