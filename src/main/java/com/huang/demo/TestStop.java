package com.huang.demo;

public class TestStop implements Runnable{

    private boolean flag = true;

    public void run() {
        int i = 0;
        while(flag){
            System.out.println("running a thread "+ i++);
        }
    }

    public void stop(){
        this.flag = false;
    }

    public static void main(String[] args) {
        TestStop testStop = new TestStop();

        Thread thread = new Thread(testStop);

        thread.setPriority(10);

        thread.start();
        for (int i = 0; i <= 1000; i++) {
            if(i == 999){
                testStop.stop();
                System.out.println("线程该停止了");
            }
            System.out.println("main " + i);
        }
    }
}
