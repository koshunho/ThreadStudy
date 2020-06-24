package com.huang.juc.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.LongStream;

public class ForkJoinTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // invoke = 20000000100000000 time: 1422
         testForkJoin();

        // invoke = 20000000100000000 time: 2082
        // testSingleThread();

        //invoke = 20000000100000000 time: 235
        // testStream();
    }

    private static void testForkJoin() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        ForkJoinPool forkJoinPool = new ForkJoinPool();

        ForkJoinWork forkJoinWork = new ForkJoinWork(0L, 200000000L);

        ForkJoinTask submit = forkJoinPool.submit(forkJoinWork);

        Long l =(Long)submit.get();

        Long end = System.currentTimeMillis();

        System.out.println("invoke = " + l + " time: " + (end - start));
    }

    private static void testSingleThread(){
        long start = System.currentTimeMillis();

        Long res = 0L;
        Long y = 200000000L;

        for (Long i = 0L; i <= y; i++) {
            res += i;
        }

        Long end = System.currentTimeMillis();

        System.out.println("invoke = " + res + " time: " + (end - start));
    }

    //Java 8 并行流的实现
    private static void testStream(){
        long start = System.currentTimeMillis();

        //这是什么？？？
        Long reduce = LongStream.rangeClosed(0l, 200000000L).parallel().reduce(0, Long::sum);

        Long end = System.currentTimeMillis();

        System.out.println("invoke = " + reduce + " time: " + (end - start));
    }
}
