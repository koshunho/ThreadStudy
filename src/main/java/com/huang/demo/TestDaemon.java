package com.huang.demo;

public class TestDaemon {
    public static void main(String[] args) {
        God god = new God();
        You you = new You();

        Thread godThread = new Thread(god);
        godThread.setDaemon(true);   //false代表是用户线程，true就是守护线程。虚拟机保证用户进程执行完毕，不用管守护线程

        new Thread(you).start();
        godThread.start();
    }
}

class God implements Runnable{
    @Override
    public void run() {
        while(true){
            System.out.println("God always saves you");
        }
    }
}

class You implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 365; i++) {
            System.out.println("你每天都在开心活着");
        }
        System.out.println("======Goodbye!world!====");
    }
}