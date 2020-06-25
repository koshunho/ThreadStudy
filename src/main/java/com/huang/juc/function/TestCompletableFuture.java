package com.huang.juc.function;

import java.util.concurrent.*;

/*举一个生活上的例子，假如我们需要出去旅游，需要完成三个任务：

任务一：订购航班
任务二：订购酒店
任务三：订购租车服务
很显然任务一和任务二没有相关性，可以单独执行。但是任务三必须等待任务一与任务二结束之后，才能订购租车服务。

之前的操作是借助 CountDownLatch。 现在使用CompletableFuture
*/

/*1.创建CompletableFuture实例
public static <U> CompletableFuture<U> completedFuture(U value)
public static CompletableFuture<Void> runAsync(Runnable runnable)
public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)

区别：runAsync 没有返回值， supplyAsync有返回值

2.设置任务结果
CompletableFuture 提供以下方法，可以主动设置任务结果。
boolean complete(T value)
boolean completeExceptionally(Throwable ex)
第一个方法，主动设置 CompletableFuture 任务执行结果，若返回 true，表示设置成功。
如果返回 false，设置失败，这是因为任务已经执行结束，已经有了执行结果。
*/

/*CompletableFuture
* CompletableFuture 实现了接口CompletionStage
* CompletableFuture 大部分方法来自CompletionStage 接口，正是因为这个接口，CompletableFuture才有如从强大功能。

想要理解 CompletionStage 接口，我们需要先了解任务的时序关系的。我们可以将任务时序关系分为以下几种：

串行执行关系
并行执行关系
AND 汇聚关系
OR 汇聚关系*/

//CompletableFuture 功能非常强大，可以编排异步任务，完成串行执行，并行执行，AND 汇聚关系，OR 汇聚关系
public class TestCompletableFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // testComplete();

        // testCompleteExceptionally();

        // testThenApply();

        // testThenCombine();

        testApplyToEither();
    }

    private static void testComplete() throws ExecutionException, InterruptedException {
        //执行异步任务
        CompletableFuture cf = CompletableFuture.supplyAsync(()->{
            System.out.println("cf执行任务开始");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("cf执行任务结束");
            return "NMSL";
        });

        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        try {
            threadPool.execute(()->{
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("主动设置cf任务结果");
                // 设置任务结果，由于 cf 任务未执行结束，结果返回 true
                cf.complete("主动设置NMSL");
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }

        // 由于 cf 未执行结束，将会被阻塞。5 秒后，另外一个线程主动设置任务结果
        System.out.println("get:" + cf.get());
        // 等待 cf 任务执行结束
        TimeUnit.SECONDS.sleep(6);
        // 由于已经设置任务结果，cf 执行结束任务结果将会被抛弃
        System.out.println("get:" + cf.get());
    }

    private static void testCompleteExceptionally() throws ExecutionException, InterruptedException {
        //执行异步任务
        CompletableFuture cf = CompletableFuture.supplyAsync(()->{
            System.out.println("cf执行任务开始");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("cf执行任务结束");
            return "NMSL";
        });

        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        try {
            threadPool.execute(()->{
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("主动设置cf任务结果");
                // 设置任务结果，由于 cf 任务未执行结束，结果返回 true
                cf.completeExceptionally(new RuntimeException("挂了！"));
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }

        // 由于 cf 未执行结束，前 5 秒将会被阻塞。后续程序抛出异常，结束
        System.out.println("get:" + cf.get());
    }

    /*任务串行执行，下一个任务必须等待上一个任务完成才可以继续执行
    * CompletionStage 有四组接口可以描述串行这种关系:
    * 同步：thenApply(Function<T,R>)、thenAccept(Consumer<T>)、thenRun(Runnable)、 thenCompose(Runnable)
    * 异步：thenApplyAsync(Function<T,R>)、thenAcceptAsync(Consumer<T>)、thenRunAsync(Runnable)、thenComposeAsync(Runnable)*/
    private static void testThenApply() throws ExecutionException, InterruptedException {
        //感觉有点像stream。链式回调：回调的美妙之处在于，我们无需等待结果就能够说出当异步计算结束后应当发生什么
        //首先开启一个异步任务，接着串行执行后续两个任务。任务 2 需要等待任务1 执行完成，任务 3 需要等待任务 2
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(()-> "nmsl")
                .thenApply(s-> s + "@Fukuoka")
                .thenApply(s-> s.toUpperCase());

        System.out.println(cf.get());
    }

    /*AND 汇聚关系代表所有任务完成之后，才能进行下一个任务。
    * CompletionStage 有以下接口描述这种关系:
    * 同步：thenCombine( CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn)
    *      thenAcceptBoth(CompletionStage<? extends U> other,BiConsumer<? super T, ? super U> action)
    *      runAfterBoth
    *
    * 异步：thenCombineAsync(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn)
    *      thenAcceptBothAsync
    *      runAfterBothAsync
    *
    * thenCombine 方法核心参数 BiFunction ，作用与 Function一样，只不过 BiFunction 可以接受两个参数，而 Function 只能接受一个参数
    * thenAcceptBoth 方法核心参数BiConsumer 作用也与 Consumer一样，不过其需要接受两个参数。
    * */
    private static void testThenCombine() throws ExecutionException, InterruptedException {
        //如开头生活中的例子
        //任务1：订购航班
        CompletableFuture<String> orderFlight = CompletableFuture.supplyAsync(()->{
            System.out.println("查询航班");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("订购航班");
            return "航班信息";
        });

        //任务2：订购酒店
        CompletableFuture<String> orderHotel = CompletableFuture.supplyAsync(()->{
            System.out.println("查询酒店");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("订购酒店");
            return "酒店信息";
        });

        //任务3：任务1和任务2都完成后，才能订车
        CompletableFuture<String> hireCar = orderFlight.thenCombine(orderHotel, (h,f)->{
            System.out.println("根据航班+酒店信息租车");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "租车信息";
        });

        System.out.println(hireCar.get());
    }


    /*OR 汇聚关系代表只要多个任务中任一任务完成，就可以接着接着执行下一任务。
    * CompletionStage 有以下接口描述这种关系：
    * 同步：applyToEither、acceptEither、runAfterEither
    * 异步：+Async*/
    private static void testApplyToEither() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "NMSL";
        });

        //由cf调
        CompletableFuture<String> cf2 = cf.supplyAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "WCNMLGB";
        });

        //执行or关系
        CompletableFuture<String> cf3 = cf2.applyToEither(cf, s->s);

        // 输出结果，由于 cf2 只休眠 3 秒，优先执行完毕
        System.out.println(cf3.get());
    }
}
