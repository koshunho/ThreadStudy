# ThreadStudy

Some examples to help understanding thread

#### 注意点
##### 1.在资源类内sleep，和在main中sleep，效果一样吗？

我想用一个线程A调用某个方法 10次，期间不想被其他线程打扰。A执行完之后再让别的线程执行

先来看看这样一个资源类
```java
class Phone{
    public synchronized void SMS(){
        System.out.println(Thread.currentThread().getName() + " 发短信");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

然后是这么调用的
```java
public class TestReentrantLock {
    public static void main(String[] args) {
        Phone phone = new Phone();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                phone.SMS();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"T1").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                phone.SMS();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"T2").start();
    }
}
```

输出结果：

T1 发短信

T1 发短信

T2 发短信

T2 发短信

T2 发短信

T2 发短信

T2 发短信

T2 发短信

T1 发短信

T1 发短信

...

是交织的。

为啥不是T1先输出完毕，然后T2再输出呢？不是说synchronized可以同步吗，而且sleep是不释放锁的

个人理解：因为写的是资源类。比如说A B线程，里面各自循环调用资源类的同步方法A十次，那么我有一个执行资源类的队列，一开始就决定好了，比如说A-B-B-A-B-A-B-A-B-A...

如果不是同步方法的话，那执行资源类的非同步方法，可能就是AB-BA-AB（AB同时执行的意思 - 代表一个时间间隔

所以，如果想实现开头说的那个效果，则应该在资源类内循环。

##### 2.如何排查死锁

①查看JDK目录的bin目录

②使用 `jps -l ` 命令定位进程号

③使用 `jstack 进程号 `找到死锁查看

```
Java stack information for the threads listed above:
===================================================
"Thread-1":
        at com.huang.syn.MakeUp.cosmetic(DeadLock.java:70)
        - waiting to lock <0x00000000d6119350> (a com.huang.syn.LipStick)
        - locked <0x00000000d611b200> (a com.huang.syn.Mirror)
        at com.huang.syn.MakeUp.run(DeadLock.java:49)
"Thread-0":
        at com.huang.syn.MakeUp.cosmetic(DeadLock.java:62)
        - waiting to lock <0x00000000d611b200> (a com.huang.syn.Mirror)
        - locked <0x00000000d6119350> (a com.huang.syn.LipStick)
        at com.huang.syn.MakeUp.run(DeadLock.java:49)

Found 1 deadlock.

```