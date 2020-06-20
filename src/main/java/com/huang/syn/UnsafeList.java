package com.huang.syn;

import java.util.ArrayList;
import java.util.List;

//多个线程都添加到同一个位置，就覆盖掉了
public class UnsafeList {
    public static void main(String[] args) throws InterruptedException {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            new Thread(()->{
                synchronized (list){
                    list.add(Thread.currentThread().getName());
                }
            }).start();
        }

        //不加sleep 主线程会提前跑完！！！！
        Thread.sleep(1000);

        System.out.println(list.size());
    }
}
