package com.huang.juc.pool;

// 查看三大方法的底层源码，发现本质都是调用了 new ThreadPoolExecutor(7大参数)
/*public ThreadPoolExecutor(int corePoolSize,
        int maximumPoolSize,
        long keepAliveTime,
        TimeUnit unit,
        BlockingQueue<Runnable> workQueue,
        ThreadFactory threadFactory,
        RejectedExecutionHandler handler)

corePoolSize：核心线程数。在创建了线程池之后，线程中没有任何线程，等到有任务到来的时候才创建线程去执行任务。
              默认情况下，在创建了线程池后，线程池中的线程数为0，当有任务来之后，就会创建一个线程去执行任务。
              当线程池中的线程数目到达corePoolSize之后，就会把到达的任务放到缓存队列当中。

maximumPoolSize：最大线程数。表明线程中最多能创建的线程数量，此值必须大于等于1。

keepAliveTime：空闲的线程保留的时间

TimeUnit：空闲线程的保留时间单位。

BlockingQueue<Runnable>：阻塞队列，存储等待执行的任务。
                        参数有ArrayBlockingQueue、LinkedBlockingQueue、SynchronousQueue可选。一般不用改。

threadFactory：线程工厂，用来创建线程，默认即可

RejectedExecutionHandler：阻塞队列已满，而且任务量大于最大线程的异常处理策略。有如下取值：
                          ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常
                          ThreadPoolExecutor.DiscardPolicy:也是丢弃任务，但是不抛出异常。
                          ThreadPoolExecutor.DiscardOlderstPolicy:丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
                          ThreadPoolExecutor.CallerRunsPolicy: 由调用线程处理该任务。哪里来的哪里去，比如main调用的话就让Main线程来处理
 */

/*ThreadPool工作原理：
* 一个任务进来之后：1.核心池是否已满？ == corePoolSize? 否，创建新线程执行任务。 是，进入2、
*                2.阻塞队列是否已满？ ==workQueue? 否，将任务添加到任务队列中（复用1中的线程）。 是，进入3
*                3.线程池是否已满？ == maximumPoolSize? 否，创建新线程执行任务。 是，触发拒绝策略
*
* */

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*所以在工作中线程池用哪个？三种创建线程池的方法哪个用得多？ 坑
* 回答：一个都不用。我们在工作中使用自定义的。应该通过ThreadPoolExecutor来创建，这样能明确线程池的运行规则，规避资源耗尽风险。
* FixedThreadPool 和 SingleThreadPool 允许请求队列长度为Integer.MAX_VALUE,可能积压大量请求，导致OOM
* CachedThreadPool 和 ScheduledThreadPool 允许创建线程的数量为Integer.MAX_VALUE,可能创建大量的线程，导致OOM */
public class TestThreadPoolExecutor {
    public static void main(String[] args) {

        int processors = Runtime.getRuntime().availableProcessors(); //获得CPU的内核数

        System.out.println(processors);

        //自定义ThreadPoolExecutor

        //思考一下，线程是否越多越好？
        //一个计算为主的程序（CPU密集型程序）。多线程跑的时候，可以充分利用所有的CPU核心，比如说4核的CPU，开4个线程的时候，可以同时跑4个线程的任务，此时是最大效率。
        //但是如果线程远远超出CPU核心数量，反而会使任务效率下降，因为频繁的切换线程也是需要时间的。因此对于CPU密集型程序来说，线程数=CPU核数是最好的了

        //如果是一个磁盘or网络为主的程序（IO密集型）。一个线程处在IO等待的时候，另一个线程还可以在CPU里面跑，有时候CPU闲着没事干，所有的线程都在等着IO，这时候他们就是同时的了
        //而单线程的话此时还是在一个一个等待的，我们都知道IO的速度比起CPU是慢的令人发指的。所以开多线程，比方说多线程网络传输，多线程往不同目录写文件，等等。
        //此时线程数= IO任务数是最佳的
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                2,
                4,
                3L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        /*因为我的处理器是4核，所以将maximumPoolSize设定为4。
        * workQueue的大小为3，那么我假设有7个人来办理业务
        * 1. 1~2人被受理 （核心大小core）
        * 2. 3~5人进队列（Queue）
        * 3. 6~7人到最大线程池（扩容大小max)
        * 4.再有人进来就要被拒绝策略了 */
        try {
            //模拟有x个顾客来办理业务
            //最大容量为：maximumPoolSize + workQueue = 最大容量数
            for (int i = 1; i <= 7; i++) {
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
