package com.huang.juc.single;

public class Lazy {

    private static Lazy instance;

    private Lazy(){
        System.out.println(Thread.currentThread().getName() + " OK");
    }

    public static Lazy getInstance(){
        if(instance == null){
            instance = new Lazy();
        }
        return instance;
    }

    //多线程并发，有问题
    /*输出结果：（不固定）
      Thread-1 OK
      Thread-0 OK*/
    public static void main(String[] args) {
        for (int i = 1; i <= 100; i++) {
            new Thread(()->{
                Lazy.getInstance();
            }).start();
        }

        while(Thread.activeCount() > 2){
            Thread.yield();
        }
    }
}
