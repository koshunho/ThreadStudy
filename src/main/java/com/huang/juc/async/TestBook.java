package com.huang.juc.async;

import java.util.concurrent.*;

public class TestBook {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        CountDownLatch count = new CountDownLatch(2);

        //任务1 订购航班
        Future<String> orderAirplane = pool.submit(() -> {
            System.out.println("查询航班");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("订购航班");
            count.countDown();
            return "航班号";
        });

        //任务2 订购酒店
        Future<String> orderHotel = pool.submit(() -> {
            System.out.println("查询酒店");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("订购酒店");
            count.countDown();
            return "酒店";
        });

        count.await();

        Future<String> orderCar = pool.submit(() -> {
            System.out.println("根据航班加酒店订购租车服务");
            try {
                TimeUnit.SECONDS.sleep(6);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "租车信息";
        });

        String s = orderCar.get();

        System.out.println(s);

        pool.shutdown();
    }
}
