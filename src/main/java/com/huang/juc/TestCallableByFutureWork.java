package com.huang.juc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

// 我们要跑一个线程： new Thread(new Runnable()).start()
// 但这里是Callable咋办
// 找啊找啊发现FutureTask是Runnable的实现类
// 并且FutureTask的构造器是public FutureTask(Callable<V> callable)
// 所以就用一个FutureTask来包装Callabe，适配类
// 获取返回值用futureTask.get()
public class TestCallableByFutureWork {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MyThread myThread = new MyThread();

        FutureTask<Integer> futureTask = new FutureTask<>(myThread);

        new Thread(futureTask,"你爹").start();

        Integer i = futureTask.get();

        System.out.println(i);
    }
}

class MyThread implements Callable<Integer>{
    @Override
    public Integer call() throws Exception {
        System.out.println(Thread.currentThread().getName() + "'s call()");
        return 5464;
    }
}