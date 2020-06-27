package com.huang.juc.cas;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/*
 * 原子类AtomicInteger 的ABA问题谈谈？原子更新引用知道吗？
 * CAS -> Unsafe(CAS核心类) -> CAS底层思想 -> ABA -> 原子引用更新 -> 如何规避ABA问题
 *
 * ABA问题怎么产生的？
 * CAS算法实现的一个重要前提：需要取出内存中某时刻的数据并在当下时刻比较并交换，那么在这个时间差内会导致数据的变化
 * 比如说一个线程one从内存位置V中取出A，这时候另一个线程two也从内存中取出A，并且线程two进行了一些操作将值变成了B，然后线程two又将V位置的数据变成A。
 * 这时候线程one进行CAS操作发现内存中仍然是A，然后线程one操作成功。
 *
 * 尽管线程one的CAS操作成功，但是不代表这个过程就是没有问题的。
 */

/*
* 要解决ABA问题，我们就需要加一个版本号。类似于乐观锁
* T1 100 1
* T2 100 1 -> 101 2 -> 100 3
*
* 使用AtomicStampedReference！！！
* 使用AtomicStampedReference！！！
* 使用AtomicStampedReference！！！
* 使用AtomicStampedReference！！！*/
public class AtomicReferenceDemo {


    //这里有个坑。。。AtomicReference中，如果泛型是一个包装类，注意对象的引用问题
    //对于Integer var = ？ 在-128 ~ 127之间的赋值，Integer对象是在IntegerCache.cache产生，会复用已有对象
    //这个区间内的Integer值可以直接使用 == 进行判断，但是这个区间之外的所有数据，都会在堆上产生，并不会复用已有对象
    // 这是一个大坑。所以所有包装类的对象之间的值都应该用equals方法判断！！
    static AtomicReference<Integer> atomicReference = new AtomicReference<Integer>(100);

    static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(100,1);

    public static void main(String[] args) {
        // ABAProblem();

        solution();
    }

    private static void ABAProblem(){
        new Thread(()->{
            atomicReference.compareAndSet(100,101);
            atomicReference.compareAndSet(101,100);
        },"T1").start();

        new Thread(()->{
            //暂停1s，保证T1先执行
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(atomicReference.compareAndSet(100, 2019)); //修改成功
            System.out.println(atomicReference.get());
        }, "T2").start();
    }

    private static void solution(){
        new Thread(()->{
            int stamp = atomicStampedReference.getStamp(); //获得版本号
            System.out.println("T1 stamp 01-> " + stamp);

            //暂停线程2s，保证T2获得原始版本号
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            atomicStampedReference.compareAndSet(100,101,
                    atomicStampedReference.getStamp(), atomicStampedReference.getStamp()+1);

            System.out.println("T1 stamp 02-> " + atomicStampedReference.getStamp());

            atomicStampedReference.compareAndSet(101, 100,
                    atomicStampedReference.getStamp(), atomicStampedReference.getStamp()+1);

            System.out.println("T1 stamp 03-> " + atomicStampedReference.getStamp());
        },"T1").start();

        new Thread(()->{
            int stamp = atomicStampedReference.getStamp(); //获得原始版本号
            System.out.println("T2 stamp 01-> " + stamp);

            //暂停3s，保证T1先执行
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean b = atomicStampedReference.compareAndSet(100, 2019, stamp, stamp + 1);
            System.out.println("T2是否修改成功： " + b);
            System.out.println("T2 最新stamp-> " + atomicStampedReference.getStamp());
            System.out.println("T2 当前最新值-> " + atomicStampedReference.getReference());

        },"T2").start();
    }
}
