package com.huang.syn;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TestBlockingQueue {
    public static void main(String[] args) {
        BlockingQueue<Donburi> queue = new ArrayBlockingQueue<Donburi>(10);

        DonburiProducer donburiProducer = new DonburiProducer(queue);
        DonburiConsumer donburiConsumer = new DonburiConsumer(queue);

        new Thread(donburiProducer).start();
        new Thread(donburiConsumer).start();

        System.out.println("开始买卖");
    }
}

class Donburi{
    private String id;

    public Donburi(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

class DonburiProducer implements Runnable{
    private BlockingQueue<Donburi> queue;

    public DonburiProducer(BlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        int i;
        for (i = 0; i < 100; i++) {
            try {
                queue.put(new Donburi(String.valueOf(i)));
                System.out.println("生产了第" + i + "碗丼");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Donburi lastOrder = new Donburi("lastOrder");
        try {
            queue.put(lastOrder);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class DonburiConsumer implements Runnable{
    private BlockingQueue<Donburi> queue;

    public DonburiConsumer(BlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            Donburi donburi;
            while(!(donburi = queue.take()).getId().equals("lastOrder")){
                System.out.println("消费了-->第"+ donburi.getId()+"碗どんぶり");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}