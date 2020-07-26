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

### 3.相关面试题
##### 1.线程状态？

 - NEW:尚未启动
 - RUNNABLE:正在执行中
 - BLOCKED:阻塞的
 - WAITING:永久等待状态
 - TIME_WAITING:等待指定时间重新被唤醒的状态
 - TERMINATED:执行完成
  
简而言之：新建、运行、阻塞、等待、永久等待、消亡
   
##### 2.线程池状态？
 - RUNNING: 正常的状态，接受新的任务，处理等待队列中的任务
 - SHUTDOWN:不接受新的任务提交，但会继续处理等待队列中的任务
 - STOP:不接受新的任务提交，不再处理等待队列中的任务，中断正在执行的任务
 - TIDYING:所有任务都销毁了，workCount为0，线程池的状态在转换为TIDYING状态时，会执行钩子方法terminated()
 - TERMINATED:terminated()方法结束后，线程池的状态就会变成这个

##### 3.线程池中 submit() 和 execute() 有什么区别？

execute()：只能执行Runnable类型的任务

submit():可以执行Runnable和Callable类型的任务。**submit()执行后有返回值**

##### 4.ThreadLocal是什么？
看TestThreadLocal

##### 5.说一下synchronized底层实现原理？
synchronized是由一对 **monitorenter/monitorexit** 指令实现的，monitor对象是同步的基本实现单元。

##### 6. sleep()和wait()的区别
1. 来自不同的类：sleep()来自Thread，wait()来自Object
2. 锁：sleep()不释放锁（抱着锁睡），wait()释放锁
3. 使用范围：wait()必须在同步代码块，sleep()可以在任何地方睡（比如main线程就直接能睡，让main线程暂时停一下）
4. 异常：wait(~~)不需要~~（错！**需要**）捕获异常，sleep()必须捕获异常

##### 7.synchronized 和 Lock的区别
1. synchronized是内置Java关键字，Lock是一个Java类（是一个接口，实现类有3个，一个是ReentrantLock，另两个是ReentrantReadWriteLock类中的两个静态内部类ReadLock和WriteLock。）--> synchronized基于JVM，Lock基于API
   
