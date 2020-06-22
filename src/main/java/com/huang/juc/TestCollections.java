package com.huang.juc;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestCollections {
    public static void main(String[] args) {

        /*
        报错：java.util.ConcurrentModificationException
        原因：add方法没有加锁
        */
        //testUnsafeList();

        /*CopyOnWrite:写入时复制
        读写分离，写时复制出一个新的数组，完成插入、修改or移除操作后将新数组赋值给array
        CopyOnWriteArrayList()
        */
        //testSafeList();

        //testUnsafeMap();

        /*ConCurrentHashmap()*/
        testSafeMap();
    }

    private static void testUnsafeList(){
        List<String> list = new ArrayList<>();

        for (int i = 1; i <= 30; i++) {
            new Thread(()->{
                list.add(UUID.randomUUID().toString().substring(0,5));
                System.out.println(list);
            },String.valueOf(i)).start();
        }
    }

    private static void testSafeList(){
        List<String> list = new CopyOnWriteArrayList<>();

        for (int i = 1; i <= 30; i++) {
            new Thread(()->{
                list.add(UUID.randomUUID().toString().substring(0,5));
                System.out.println(list);
            },String.valueOf(i)).start();
        }
    }

    private static void testUnsafeMap(){
        Map<String,String> map = new HashMap<>(16,0.75f);

        for (int i = 1; i <= 30; i++) {
            new Thread(()->{
                map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0,5));
                System.out.println(map);
            }, String.valueOf(i)).start();
        }
    }

    private static void testSafeMap(){
        Map<String,String> map = new ConcurrentHashMap<>();

        for (int i = 1; i <= 30; i++) {
            new Thread(()->{
                map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0,5));
                System.out.println(map);
            }, String.valueOf(i)).start();
        }
    }
}
