package com.huang.juc;
/*现在有 4 个进程，可以操纵初始值为0的一个变流量。
实现两个线程对该变量+1，两个线程对该变量-1
实现交替10次。*/

/*
原来使用if判断会出错，乱序。

解决方案：永远不要在循环之外调用wait方法。
对于从wait中被notify的进程来说，它在被notify之后还需要重新检查是否符合执行条件。
如果不符合，就必须再次被wait，如果符合才能往下执行。所以：wait方法应该使用循环模式来调用。
*/
/*就生产者和消费者问题来说：
错误情况一：如果有两个生产者A和B，一个消费者C。
当存储空间满了之后，生产者A和B都被wait，进入等待唤醒队列。
当消费者C取走了一个数据后，如果调用了notifyAll（），注意，此处是调用notifyAll（），则生产者线程A和B都将被唤醒，如果此时A和B中的wait不在while循环中而是在if中，则A和B就不。会。再。次。判。断。是否符合执行条件，都将直接执行wait（）之后的程序。
那么如果A放入了一个数据至存储空间，则此时存储空间已经满了；但是B还是会继续往存储空间里放数据，错误便产生了*/
public class TestSpuriousWakeup {
    public static void main(String[] args) {
        Data data = new Data();

        new Thread(()->{ for (int i = 0; i < 10; i++) data.increment(); }, "A").start();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.increment(); }, "B").start();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.decrement(); }, "C").start();
        new Thread(()->{ for (int i = 0; i < 10; i++) data.decrement(); }, "D").start();
    }
}

//资源类
class Data{
    private int num = 0;

    public synchronized void increment(){
        while(num != 0){ //不为0的时候先等着，等着被减。
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        num++;

        System.out.println(Thread.currentThread().getName() + "\t" + num);

        this.notifyAll();
    }

    public synchronized void decrement(){
        while(num == 0){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        num--;

        System.out.println(Thread.currentThread().getName() + "\t" + num);

        this.notifyAll();
    }
}