2. synchronized无法判断是否获取锁的状态，Lock可以判断是否获取到锁(lock.tryLock()方法)
```java
Lock lock = ...;
if(lock.tryLock()) {
     try{
         //处理任务
     }catch(Exception ex){
         
     }finally{
         lock.unlock();   //释放锁
     } 
}else {
    //如果不能获取锁，则直接做其他事情
}
```
tryLock()方法是有返回值的，它表示用来尝试获取锁，如果获取成功，则返回true，如果获取失败（即锁已被其他线程获取），则返回false，也就说这个方法无论如何都会立即返回。在拿不到锁时不会一直在那等待。[Lock](https://www.cnblogs.com/dolphin0520/p/3923167.html)

3.  synchronized会自动释放锁（a线程执行完同步代码会释放锁；b线程执行过程中发生异常会释放锁）；Lock需要在finally手动释放锁(lock.unlock())

4. 用synchronized的两个线程：线程1和线程2，如果当前线程1获得锁，线程2等待。如果线程1阻塞，线程2就会一直傻傻地等………………Lock锁不一定会等下去，如果尝试获取不到锁，线程可以不用一直等待就结束了 --> 如tryLock(long time, TimeUnit unit)

    tryLock(long time, TimeUnit unit)方法和tryLock()方法是类似的，只不过区别在于这个方法在拿不到锁时会等待一定的时间，在时间期限之内如果还拿不到锁，就返回false。如果如果一开始拿到锁或者在等待期间内拿到了锁，则返回true。
	
5. Lock可以是公平锁（默认是非公平锁），synchronized只能是非公平锁

6. Lock锁适合大量同步的代码的同步问题，synchronized适合代码少量的同步问题

7. Lock可以**绑定多个Condition**，精确唤醒某一个线程；synchronized只能随机唤醒一个线程 or 唤醒全部线程

```java
Lock lock = new ReentrantLock();
Condition condition = lock.newCondition();

condition.await(); //相当于wait()
condition.signalAll(); //相当于notifyAll()

public void increment() throws InterruptedException{
  lock.lock();
  try{
    //业务代码
   }catch(Exception e){
      e.printStackTrace();
	  } finally{
	       lock.unlock();
	  }
  }
```

##### 8.List是线程不安全的，如何解决？

用N个线程往一个ArrayList中add，会抛出 并发修改异常（ConcurrentModificationException）

原因：add()没有加锁

解决方案：换一个集合类
1. List<String> list = new Vector<>();  //JDK1.0 就存在了，ArrayList JDK 1.2才有
2. List<String> list = Collections.synchronizedList(new ArrayList<>()); //用集合类去转
3. List<String> list = new CopyOnWriteArrayList<>();

CopyOnWrite --> 写入时复制（简称COW）。**读写分离**，**写时复制出一个新的数组**，完成插入、修改、移除操作后将新数组赋值给array

**CopyOnWriteArrayList为什么并发性能比Vector好**？（锁优化问题！非常重要）

Vector的增删改查方法都加了synchronized，保证同步，但是每个方法执行的时候都要去获取锁，性能大大下降。而CopyOnWriteArrayList只是在**修改操作上加锁**，**读不加锁**，在读方面的性能就好于Vector，CopyOnWriteArrayList支持读多写少的并发情况。

##### 9.HashSet是线程不安全的，如何解决？
用N个线程往一个HashSet中add，会抛出 并发修改异常（ConcurrentModificationException）

原因：add()没有加锁

解决方案：换一个集合类
1. Set<String> set = Collections.synchronizedSet(new HashSet<>()); //用集合类去转
2. Set<String> set = new CopyOnWriteArraySet();

##### 10.HashMap是线程不安全的，如何解决？
用N个线程往一个HashMap中put，会抛出 并发修改异常（ConcurrentModificationException）

原因：add()没有加锁

解决方案：换一个集合类
1. Map<String,String> map = Collections.synchronizedMap(new HashMap<>()); //用集合类去包装
2. Map<String,String> map = new ConcurrentHashMap();

`ConcurrentHashMap`
`ConcurrentHashMap`
`ConcurrentHashMap`
`ConcurrentHashMap`
`ConcurrentHashMap`
`ConcurrentHashMap`
`ConcurrentHashMap`

名字换了注意！！！！！不是CopyOnwriteXXX了！！！！

##### 11.Callable和Runnable有什么区别？

- Callable有返回值(Callable`<V>`，**泛型V就是返回值的类型**！)，Runnable没有
- Callable是实现call()方法，Runnable是实现run()方法

注意！启动一个线程，只有start()方法!!!!!!!!

如何启动Callable呢？

Thread的构造函数里就有Runnable，他们能直接联系。但是Callable咋办？它没有和Thread有直接联系。
```java
    public Thread(Runnable target, String name) {
        init(null, target, name, 0);
    }
```

于是这时候就想到适配器模式，有什么适配器实现了Runnable又可以调用Callable呢？

![RunnableFuture](http://qcorkht4q.bkt.clouddn.com/blog1595433284932.png)

RunnableFuture接口继承了Runnable

![FutureTask](http://qcorkht4q.bkt.clouddn.com/blog1595433338875.png)

RunnableFuture有一个实现类FutureTask。**说明FutureTask就是一个Runnable**
**说明FutureTask就是一个Runnable**
**说明FutureTask就是一个Runnable**

```java
public class FutureTask<V> implements RunnableFuture<V> {
   ...
    public FutureTask(Callable<V> callable) {
        if (callable == null)
            throw new NullPointerException();
        this.callable = callable;
        this.state = NEW;       // ensure visibility of callable
    }
  ...
  
      public FutureTask(Runnable runnable, V result) {
        this.callable = Executors.callable(runnable, result);
        this.state = NEW;       // ensure visibility of callable
    }
}
```
FutureTask又可以和Callable联系上。所以

```java
new Thread(new Runnable()).start();
new Thread(new FutureTask<V>()).start();
new Thread(new FutureTask<V>(Callable)).start();
```

非常好的体现了适配器模式！


```java
public class TestCallableByFutureWork {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MyThread myThread = new MyThread();

        FutureTask<Integer> futureTask = new FutureTask<>(myThread);

        /*
        输出结果：
        你爹's call()
        5464
        */
        //只打印出一个 你爹's call() 。因为结果会被缓存！
        new Thread(futureTask,"你爹").start();
        new Thread(futureTask,"你妈").start();

        Integer i = futureTask.get();

        System.out.println(i);
    }
}

class MyThread implements Callable<Integer>{
    @Override
    public Integer call() throws Exception {
        System.out.println(Thread.currentThread().getName() + "'s call()");
        return 5464;
    }
}
```
**如何获取Callable的返回值**？

通过futureTask.get()

##### 12.如何确保若干个线程都执行完毕才执行接下来的逻辑？

使用CountDownLatch

就是使用两个方法`countDown()` 和 `await()`。当一个线程调用调用await()方法时，这个线程会阻塞。其他线程会调用countDown()方法将计数器-1（调用countDown方法的线程不会阻塞），当计数器变为0，await()方法阻塞的线程会被唤醒，继续执行。


###### 让单个线程等待：多个线程(任务)完成后，进行汇总合并
 
假设一间教室，里面有6个学生，必须等所有学生出门后才能关门！

```java
//线程执行速度不一样，有的快有的慢，部分业务需要等所有线程结束后才能处理后面的逻辑。所以就用CountdownLatch
//countDownLatch.countDown() 和 countDownLatch.await()配套使用
public class TestCountDownLatch {

    //需求：有6个人要从一个房间出门。六个人走后才能关门
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(6); //总数是6，必须要执行任务的时候才使用

        for (int i = 1; i <= 6; i++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName() + "left");
                countDownLatch.countDown(); //数量-1
            }, String.valueOf(i)).start();
        }

        countDownLatch.await(); //等待计数器归零，然后再向下执行！！！！！在变为0之前会一直阻塞。

        System.out.println("All people already left! Close door");
    }
}

```


###### 让多个线程等待：模拟并发，让并发线程一起执行
为了模拟高并发，让一组线程在指定时刻(秒杀时间)执行抢购，这些线程在准备就绪后，进行等待(CountDownLatch.await())，直到秒杀时刻的到来，然后一拥而上；
这也是本地测试接口并发的一个简易实现。

在这个场景中，CountDownLatch充当的是一个发令枪的角色；
就像田径赛跑时，运动员会在起跑线做准备动作，等到发令枪一声响，运动员就会奋力奔跑。和上面的秒杀场景类似，代码实现如下：

```java
CountDownLatch countDownLatch = new CountDownLatch(1);
for (int i = 0; i < 5; i++) {
    new Thread(() -> {
        try {
            //准备完毕……运动员都阻塞在这，等待号令
            countDownLatch.await();
            String parter = "【" + Thread.currentThread().getName() + "】";
            System.out.println(parter + "开始执行……");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }).start();
}

Thread.sleep(2000);// 裁判准备发令
countDownLatch.countDown();// 发令枪：执行发令
```

##### 13.如何集齐7颗龙珠 才能 召唤神龙？

用CyclicBarrier

```java
public CyclicBarrier(int parties, Runnable barrierAction) {...}
```

要等待parties个线程阻塞(注意，我没说 要等这些线程结束)，才执行一个barrierAction线程。

注意`await()`方法。CyclicBarrier的await()方法是用来计数的，await之后该线程会阻塞，直到神龙召唤后才执行后面的。

```java
public class TestCyclicBarrier {
    /*集齐7颗龙珠召唤神龙 -> 等待7个线程执行完毕后，然后才执行卸载CyclicBarrier的线程*/
    //cyclicBarrier.await(); 等待
    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7, () -> System.out.println("召唤神龙成功！"));

        for (int i = 1; i <= 7; i++) {
            final int temp = i; //Lambda中取不到i，所以用一个final的变量来存储i的值
            new Thread(()->{
                System.out.println("收集到了第" + temp + "颗龙珠");
                try {
                    cyclicBarrier.await(); //等待
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("第" + temp + "线程结束了");
            }).start();
        }
    }
}
```
##### 14.有6个车，只有3个车位怎么办？

抢车位，Semaphore信号量

两个方法 `acquire()` `release()`

acquire():
- 当一个线程调用acquire()，它要么成功获取信号量（信号量数量-1）
- 要么一直等下去，直到有线程释放信号量or超时

release():
-  会将信号量的数量+1，然后唤醒等待的线程

感觉很像blockingQueue

构造函数必须传入**信号量的数量**！
```java
//Semaphore就像内部维护了一个计数器，每个线程计数开始+1，maxまで会阻塞接下来的这个线程，直到有空位释放再进入。
//作用：多个共享资源互斥的使用。并发限流，控制最大的线程数
public class TestSemaphore {
    public static void main(String[] args) {
        //有3个停车位
        Semaphore semaphore = new Semaphore(3);

        //有6个车。想抢3个停车位
        for (int i = 1; i <= 6; i++) {
            new Thread(()->{
                try {
                    semaphore.acquire();    //得到

                    System.out.println(Thread.currentThread().getName() + "获得了车位");

                    TimeUnit.SECONDS.sleep(2);

                    System.out.println(Thread.currentThread().getName() + "离开车位");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();  //释放
                }
            }, String.valueOf(i)).start();
        }
    }
}
```

##### 15.读写锁有几把锁？2把吗？

不是两把，读写锁就是一把锁！读 和 写 是互斥的！

之前认为为啥要弄一个读写锁，既然是为了让写操作只有一个线程写，读操作多个线程同时读，**那么只对写方法加锁，读方法不加锁不就行了**？为什么要给读操作加锁？？？

锁降级中读锁的获取是否有必要呢？答案是必要的。主要是为了**保证数据的可见性**，如果当前线程不获取读锁而是直接释放写锁，假设此刻另一个线程T获取了写锁了并修改了数据，那么当前线程无法感知到线程T的数据更新。如果当前线程获取读锁，即遵循锁降级的步骤，则线程T将会被阻塞，直到当前线程使用数据并释放读锁之后，线程T才能获取写锁进行数据更新。


其实加读锁和加写锁这两个说法可能会造成误导，让人误以为是有两把锁，其实读写锁是一个锁。所谓加读锁和加写锁，准确的说法可能是**给读写锁加读模式的锁定和加写模式的锁定**。

---
- 当读写锁被加了写锁时，其他线程对该锁加读锁或者写锁都会阻塞。 
- 当读写锁被加了读锁时，其他线程对该锁加写锁会阻塞，加读锁会成功。
---
上面两段话非常重要！


```java
public class TestReadWriteLock {
    public static void main(String[] args) {
        MyCache myCache = new MyCache();

        //写
        for (int i = 1; i <= 5; i++) {
            final int temp = i; //注意final
            new Thread(()->{
                myCache.put(String.valueOf(temp), Thread.currentThread().getName());
            }, String.valueOf(i)).start();
        }

        //读
        for (int i = 1; i <= 5; i++) {
            final int temp = i; //注意final
            new Thread(()->{
                myCache.get(String.valueOf(temp));
            }, String.valueOf(i)).start();
        }
    }
}

class MyCache{
    private volatile Map<String, Object> map = new HashMap<>();

    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public void put(String key, Object value){

        readWriteLock.writeLock().lock();

        try {
            System.out.println(Thread.currentThread().getName() + " 写入" + key);
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + " 写入成功！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void get(String key){
        readWriteLock.readLock().lock();

        try {
            System.out.println(Thread.currentThread().getName() + " 读取" + key);
            Object value = map.get(key);
            System.out.println(Thread.currentThread().getName() + " 读取成功！结果：" + value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.readLock().unlock();
        }

    }
}

/*
可以看到，写操作就是 写入 写入成功，没有插队的。
测试结果：
        1 写入1
        1 写入成功！
        2 写入2
        2 写入成功！
        4 写入4
        4 写入成功！
        5 写入5
        5 写入成功！
        3 写入3
        3 写入成功！
        5 读取5
        5 读取成功！结果：5
        3 读取3
        3 读取成功！结果：3
        2 读取2
        2 读取成功！结果：2
        1 读取1
        4 读取4
        1 读取成功！结果：1
        4 读取成功！结果：4*/
```
##### 16.创建线程池有多少种方式？

三大方法、7大参数、4种策略

池化技术：提前保存大量的资源，以备不时之需。在机器资源有限的情况下，使用池化技术可以大大提高资源的利用率，提升性能。

线程池：线程复用，控制最大并发数，管理线程

1. 降低资源消耗，通过复用已创建的线程降低线程创建和销毁造成的消耗
2. 提高效应速度，当任务到达时，任务不需要等待线程创建就可以立即执行
3. 提供线程的可管理性，线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性。使用线程池可以进行统一分配，调优和监控


往线程池中提交线程的时候其实有两种方法，一种是`execute`另外一种是`submit`。

![execute](http://qcorkht4q.bkt.clouddn.com/blog1595578698685.png)

![submit](http://qcorkht4q.bkt.clouddn.com/blog1595578620156.png)

`execute`: execute提交的方式只能提交一个Runnable的对象，且该方法的返回值是void，也即是提交后如果线程运行后，和主线程就脱离了关系了，当然可以设置一些变量来获取到线程的运行结果。并且当线程的执行过程中抛出了异常通常来说主线程也无法获取到异常的信息的，只有通过ThreadFactory主动设置线程的异常处理类才能感知到提交的线程中的异常信息。

`submit`提交的方式有如下三种情况：
1.  `<T>` Future`<T>` submit(Callable`<T>` task);
   这种提交的方式是提交一个实现了Callable接口的对象。**这种提交的方式会返回一个Future对象，这个Future对象代表这线程的执行结果**。当主线程调用Future的get方法的时候会获取到从线程中返回的结果数据。如果在线程的执行过程中发生了异常，get会获取到异常的信息。

2. Future`<?>` submit(Runnable task);
   可以提交一个Runable接口的对象，这样当调用get方法的时候，如果线程执行成功会直接返回null，如果线程执行异常会返回异常的信息
   
3. `<T>` Future`<T>` submit(Runnable task, T result);
    除了task之外还有一个result对象，当线程**正常结束**的时候调用Future的get方法会返回result对象，当线程**抛出异常**的时候会获取到对应的异常的信息。


###### 三大方法

`Executors.newSingleThreadExecutor()`
`Executors.newFixedThreadPool(int n)`
`Executors.newCachedThreadPool()`

都是Executors下的方法，线程池的类型是`ExecutorService`
线程池的类型是`ExecutorService`
线程池的类型是`ExecutorService`
线程池的类型是`ExecutorService`
线程池的类型是`ExecutorService`
线程池的类型是`ExecutorService`

```java
public class TestCreateThreadPoolByExecutors {
    public static void main(String[] args) {
        TestNewSingleThreadExecutor();

        // TestNewFixedThreadPool();

        // TestNewCachedThreadPool();
    }

    // 只有一个线程
/*    输出结果：
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务*/
    private static void TestNewSingleThreadExecutor(){
        ExecutorService threadPool = Executors.newSingleThreadExecutor();

        try {
            for (int i = 1; i <= 10; i++) {
                //模拟有10个顾客来办理业务，池子中只有1个工作人员受理
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName() + " 办理业务");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown(); //用完要关闭
        }
    }

    //执行长期任务性能好，创建一个线程池，一个池中有N个固定的线程，有固定线程数的线程
/*  输出结果：
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-2 办理业务
    pool-1-thread-3 办理业务
    pool-1-thread-4 办理业务
    pool-1-thread-5 办理业务*/
    private static void TestNewFixedThreadPool(){
        //池子大小 5
        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        try {
            //模拟有10个顾客来办理业务，池子中只有5个工作人员受理
            for (int i = 1; i <= 10; i++) {
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName() + " 办理业务");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    //执行很多短期异步任务，线程池根据需要创建新线程，但在 先构建的线程可用时 将 重用他们。遇强则强遇弱则弱
    /*  输出结果：
    pool-1-thread-1 办理业务
    pool-1-thread-1 办理业务
    pool-1-thread-2 办理业务
    pool-1-thread-3 办理业务
    pool-1-thread-1 办理业务
    ....
    pool-1-thread-34 办理业务
    pool-1-thread-35 办理业务
    pool-1-thread-36 办理业务

    Process finished with exit code 0*/
    private static void TestNewCachedThreadPool(){
        //一池N线程，可扩容伸缩
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try {
            //模拟有10个顾客来办理业务，池子中有N个工作人员受理
            for (int i = 1; i <= 50; i++) {
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName() + " 办理业务");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}
```

###### ThreadPoolExecutor 七大参数
查看三大方法的底层源码，都是调用了 `new ThreadPoolExecutor(七大参数)`
`ThreadPoolExecutor`
`ThreadPoolExecutor`
`ThreadPoolExecutor`
`ThreadPoolExecutor`
`ThreadPoolExecutor`
```java
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) {
        if (corePoolSize < 0 ||
            maximumPoolSize <= 0 ||
            maximumPoolSize < corePoolSize ||
            keepAliveTime < 0)
            throw new IllegalArgumentException();
        if (workQueue == null || threadFactory == null || handler == null)
            throw new NullPointerException();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }
```
`corePoolSize`：核心线程数。当线程池中的数目到达corePoolsSize后，会把到达的任务放到缓存队列(BlockingQueue)当中
`maximumPoolSize`:最大线程数。表明线程中最多能够创建的线程数量
`keepAliveTime`:**空闲线程**保留时间（就是 最大线程 - 核心线程 = 空闲线程）
`TimeUnit`:空闲线程的保留时间单位
`BlockingQueue<Runnable>`:阻塞队列，存储等待执行的任务。参数有ArrayBlockingQueue、LinkedBlockingQueue、SynchronousQueue可选
`threadFactory`:线程工厂，用来创建线程，默认即可
`RejectedExecutionHandler`:队列已满，而且任务量大于最大线程的异常处理策略
```java
ThreadPoolExecutor.AbortPolicy //丢弃任务并抛出RejectedExecutionException异常。 

ThreadPoolExecutor.DiscardPolic  //丢弃任务，但是不抛出异常。

ThreadPoolExecutor.DiscardOldestPolicy //丢弃队列最前面的任务，然后重新尝试执行任务 （重复此过程） 

ThreadPoolExecutor.CallerRunsPolicy //由调用线程处理该任务
```

###### ThreadPoolExecutor 原理
![流程](http://qcorkht4q.bkt.clouddn.com/ThreadPoolExecutor.png)

举例：8个人进银行办理业务
1. 1~2人被受理（核心大小core） 
2. 3~5人进入队列（Queue） 
3. 6~8人到最大线程池（扩容大小max） 
4. 再有人进来就要被拒绝策略了

进银行。假设银行窗口有5个，平时只开两个。候客区有3个座位。

 - 假设这时候来了5个人
   有2个人直接去办理业务，剩下3个人在候客区等。
   
- 假设有这时候来了6 ~ 8个人
 有2个人直接去办理业务，这时候发现候客区人都超出了，赶紧叫平时不开的那3个窗口营业！于是候客区有3个人去平时不开的那3个窗口办理业务。此时候客区的人 <= 3。 等候客区等待的客人都办理完之后，平时不开的那3个窗口看没事了，经过keepAliveTime时间它们又休息了。所以线程池的所有任务完成后，它最终会收缩到corePoolSize的大小

- 假设这时候来了 >= 9个人
  第9个开始执行拒绝策略，不接客了886
  
 ###### 在工作中三种创建线程池的方法哪个用得比较多？
 一个都不用，我们工作中只用`ThreadPoolExecutor`自定义的
 
 Executor中JDK都给你提供了，为什么不用？
 
 ![为什么不用Executors](http://qcorkht4q.bkt.clouddn.com/%E4%B8%8D%E5%85%81%E8%AE%B8%E4%BD%BF%E7%94%A8Executors.png)
 
 ###### 线程是否越多越好？
 
 要看是`CPU密集型程序`还是`IO密集型程序`
 
 一个计算为主的程序（CPU密集型程序）。多线程跑的时候，可以充分利用起所有的cpu核心，比如说4个核心的cpu,开4个线程的时候，可以同时跑4个线程的运算任务，此时是最大效率。
但是如果线程远远超出cpu核心数量 反而会使得任务效率下降，因为频繁的切换线程也是要消耗时间的。因此对于cpu密集型的任务来说，线程数等于cpu数是最好的了。
```java
int processors = Runtime.getRuntime().availableProcessors(); //获得CPU的内核数
```

如果是一个磁盘或网络为主的程序（IO密集型）。一个线程处在IO等待的时候，另一个线程还可以在CPU里面跑，有时候CPU闲着没事干，所有的线程都在等着IO，这时候他们就是同时的了，而单线程的话此时还是在一个一个等待的。我们都知道IO的速度比起CPU来是慢到令人发指的。所以开多线程，比方说多线程网络传输，多线程往不同的目录写文件，等等。此时线程数等于IO任务数是最佳的

##### 17.请按照给出数据，找出同时满足以下条件的用户

流式计算。**计算交给流**，效率是最快的！

特别注意`map()`，这个时候泛型可能已经发生了转换！后面的操作都是针对转换后的泛型的了！

最重要一定死死记住，new 函数型接口 用lambda表示`() -> {}`。{}里面注意要不要写`return  xxx;`!!!
`() -> {}` {}里面注意要不要写`return  xxx;`
`() -> {}` {}里面注意要不要写`return  xxx;`
`() -> {}` {}里面注意要不要写`return  xxx;`
`() -> {}` {}里面注意要不要写`return  xxx;`
`() -> {}` {}里面注意要不要写`return  xxx;`
`() -> {}` {}里面注意要不要写`return  xxx;`
`() -> {}` {}里面注意要不要写`return  xxx;`
`() -> {}` {}里面注意要不要写`return  xxx;`
`() -> {}`
`() -> {}`
`() -> {}`
`() -> {}`
`() -> {}`
`() -> {}`
`() -> {}`
`() -> {}`
`() -> {}`

举例，Arrays.sort(arr, (o1, o2) -> {return o2 - o1;}); 

再简化！Arrays.sort(arr, (o1, o2)-> o2 - o1);

return xxx; 的`;`千万不要忘记呀!!!!!只要去掉{}才可以不写`;`!!!!!!!
return xxx; 的`;`千万不要忘记呀!!!!!只要去掉{}才可以不写`;`!!!!!!!
return xxx; 的`;`千万不要忘记呀!!!!!只要去掉{}才可以不写`;`!!!!!!!
return xxx; 的`;`千万不要忘记呀!!!!!只要去掉{}才可以不写`;`!!!!!!!

Function的泛型`<T,R>`，T是参数类型，R是返回值！符合逻辑 先输出，再返回
```java
        //看到这里应该 new 函数型接口 ---替换---->   () ->{}
        Function<Integer[],String> function = (i) ->{
            Arrays.sort(i);
            return "Sort Finished";};

        String apply = function.apply(arr);
        System.out.println(apply);

        for (Integer integer : arr) {
            System.out.println(integer);
        }
```

```java
public class TestFunction {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class User{
        private int id;

        private String username;

        private int age;
    }

    /*
     * 题目：请按照给出数据，找出同时满足以下条件的用户
     * 也即以下条件：
     * 1、全部满足偶数ID
     * 2、年龄大于24
     * 3、用户名转为大写
     * 4、用户名字母倒排序
     * 5、只输出一个用户名字 limit
     **/
    public static void main(String[] args) {
        User u1 = new User(11, "a", 23);
        User u2 = new User(12, "b", 24);
        User u3 = new User(13, "c", 22);
        User u4 = new User(14, "d", 28);
        User u5 = new User(16, "e", 26);

        List<User> users = Arrays.asList(u1, u2, u3, u4, u5);

        users.stream().
                filter(u-> u.getId() % 2 == 0).
                filter(u-> u.getAge() > 24).
                map(u->u.getUsername().toUpperCase()). //注意这里！ 泛型已经从User -> String
                sorted((name1, name2) -> {return name2.compareTo(name1);}).
                limit(1).
                forEach(System.out::println);
    }
}
```

##### 18.从0加到20亿，可以用什么方法？

###### 数学
高斯求和公式

###### for循环

###### ForkJoin

Fork: 任务切分
Join: 任务合并

ForkJoin有一个`工作窃取`的概念。就是一个工作线程下会维护一个包含多个子任务的**双端队列**。而对于每个工作线程来说，会从头部到尾部依次执行任务。这时，总会有一些线程执行的速度较快，很快就把所有任务消耗完了。那这个时候怎么办呢，总不能空等着吧，多浪费资源。
**双端队列**
**双端队列**
**双端队列**
**双端队列**
**双端队列**
**双端队列**

工作窃取（work-stealing）算法是指某个线程从其他队列里**窃取任务**来执行。

![工作窃取](http://qcorkht4q.bkt.clouddn.com/blog1595523139904.png)

简单理解，就是能者多劳！

核心类：`ForkJoinPool` `ForkJoinTask`

`ForkJoinPool`
WorkQueue是一个ForkJoinPool中的内部类，它是线程池中线程的工作队列的一个封装，支持任务窃取。

什么叫线程的任务窃取呢？就是说你和你的一个伙伴一起吃水果，你的那份吃完了，他那份没吃完，那你就偷偷的拿了他的一些水果吃了。存在执行2个任务的子线程，这里要讲成存在A,B两个WorkQueue在执行任务，A的任务执行完了，B的任务没执行完，那么A的WorkQueue就从B的 WorkQueue的ForkJoinTask数组中拿走了一部分尾部的任务来执行，可以合理的提高运行和计算效率。

每个线程都有一个WorkQueue，而WorkQueue中有执行任务的线程（ForkJoinWorkerThread owner），还有这个线程需要处理的任务（ForkJoinTask<?>[] array）。那么这个新提交的任务就是加到array中。

**就是每一个ForkJoinTask要放到ForkJoinPool去执行**！

`ForkJoinTask`
ForkJoinTask代表运行在ForkJoinPool中的任务。

主要方法：
- `fork()` 在当前线程运行的线程池中安排一个异步执行。简单的理解就是再创建一个子任务。
- `join()` 当任务完成的时候返回计算结果。
- `invoke()` 开始执行任务，如果必要，等待计算完成。

子类： Recursive ：递归
- `RecursiveAction`一个递归无结果的ForkJoinTask（没有返回值）
- `RecursiveTask` 一个递归有结果的ForkJoinTask（有返回值）

先写一个ForkJoinTask。RecursiveTask`<V>`有个一个泛型，这就是返回值类型，通过`get()`方法得到返回值。
```java
public class MyTask extends RecursiveTask<Long> {
    private Long start;

    private Long end;

    public static final Long threshold = 1000L;

    public MyTask(Long start, Long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        Long temp = end - start;
        if(temp <= threshold){
            Long sum = 0L;

            for (Long i = start; i <= end; i++) {
                sum += i;
            }

            return sum;
        }else{
            Long middle = start + (end - start) / 2;

            MyTask left = new MyTask(start, middle);
            left.fork();

            MyTask right = new MyTask(middle + 1, end);
            right.fork();

            return left.join()+right.join();
        }
    }
}
```

放到ForkJoinPool执行
```java
public class Test {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
	// invoke = 2000000001000000000 time: 12742
        test1();
   
   // invoke = 2000000001000000000 time: 14404
        test2();
    }

    private static void test1() throws ExecutionException, InterruptedException {
        long l = System.currentTimeMillis();

        //实现ForkJoin 就必须有 ForkJoinPool的支持
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        MyTask myTask = new MyTask(0L, 20_0000_0000L);

        ForkJoinTask<Long> submit = forkJoinPool.submit(myTask);

        Long aLong = submit.get();

        Long l1 = System.currentTimeMillis();

        System.out.println("invoke = " + aLong +" time: " + (l1-l));
    }

    private static void test2(){
        Long start = 0L;

        Long end = 20_0000_0000l;

        Long sum = 0l;

        long l = System.currentTimeMillis();

        for (Long i = start; i <= end; i++) {
            sum += i;
        }

        Long l1 = System.currentTimeMillis();

        System.out.println("invoke = " + sum +" time: " + (l1-l));
    }
}
```

###### 流式计算
之前说过，**计算就交给流**，速度是最快的！
```java
    //invoke = 2000000001000000000 time: 708
    private static void test3(){
        long l = System.currentTimeMillis();
        
		//range () 开区间， rangeClosed (] 闭区间
		//parallel()：并行流
		//sum()：求和
        long sum = LongStream.rangeClosed(0, 20_0000_0000L).parallel().sum();

        Long l1 = System.currentTimeMillis();

        System.out.println("invoke = " + sum +" time: " + (l1-l));
    }
```

效率是ForkJoin和普通线程的十倍以上

##### 19.异步调用
![](http://qcorkht4q.bkt.clouddn.com/blog1595585325363.png)

当我们需要调用一个函数方法时。如果这个函数执行很慢,那么我们就要进行等待。但有时候,我们可能并不急着要结果。
因此,我们可以让被调用者立即返回,让他在后台慢慢处理这个请求。对于调用者来说,则可以先处理一些其他任务,在真正需要数据的场合再去尝试获取需要的数据。

为了让程序更加高效，让CPU最大效率的工作，我们会采用异步编程。首先想到的是开启一个新的线程去做某项工作。再进一步，为了让新线程可以返回一个值，告诉主线程事情做完了，于是乎Future出现了。然而Future提供的方式是主线程主动问询新线程，要是有个回调函数就爽了。所以，为了满足Future的某些遗憾，CompletableFuture出现了。

举一个生活上的例子，假如我们需要出去旅游，需要完成三个任务：

- 任务一：订购航班
- 任务二：订购酒店
- 任务三：订购租车服务

很显然任务一和任务二没有相关性，可以异步执行。但是任务三必须等待任务一与任务二结束之后，才能订购租车服务。

为了使任务三执行时能获取到任务一与任务二执行结果，我们还需要借助 CountDownLatch 。

```java
public class TestBook {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        CountDownLatch count = new CountDownLatch(2);

        //任务1 订购航班
        Future<String> orderAirplane = pool.submit(() -> {
            System.out.println("查询航班");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("订购航班");
            count.countDown();
            return "航班号";
        });

        //任务2 订购酒店
        Future<String> orderHotel = pool.submit(() -> {
            System.out.println("查询酒店");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("订购酒店");
            count.countDown();
            return "酒店";
        });

        count.await();

        Future<String> orderCar = pool.submit(() -> {
            System.out.println("根据航班加酒店订购租车服务");
            try {
                TimeUnit.SECONDS.sleep(6);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "租车信息";
        });

        String s = orderCar.get();

        System.out.println(s);

        pool.shutdown();
    }
}
```

###### CompletableFuture
[详解](https://juejin.im/post/5e6588fc6fb9a07cbe347747)

对比 `Future<V>`，`CompletableFuture `优点在于：
- **不需要手工分配线程**，JDK 自动分配
- 代码语义清晰，**异步任务**链式调用
- 支持**编排**异步任务

可以根据**时序**编排任务！
- 串行执行关系
- 并行执行关系
- AND 汇聚关系
- OR 汇聚关系

**时序**
**时序**
**时序**
**时序**
**时序**
**时序**
**时序**
**时序**
**时序**
**时序**
**时序**
**时序**

1. 串行执行
   任务串行执行，下一个任务必须等待上一个任务完成才可以继续执行
   
   ![串行关系](http://qcorkht4q.bkt.clouddn.com/blog1595583109123.png)
   
   ![四组接口](http://qcorkht4q.bkt.clouddn.com/blog1595583228967.png)
   
2. AND 汇聚关系
   AND 汇聚关系代表所有任务完成之后，才能进行下一个任务。

   ![AND](http://qcorkht4q.bkt.clouddn.com/blog1595583397197.png)
   
   ![四组接口](http://qcorkht4q.bkt.clouddn.com/blog1595583419320.png)
   
3. OR 汇聚关系
   OR 汇聚关系代表只要多个任务中任一任务完成，就可以接着接着执行下一任务。
   
   ![四组接口](http://qcorkht4q.bkt.clouddn.com/blog1595583471824.png)
   
   ..........

```java
public class TestBookByCompletableFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //任务1 查询航班
        CompletableFuture<String> orderAirplane = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询航班");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("订购航班");
            return "航班号";
        });

        //任务2 查询酒店
        CompletableFuture<String> orderHotel = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询酒店");
            try {
                TimeUnit.SECONDS.sleep(6);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("订购酒店");
            return "酒店信息";
        });

        CompletableFuture<String> hireCar = orderHotel.thenCombine(orderAirplane, (a, b) -> {
            System.out.println("根据航班 + 酒店 订购租车服务");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "租车信息";
        });

        String s = hireCar.get();

        System.out.println(s);
    }
}
```

##### 20. JMM

JMM: Java Memory Model

JMM 关于同步的规定：
1. 线程解锁前，必须把共享变量的值刷新回主内存
2. 线程加锁前，必须读取主内存的最新值到自己的工作内存
3. 加锁解锁是同一把锁

JMM规定了内存主要划分为**主内存**和**工作内存**两种。此处的主内存和工作内存跟JVM内存划分（堆、栈、方法区）是在不同的层次上进行的，如果非要对应起来，**主内存对应的是Java堆中的对象实例部分**，**工作内存对应的是栈中的部分区域**，从更底层的来说，主内存对应的是硬件的物理内存，工作内存对应的是寄存器和高速缓存。

各线程的工作内存间彼此独立，互不可见。

![JMM8种操作](http://qcorkht4q.bkt.clouddn.com/blogJMM8种操作.png)

![没有可见性](http://qcorkht4q.bkt.clouddn.com/blog没有可见性.png)

线程A感知不到线程B操作了值的变化！如何能够保证线程间可以同步感知这个问题呢？只需要使用`volatile`关键字！volatile 保证线程间变量的可见性，简单地说就是当线程A对变量X进行了修改后，在线程A后面执行的其他线程能看到变量X的变动，更详细地说是要符合以下两个规则 ：
- 线程对变量进行修改之后，要立刻回写到主内存。
- 线程对变量读取的时候，要从主内存中读，而不是缓存。

##### 21.volatile
- 保证可见性
- 不保证原子性
- 禁止指令重排
---
保证可见性
```java
public class TestVolatile {

    private volatile static int num = 0;
	
    public static void main(String[] args) {
        /*这里有两个线程。一个是new Thread，一个main线程
        测试加没加volatile前后的效果*/
        // volatile可保证数据的同步，也就是可见性
         testVisibility();
    }

    private static void testVisibility(){
        new Thread(()->{
            while(num == 0){
            //故意死循环
            }
        }).start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        num = 1;

        System.out.println(num);
    }
}
```
---
不保证原子性

原子性理解：不可分割，完整性，也就是某个线程正在做某个具体的业务的时候，中间不可以被加塞或者被分割，需要整体完整，要么同时成功，要么同时失败。

``++``只能保证立即写入主存，但是多个线程可能同时从主存取出同一个值，导致`++`后值被覆盖掉了。
```java
public class TestVolatile {

    private volatile static int num = 0;

    public static void main(String[] args) {
        testAtomicity();
    }

    private static void testVisibility(){
        new Thread(()->{
            while(num == 0){
            //故意死循环
            }
        }).start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        num = 1;

        System.out.println(num);
    }

    private static void testAtomicity(){

        for (int i = 1; i <= 20; i++) {
            //正常的话结果应该是2w，但是实际却小于2w，即使加了volatile
            new Thread(()->{
                for (int j = 0; j < 1000; j++) {
                    unsafeAdd();
                }
            }, String.valueOf(i)).start();
        }

        // 需要等待上面20个线程都计算完毕，看最终结果
        while(Thread.activeCount() > 2){   //默认一个main线程 一个gc线程
            Thread.yield();
        }

        System.out.println(Thread.currentThread().getName() + " " + num);
    }

    private static void unsafeAdd(){
        //num++是非原子性操作！！
        /*num++实际上是3个指令：
        * 1.执行getfield拿到原始num
        * 2.执行iadd进行加1操作
        * 3.执行putfield写把累加后的值写回
        * 简而言之，取值，+1，写入 */
        num++;
    }
}
```
num++是非原子性操作！

![i++](http://qcorkht4q.bkt.clouddn.com/blog1595600327035.png)


对unsafeAdd加锁，可以解决。还有其他办法吗？`Atomic`原子类

假设玩拳击，我要求锤一个球2w次，同一时刻击中一次算1分。

假设我现在分10个人去锤，一个人锤1000次。每个人都看不到别人，自管自己锤，就有可能同一时刻同时出拳，两个人（甚至多个人）都打出了一拳，但是只算1分，白白浪费了一次机会。

而我用AtomicInteger，里面`getAndIncrement()`采用了CAS，修改操作在循环内进行，线程会不断的尝试修改共享资源。如果没有冲突就修改成功并退出，否则就会继续循环尝试。如果有多个线程修改同一个值，必定会有一个线程能修改成功，而其他修改失败的线程会不断重试直到修改成功。

就是我每个人要锤之前，先取一次当前的分数，当我真正要锤的时候，在锤的那个瞬间再取一次值，看看跟我在锤之前的分数是不是一样，是的话我就锤，不是的话我就再重新试试，反正我没锤下去就没有浪费次数对吧。因为我知道我总会成功的，最极端的情况（这是不可能的，假设而已）别人都锤完自己的1k次了，我再锤也OK，反正我只要把我的1k次锤下去，能锤到有效的1k分数就行了，管那么多干嘛

```java
public class TestAtomicInteger {
    private static AtomicInteger num = new AtomicInteger();

    private static void safeAdd(){
        num.getAndIncrement();   //等价于 num++
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 20; i++) {
            new Thread(()->{
                for (int j = 0; j < 1000; j++) {
                    safeAdd();
                }
            }, String.valueOf(i)).start();
        }

        //需要等待上面20个线程都执行完毕后，看最终结果
        //默认一个main线程 一个gc线程
        while(Thread.activeCount() > 2){
            Thread.yield();
        }

        System.out.println(Thread.currentThread().getName() + " " + num);
    }
}
```

这里插个题外话，如何在main线程中保证代码中的所有线程已经执行完毕？

1. 使用Threa.yield()礼让。因为默认有两个线程 main线程、GC线程  (这个比较**实用**！)
```java
        // 需要等待上面20个线程都计算完毕，看最终结果
        while(Thread.activeCount() > 2){   //默认一个main线程 一个gc线程
            Thread.yield();
        }
```
2. while(thread.isAlive())
```java
public class VolatileExample {
    public static volatile int count = 0; // 计数器
    public static final int size = 100000; // 循环测试次数

    public static void main(String[] args) {
        // ++ 方式
        Thread thread = new Thread(() -> {
            for (int i = 1; i <= size; i++) {
                count++;
            }
        });
        thread.start();
        // -- 方式
        for (int i = 1; i <= size; i++) {
            count--;
        }
        // 等所有线程执行完成
        while (thread.isAlive()) {}
        System.out.println(count); // 打印结果
    }
}
```
---
禁止指令重排 [详解](https://cloud.tencent.com/developer/article/1601019)

计算机在执行程序时，为了提高性能，编译器和处理器的常常会对指令做重排。（但也不是任意排，要遵循happen-before原则！）

```java
public class VolatileExample {
    // 指令重排参数
    private static int a = 0, b = 0;
    private static int x = 0, y = 0;

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            Thread t1 = new Thread(() -> {
                // 有可能发生指令重排，先 x=b 再 a=1
                a = 1;
                x = b;
            });
            Thread t2 = new Thread(() -> {
                // 有可能发生指令重排，先 y=a 再 b=1
                b = 1;
                y = a;
            });
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            System.out.println("第 " + i + "次，x=" + x + " | y=" + y);
            if (x == 0 && y == 0) {
                // 发生了指令重排
                break;
            }
            // 初始化变量
            a = 0;
            b = 0;
            x = 0;
            y = 0;
        }
    }
}
```

![指令重排](http://qcorkht4q.bkt.clouddn.com/blog1595601268484.png)

原理：**内存屏障**

内存屏障（Memory Barrier）又称内存栅栏，是一个CPU 指令，它的作用有两个：
1. 保证特定操作的执行顺序。
2. 保证某些变量的内存可见性（利用该特性实现volatile的内存可见性）。

由于编译器和处理器都能执行指令重排优化。如果在指令间插入一条 Memory Barrier 则会告诉编译器和CPU，不管什么指令都不能和这条 Memory Barrier 指令重排序，也就是说，通过插入内存屏障禁止在**内存屏障`前后`的指令**执行重排序优化。内存屏障另外一个作用是强制刷出各种CPU的缓存数据，因此任何CPU上的线程都能读取到这些数据的最新版本。

![](http://qcorkht4q.bkt.clouddn.com/blog1595601663226.png)

内存屏障 --->简而言之，禁止上面的指令和下面的指令顺序交换

`前后`的指令
`前后`的指令
`前后`的指令
`前后`的指令
`前后`的指令
`前后`的指令
`前后`的指令
`前后`的指令
`前后`的指令
`前后`的指令
`前后`的指令

##### 21.CAS是什么

CAS: Compare-And-Swap

真实值和期望值相同，就修改成功，真实值和期望值不同，就修改失败！

###### CAS底层原理？如果知道谈谈你对Unsafe类的理解？

就用之前写过的AtomicInteger举例，`atomicInteger.getAndIncrement();`探究这里的自增加1是怎么实现的

```java
atomicInteger.getAndIncrement(); // 分析源码，如何实现的 i++ 安全的问题 

```java
atomicInteger.getAndIncrement(); // 分析源码，如何实现的 i++ 安全的问题 
public final int getAndIncrement() { // 继续走源码 
// this 当前对象 
// valueOffset 内存偏移量，内存地址 
// 1.固定写死 
return unsafe.getAndAddInt(this, valueOffset, 1); 
} 
```
![value](http://qcorkht4q.bkt.clouddn.com/blog1595755728834.png)

![getAndAddInt](http://qcorkht4q.bkt.clouddn.com/blog1595755448314.png)

```java
public final int getAndAddInt(Object var1, long var2, int var4) { 
int var5; 
do {
// 获取传入对象的地址 
var5 = this.getIntVolatile(var1, var2); 
// 比较并交换，如果var1，var2 还是原来的 var5，就执行var5 =  var5 + var4
} while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4)); 
return var5; 
} 
```

1. UnSafe: 获取并操作内存的数据。
UnSafe是CAS的核心类，由于Java方法无法直接访问底层系统，需要通过本地（native）方法来访问，UnSafe相当于一个后门，基于该类可以直接操作特定内存的数据，Unsafe类存在于 sun.misc包中，其内部方法操作可以像C的指针一样直接操作内存，因为Java中CAS操作的执行依赖于Unsafe类的方法。

注意：**Unsafe类中的所有方法都是Native修饰的，也就是说Unsafe类中的方法都直接调用操作系统底层资源执行相应任务**

2. 变量valueOffset: 存储value在AtomicInteger中的偏移量。
表示该变量值在内存中的偏移地址，因为Unsafe就是根据内存偏移地址获取数据的。

3. 变量 value: 存储AtomicInteger的int值
用volatile修饰，保证了多线程之间的内存可见性

总结：**CAS 有3个操作数，内存值V，旧的预期值A，要修改的更新值B。且仅当预期值A 和 内存值 V 相同时，将内存值 V 修改为B，否则什么都不做。**

缺点：
1. 循环时间长开销很大。
可以看到源码中存在 一个 do...while 操作，如果CAS失败就会一直进行尝试。

2. 只能保证一个共享变量的原子操作。

3. ABA问题
###### ABA问题

ABA问题带来的危害：
小明在提款机，提取了50元，因为提款机问题，有两个线程，同时把余额从100变为50
线程1（提款机）：获取当前值100，期望更新为50，
线程2（提款机）：获取当前值100，期望更新为50，
线程1成功执行，线程2某种原因block了，这时，某人给小明汇款50
线程3（默认）：获取当前值50，期望更新为100，
这时候线程3成功执行，余额变为100，
线程2从Block中恢复，获取到的也是100，compare之后，继续更新余额为50！！！
此时可以看到，实际余额应该为100（100-50+50），但是实际上变为了50（100-50+50-50）这就是ABA问题带来的成功提交。

解决方法：
在变量前面加上版本号（时间戳），每次变量更新的时候变量的版本号都+1，即A->B->A就变成了1A->2B->3A。（就是乐观锁）

`AtomicStampedReference`
`AtomicStampedReference`
`AtomicStampedReference`
`AtomicStampedReference`
`AtomicStampedReference`
`AtomicStampedReference`
`AtomicStampedReference`


[实例](https://www.cnblogs.com/MWCloud/p/11460721.html)

狂神写的实例感觉很差

## 22. 公平锁、非公平锁

公平锁：是指多个线程按照申请锁的顺序来获取锁，类似排队打饭，先来后到。
非公平锁：是指多个线程获取锁的顺序并不是按照申请锁的顺序，有可能后申请的线程比现申请的线程

优先获取锁，在高并发的情况下，有可能会造成优先级反转或者饥饿现象。

```java
// 无参 
public ReentrantLock() { 
sync = new NonfairSync(); 
}
// 有参 
public ReentrantLock(boolean fair) { 
sync = fair ? new FairSync() : new NonfairSync(); 
} 
```

非公平锁就是食堂窗口开了，所有人一窝蜂都去抢饭，不管你是买饭要买10分钟，还是买1s。抢到就是王

公平锁就讲究**先来后到**！

- 公平锁：就是很公平，在并发环境中，每个线程在获取到锁时会先查看此锁维护的等待队列，如果为空，或者当前线程是等待队列的第一个，就占有锁，否则就会加入到等待队列中，以后会按照**FIFO的规则从队列中取到自己**。

- 非公平锁：非公平锁比较粗鲁，上来就直接尝试占有锁，如果尝试失败，就会采用类似公平锁那种方式。

Java ReentrantLock 而言，通过构造函数指定该锁是否是公平锁，默认是非公平锁。非公平锁的优点在于吞吐量比公平锁大。

对于Synchronized而言，只能是非公平锁。

## 23. 可重入锁
指的是同一线程外层函数获得锁之后，内层递归函数仍然能获取该锁的代码，在同一个线程在外层方法获取锁的时候，在进入内层方法会自动获取锁。

也就是说，线程可以进入任何一个它已经拥有的锁，所同步着的代码块。 **好比家里进入大门之后，就可以进入里面的房间了**。

可重入锁最大的作用就是避免死锁

没有好的例子就不说了

## 23.自旋锁

指尝试获取锁的线程不会立即阻塞，而是采用循环的方式去尝试获取锁，这样的好处是减少线程上下文切换的消耗，缺点是循环会消耗CPU。

自己写一个自旋锁来为线程线程加锁8

核心：`AtomicReference`原子引用

自己的锁。重点：Thread thread = Thread.currentThread(); 获得当前的线程！然后CAS！

在使用的过程中，就要保证是同一个锁（也就是同一个spinLock）
```java
public class SpinLock {
    private AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public void myLock(){
        Thread thread = Thread.currentThread();

        System.out.println(thread.getName() + "-> myLock");

        while(!atomicReference.compareAndSet(null, thread)){
            //自旋
            //采用循环的方式尝试获取锁
            System.out.println(thread.getName() + "-> is waiting the lock");
        }
    }

    public void myUnlock(){
        Thread thread = Thread.currentThread();

        atomicReference.compareAndSet(thread, null);

        System.out.println(thread.getName() + "-> myUnlock");
    }
}
```

测试一下
```java
public class TestSpinLock {
    public static void main(String[] args) throws InterruptedException {
        SpinLock spinLock = new SpinLock();

        new Thread(()->{
            spinLock.myLock();
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                spinLock.myUnlock();
            }
        },"T1").start();


        //T2会一直自旋，直到T1释放锁
        new Thread(()->{
            spinLock.myLock();
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                spinLock.myUnlock();
            }
        },"T2").start();
    }
}
```

输出结果
```
T1-> myLock
T2-> myLock
T2-> is waiting the lock
T2-> is waiting the lock
...
T2-> is waiting the lock
T2-> is waiting the lock
T2-> is waiting the lock
T2-> is waiting the lock
T2-> is waiting the lock
T2-> is waiting the lock
T2-> is waiting the lock
T2-> is waiting the lock
T2-> is waiting the lock
T1-> myUnlock
T2-> myUnlock
```
主线程中T1先执行，会先获得锁。T2会一直自旋，直到T1释放锁，T2才能获得锁-->释放锁

## 24.怎么排查死锁？

死锁是指两个或两个以上的进程在执行过程中，因争夺资源而造成的一种互相等待的现象。

![死锁](http://qcorkht4q.bkt.clouddn.com/blog1595764863627.png)

产生死锁的4大条件：
1. 互斥条件：一个资源每次只能被一个进程使用。
2. 占有且等待：一个进程因请求资源而阻塞时，对已获得的资源保持不放。
3. 不可强行占有:进程已获得的资源，在末使用完之前，不能强行剥夺。
4. 循环等待条件:若干进程之间形成一种头尾相接的循环等待资源关系。

这四个条件是死锁的**必要条件**，只要系统发生死锁，这些条件必然成立，而只要**上述条件之一不满足，就不会发生死锁**。

###### 排查死锁

**查看堆栈信息**！！！！

1. 查看JDK目录的bin目录
2. 使用 `jps -l` 命令定位进程号
3. 使用 `jstack 进程号` 找到死锁查看
   
![定位进程号](http://qcorkht4q.bkt.clouddn.com/blog1595765532461.png)

![查看堆栈信息](http://qcorkht4q.bkt.clouddn.com/blog1595765585881.png)