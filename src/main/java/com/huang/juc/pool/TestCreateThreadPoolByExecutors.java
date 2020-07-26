package com.huang.juc.pool;
// 池化技术：提前保存大量的资源，以备不时之需。在机器资源有限的情况下，使用池化技术可以大大提高资源的利用率，提升性能。
// 我们通过创建一个线程对象，并且实现Runnable接口就可以实现一个简单的线程。
// 但很多时候我们不止会执行一个任务。如果每次都是如此创建线程->执行任务->销毁线程，就会造成很大的性能开销
// 那能否一个线程创建后，执行完一个任务后，又去执行另一个任务，而不是销毁，这就是线程池

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 通过预先创建好多个线程，放在池中，这样可以在需要使用线程的时候 直接获取，避免多次重复创建、销毁带来的开销。
// Java中的线程池是通过Executor框架实现的，用到了Executor，Executors，ExecutorService， ThreadPoolExecutor这几个类
// 三大方法，七大参数
public class TestCreateThreadPoolByExecutors {
    public static void main(String[] args) {
        TestNewSingleThreadExecutor();

        // TestNewFixedThreadPool();

        // TestNewCachedThreadPool();
    }

    // 只有一个线程
/*    输出结果：
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务*/
    private static void TestNewSingleThreadExecutor(){
        ExecutorService threadPool = Executors.newSingleThreadExecutor();

        try {
            for (int i = 1; i <= 10; i++) {
                //模拟有10个顾客来办理业务，池子中只有1个工作人员受理
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName() + " 办理业务");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown(); //用完要关闭
        }
    }

    //执行长期任务性能好，创建一个线程池，一个池中有N个固定的线程，有固定线程数的线程
/*  输出结果：
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-2 办理业务
    pool-1-thread-3 办理业务
    pool-1-thread-4 办理业务
    pool-1-thread-5 办理业务*/
    private static void TestNewFixedThreadPool(){
        //池子大小 5
        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        try {
            //模拟有10个顾客来办理业务，池子中只有5个工作人员受理
            for (int i = 1; i <= 10; i++) {
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName() + " 办理业务");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    //执行很多短期异步任务，线程池根据需要创建新线程，但在 先构建的线程可用时 将 重用他们。遇强则强遇弱则弱
    /*  输出结果：
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-2 办理业务
    pool-1-thread-3 办理业务
    pool-1-thread-1 办理业务
    ....
    pool-1-thread-34 办理业务
    pool-1-thread-35 办理业务
    pool-1-thread-36 办理业务

    Process finished with exit code 0*/
    private static void TestNewCachedThreadPool(){
        //一池N线程，可扩容伸缩
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try {
            //模拟有10个顾客来办理业务，池子中有N个工作人员受理
            for (int i = 1; i <= 50; i++) {
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName() + " 办理业务");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}
