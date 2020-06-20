package com.huang.syn;

import java.util.concurrent.locks.ReentrantLock;

public class TestLock {
    public static void main(String[] args) {
        BuyTicketNibanme buyTicketNibanme = new BuyTicketNibanme();

        new Thread(buyTicketNibanme,"嘻嘻").start();
        new Thread(buyTicketNibanme,"哈哈").start();
        new Thread(buyTicketNibanme,"噜噜").start();
    }
}


class BuyTicketNibanme implements Runnable{

    int ticketNums = 10;

    //定义Lock
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        while(true){
            try {
                //！！！！sleep不释放对象的锁，所以程序一直是第一个线程在执行。先把sleep放在锁前就OK了！！！
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //加锁
                lock.lock();
                if(ticketNums>0){
                    System.out.println(Thread.currentThread().getName() + "买到了第" + ticketNums-- + "张票");
                }else{
                    break;
                }
            }finally {
                //解锁
                lock.unlock();
            }

        }
    }
}