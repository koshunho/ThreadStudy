package com.huang.demo;

public class TestJoin implements Runnable {
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println("VIP线程来惹 "+ i);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TestJoin testJoin = new TestJoin();
        Thread thread = new Thread(testJoin);

        //主线程
        for (int i = 0; i < 1000; i++) {
            if(i == 500){
                //狂神这里写错了。应该start()和join()放在一起
                thread.start();
                thread.join();
            }
            System.out.println("main " + i);
        }
    }
}
