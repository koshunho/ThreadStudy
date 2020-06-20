package com.huang.demo;

public class TestSleep implements Runnable{

    private int tickerNums = 10;

    public void run() {
        while(true){
            if(tickerNums <= 0){
                break;
            }

            //不sleep的话，有的人一下就把票拿完了
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "-->拿到了第" + tickerNums-- + "张票");
        }
    }

    public static void main(String[] args) {
        TestSleep testSleep = new TestSleep();

        // 并发问题，多个线程操纵同一个对象
        new Thread(testSleep, "习近平").start();
        new Thread(testSleep,"小柳").start();
        new Thread(testSleep, "古月").start();
    }
}
