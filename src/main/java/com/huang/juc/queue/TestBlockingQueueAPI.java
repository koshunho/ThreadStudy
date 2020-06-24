package com.huang.juc.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TestBlockingQueueAPI {
    public static void main(String[] args) throws InterruptedException {
        //test1();

        //test2();

        //test3();

        test4();
    }

    //抛出异常：add(),remove(), element()
    private static void test1(){
        ArrayBlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<>(3);

        System.out.println(blockingQueue.add("a"));
        System.out.println(blockingQueue.add("b"));
        System.out.println(blockingQueue.add("c"));

/*        //java.lang.IllegalStateException: Queue full
        System.out.println(blockingQueue.add("d"));*/

        System.out.println(blockingQueue.element());

        System.out.println(blockingQueue.remove());  //a
        System.out.println(blockingQueue.remove());  //b
        System.out.println(blockingQueue.remove());  //c

/*        //java.util.NoSuchElementException
        System.out.println(blockingQueue.remove());*/
    }

    //返回特殊值：offer(),poll(),peek()
    private static void test2(){
        ArrayBlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<>(3);

        System.out.println(blockingQueue.offer("a")); //true
        System.out.println(blockingQueue.offer("b")); //true
        System.out.println(blockingQueue.offer("c")); //true

        System.out.println(blockingQueue.offer("d")); //false

        System.out.println(blockingQueue.peek());

        System.out.println(blockingQueue.poll());  //a
        System.out.println(blockingQueue.poll());  //b
        System.out.println(blockingQueue.poll());  //c

        System.out.println(blockingQueue.poll()); //null
    }

    //一直阻塞：put(),take(), 没有
    private static void test3() throws InterruptedException {
        ArrayBlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<>(3);

        //一直阻塞
        blockingQueue.put("a");
        blockingQueue.put("b");
        blockingQueue.put("c");
        blockingQueue.put("d"); //会一直等待，阻塞着不往下走


        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
    }

    //超时退出：offer(e, time, unit),poll(time, unit)，没有
    private static void test4() throws InterruptedException {
        ArrayBlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<>(3);

        blockingQueue.offer("a");
        blockingQueue.offer("b");
        blockingQueue.offer("c");
        blockingQueue.offer("d",3L, TimeUnit.SECONDS); //等待3s超时退出

        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll(3l,TimeUnit.SECONDS)); //超过3s停止等待，打印Null
    }
}


