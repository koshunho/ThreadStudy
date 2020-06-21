package com.huang.syn;

//生产者消费者模型->利用缓冲区解决：管程法
//生产者,消费者,产品,缓冲区
public class TestPC_Monitor {
    public static void main(String[] args) {
        SynContainer container = new SynContainer();

        new Producer(container).start();
        new Consumer(container).start();
    }
}

class Producer extends Thread{
    SynContainer container;

    public Producer(SynContainer container){
        this.container = container;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            container.push(new Chicken(i));
            System.out.println("生产了第"+i+"只鸡");
        }
    }
}

class Consumer extends Thread{
    SynContainer container;

    public Consumer(SynContainer container){
        this.container = container;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println("消费了--->第"+container.pop().id+"只鸡");
        }
    }
}

class Chicken{
    int id;

    public Chicken(int id) {
        this.id = id;
    }
}

//!!!!!!!!一个实例对象的锁只有一个，如果有几个非静态函数都是synchronized，在某一时刻只有一个线程能调用其中一个函数!!!!
//当一个方法被synchronized修饰后，锁对象为当前方法所属对象，即方法中的this
//对象方法的synchronized修饰，锁为对象自身，也就是this；
//synchronized作用的对象是一个静态方法，则它取得的是类的锁，该类所有的对象同一把锁，所有会阻塞

/*无论synchronized关键字加在方法上还是对象上，如果它作用的对象是非静态的，则它取得的锁是对象；
如果synchronized作用的对象是一个静态方法或一个类，则它取得的锁是对类，该类所有的对象同一把锁。*/

//wait():将当.前.线.程.持有对象的锁交出（允许其他线程持有），并进入等待状态。
//notify():唤醒某一个正在等待的线程（由某一个正在等待的线程获取锁）。
//notifyAll():通知所有正在等待的线程（所有正在等待的线程争夺一个锁），由jvm决定唤醒哪一个。
class SynContainer{
    //容器大小
    Chicken[] arr = new Chicken[10];
    //容器计数器
    int count = 0;

    //生产者放入产品
    public synchronized void push(Chicken chicken) {
        //如果容器满了，需要等待消费者消费
        if(count == arr.length){
            //通知消费者消费，生产等待
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //如果没有满，就需要丢入产品
        arr[count++] = chicken;

        //可以通知消费者消费了
        this.notifyAll();
    }

    //消费者消费产品
    public synchronized Chicken pop()  {
        //判断能否消费
        if(count == 0){
            //等待生产者生产，消费者等待
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //如果可以消费
        Chicken chicken = arr[--count];

        //吃完了，通知生产者生产
        this.notifyAll();
        return chicken;
    }
}