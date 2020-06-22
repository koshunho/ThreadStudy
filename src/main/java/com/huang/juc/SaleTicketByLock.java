package com.huang.juc;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SaleTicketByLock {
    public static void main(String[] args) {
        Ticket ticket = new Ticket();

        new Thread(()->{for (int i = 1; i <= 40; i++) ticket.sale();}, "A").start();
        new Thread(()->{for (int i = 1; i <= 40; i++) ticket.sale();}, "B").start();
        new Thread(()->{for (int i = 1; i <= 40; i++) ticket.sale();}, "C").start();
    }
}

//资源类
class Ticket{
    private Lock lock = new ReentrantLock();

    private int ticketNums = 60;

    public void sale(){
        lock.lock();
        try{
            if(ticketNums > 0){
                System.out.println(Thread.currentThread().getName() + "卖出了第" + (ticketNums--) + "张票，还剩下:" + ticketNums);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}