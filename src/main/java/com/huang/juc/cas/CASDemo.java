package com.huang.juc.cas;

import java.util.concurrent.atomic.AtomicInteger;

// 真实值和期望值相同，就修改成功；真实值和期望值不同，修改失败
/* CAS底层原理？如果知道，谈谈你对Unsafe的理解
---
* 点开AtomicInteger里面的方法，发现都是调用Unsafe类里面的方法
* 比如说atomicInteger.getAndIncrement(); 这里的自增+1是怎么实现的？
*
*     public final int getAndIncrement() {  //分析源码，如何实现的i++安全的问题
        //this 当前对象
        //valueOffset 荡村偏移量，内存地址
        // 1 固定写死
        return unsafe.getAndAddInt(this, valueOffset, 1);
    }
---
*
    public final int getAndAddInt(Object var1, long var2, int var4) {
        int var5;
        do {
            var5 = this.getIntVolatile(var1, var2);
        } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));

        return var5;
    }

    * 到这里就已经无法操作了，而且这个类中的方法大部分是native方法
    * 问题：这个Unsafe类到底是什么？ 在AtomicInteger源码中基本都是它！
    *
    * 1.Unsafe
    * Unsafe是CAS的核心类，由于Java无法直接访问底层系统，需要通过native方法来访问，Unsafe相当于一个后门，基于该类可以直接操作特定内存的数据。
    * Unsafe类存在于sun.misc包钟，其内部方法操作可以像C的指针一样直接操作内存，因为Java中CAS操作依赖于Unsafe类的方法
    *
    * 注意：Unsafe类中的所有方法都是native修饰的，也就是说Unsafe类中的方法都直接调用操作系统底层资源执行相应任务
    *
    * 2.变量valueOffset
    * 表示该变量值再阿内存中的偏移地址，因为Unsafe就是根据内存偏移地址获取数据的
    *
    * 3.变量value用volatile修饰，保证了多线程之间的内存可见性
    *

最后解释CAS是什么？
CAS全程Compare-And-Swap，它是一条CPU并发原语。并发原语并发原语并发原语。
它的功能是判断内存某个位置的值是否为预期值，如果是则更改为新的值，这个过程是原子的。

由于CAS是一种系统原语，原语属于操作系统用语范畴，是由若干条指令组成的，用于完成某个功能的一个过程，
并且原语的执行必须是连续的，在执行过程中不允许被中断，也就是说CAS是一条CPU的原子指令，不会造成所谓的数据不一致问题。

    public final int getAndAddInt(Object var1, long var2, int var4) {
        int var5;
        do {
            // 获取传入对象的地址
            var5 = this.getIntVolatile(var1, var2);

            // 比较并交换，如果var1，var2 ——> 还是原来的var5，就执行内存偏移+1； var5 + var4
        } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));

        return var5;
    }
 */

/*CAS总结：
* 比较当前工作内存中的值 和 主内存中的值，如果相同则执行操作，否则继续比较直到主内存和工作内存中的值一致为止。
*
* CAS应用：
* CAS有3个操作数， 内存值V，旧的预期值A，要修改的更新值B。当预期值A 和 内存值 V 相同时吗，才将内存值V修改为B，否则什么也不做
*
* CAS的缺点：
* 1. 循环时间长开销很大
*    可以看到源码中存在一个do..while操作，如果CAS失败就会一直进行尝试
*
* 2. 只能保证一个共享变量的原子操作
*     当对一个共享变量执行操作时，我们可以使用循环CAS的方法来保证原子操作。
*      但是当对多个共享变量操作时，循环CAS就无法保证操作的原子性，这时候就可以用锁来保证原子性。
*
* 3. 引出来的ABA问题？？？*/
public class CASDemo {
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(100);

        //期待是100，后面改为2020。 所以结果为true(compareAndSet()返回一个boolean值), 2020
        System.out.println(atomicInteger.compareAndSet(100, 2020) + "->" + atomicInteger.get());

        //期待是100，后面改为438。 false(compareAndSet()返回一个boolean值), 2020
        System.out.println(atomicInteger.compareAndSet(100, 438) + "->" + atomicInteger.get());

    }
}
