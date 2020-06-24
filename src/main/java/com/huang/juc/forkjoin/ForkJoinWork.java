package com.huang.juc.forkjoin;

// 两步：1.任务切分 2.结果合并
// ForkJoin有一个 工作窃取 的概念。就是一个工作线程下会维护一个多个包含多个子任务的双端队列。
// 对于每个工作线程来说，会从头部到尾部依次执行任务。这是总会有一些线程执行的速度比较多，很多就把所有任务消耗完了
// 这时候怎么办呢，总不能空等着吧，多浪费资源。然后就从其他队列窃取任务来执行
// 被窃取任务线程永远从双端队列的头部拿任务执行，而窃取任务的线程永远从双端队列的尾部拿任务执行

// 核心类：ForkJoinPool、 ForkJoinTask
// ForkJoinPool: 其中有一个内部类WorkQueue,是线程池中线程的工作队列的一个封装，支持任务窃取。每一个线程都有一个WorkQueue，而WorkQueue中有执行任务的线程(ForkJoinWorkerThread owner)，还有这个线程需要处理的任务（ForkJoinTask<?>[]array)、新提交的任务就是加到array中
// ForkJoinTask: 代表运行在ForkJoinPool的任务。

/*ForkJoinTask主要方法： fork(): 在当前线程运行的线程池中安排一个异步执行。简单理解就是再创建一个子任务。
*                      join(): 当任务完成时返回计算结果
*                      invoke(): 开始执行任务，如果必要，等待计算完成
*子类： Recursive：递归
* RecursiveAction:一个递归无结果的ForkJoinTask(没有返回值）
* RecursiveTask：一个递归有结果的ForkJoinTask（有返回值） 使用：通过继承抽象类RecursiveTask实现抽象方法compute()*/

import java.util.concurrent.RecursiveTask;

public class ForkJoinWork extends RecursiveTask {

    private Long start;

    private Long end;

    public static final Long critical = 10000L; //final对变量使用：不可修改

    public ForkJoinWork(Long start, Long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        //判断是否拆分完毕
        Long length = end - start;

        if(length <= critical){
            //如果小于临界值就直接加，不然就fork
            Long sum = 0L;

            for (Long i = start; i <= end; i++) {
                sum += i;
            }
            return sum;
        }else{
            //没有拆分完毕就拆分
            Long middle = (end + start) / 2;

            ForkJoinWork left = new ForkJoinWork(start, middle);

            //拆分，并压入线程队列
            left.fork();

            ForkJoinWork right = new ForkJoinWork(middle + 1, end);

            //拆分，并压入线程队列
            right.fork();

            return (Long)left.join() +(Long)right.join();
        }
    }
}
