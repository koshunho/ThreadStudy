package com.huang.demo;

public class TestState {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(()->{
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread.State state = thread.getState();
        System.out.println(state); //NEW

        thread.start();
        state = thread.getState();
        System.out.println(state); //RUNNABLE

        while(state!=Thread.State.TERMINATED){
            Thread.sleep(500);               //这里指的是主线程sleep
            state = thread.getState();
            System.out.println(state);             //TIME_WAITING -> TERMINATED
        }
    }
}
