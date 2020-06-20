package com.huang.syn;

public class UnsafeBuyingTicket {
    public static void main(String[] args) {
        BuyTicket buyTicket = new BuyTicket();

        new Thread(buyTicket,"小柳").start();
        new Thread(buyTicket,"古月").start();
        new Thread(buyTicket,"坪川").start();
    }
}

class BuyTicket implements Runnable{

    private int ticketNums = 10;

    private boolean flag = true;


    @Override
    public void run() {
        while(flag){
            try {
                buy();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void buy() throws InterruptedException {
        if(ticketNums <= 0) {
            flag = false;
            return;
        }
        Thread.sleep(1000);

        System.out.println(Thread.currentThread().getName() + "拿到" + ticketNums-- + "张票");
    }
}