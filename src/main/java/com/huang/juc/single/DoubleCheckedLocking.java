package com.huang.juc.single;

// 在懒加载中，如果是多线程的就会出问题。所以用DCL双重检验锁来保证线程的安全
public class DoubleCheckedLocking {

    // 重点：这里的 volatile 很重要！
    // 因为下面的 instance = new DoubleCheckedLocking() 并不是一个原子性操作
    // 至少会经过三个步骤：
    // 1.分配对象内存空间
    // 2.执行构造方法初始化对象
    // 3.设置instance指向刚分配的内存地址，此时instance != null
    // 但是，由于指令重排的存在，导致A线程在执行 instance = new DoubleCheckedLocking()的时候，可能先执行了第三步（还没有执行第二步）
    // 此时，线程B又进来了，发现instance已经不为空了，就直接返回了instance，并且后面使用了返回的instance
    // 由于线程A还没有执行第二步，导致此时instance并不完整，可能会有一些意外不到的错误
    // 所以，必须添加volatile防止指令重排！
    private volatile static DoubleCheckedLocking instance;

    private DoubleCheckedLocking() {
        System.out.println(Thread.currentThread().getName() + " OK");
    }

    public static DoubleCheckedLocking getInstance(){
        if(instance == null){
            synchronized (DoubleCheckedLocking.class){  //类锁
                if(instance == null){
                    instance = new DoubleCheckedLocking();
                }
            }
        }
        return instance;
    }
}
