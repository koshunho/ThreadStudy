package com.huang.demo;

public class CountDown {
    public static void countdown(int num) throws InterruptedException {
        while(true){
            Thread.sleep(1000);

            System.out.println(num--);

            if(num < 0){
                break;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        countdown(10);
    }
}
