package com.huang.juc.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TestBookByCompletableFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //任务1 查询航班
        CompletableFuture<String> orderAirplane = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询航班");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("订购航班");
            return "航班号";
        });

        //任务2 查询酒店
        CompletableFuture<String> orderHotel = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询酒店");
            try {
                TimeUnit.SECONDS.sleep(6);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("订购酒店");
            return "酒店信息";
        });

        CompletableFuture<String> hireCar = orderHotel.thenCombine(orderAirplane, (a, b) -> {
            System.out.println("根据航班 + 酒店 订购租车服务");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "租车信息";
        });

        String s = hireCar.get();

        System.out.println(s);
    }
}
