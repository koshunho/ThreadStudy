package com.huang.demo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintCurrentTime {
    public static void main(String[] args) throws InterruptedException {
        Date startTime = new Date(System.currentTimeMillis());

        while(true){
            Thread.sleep(1000);
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(startTime));
            startTime = new Date(System.currentTimeMillis());
        }
    }
}
