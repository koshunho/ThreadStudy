package com.huang.demo;

import com.huang.utils.WebDownloader;

import java.util.concurrent.*;

public class TestCallable implements Callable {
    private String url;
    private String name;

    public TestCallable(String url, String name) {
        this.url = url;
        this.name = name;
    }

    //Callable的call()需要一个返回值。这里设置为Boolean
    public Boolean call() {
        WebDownloader webDownloader = new WebDownloader();
        webDownloader.download(url, name);
        System.out.println("下载了文件名为："+name);
        return true;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        TestCallable t1 = new TestCallable("https://ci.phncdn.com/www-static/images/pornhub_logo_straight.png?cache=2020061802", "pornhub.jpg");
        TestCallable t2 = new TestCallable("https://cdn1-s-hw-e1.xtube.com/v3_img/logo_xtube.png?cb=1699", "Xtube.jpg");
        TestCallable t3 = new TestCallable("https://static-t.xhcdn.com/logo/43/desktop-light.svg", "Xhamster.jpg");

        //创建执行服务
        ExecutorService ser = Executors.newFixedThreadPool(3);

        //提交执行
        Future<Boolean> submit1 = ser.submit(t1);
        Future<Boolean> submit2 = ser.submit(t2);
        Future<Boolean> submit3 = ser.submit(t3);

        //获取结果
        boolean rs1 = submit1.get();
        boolean rs2 = submit2.get();
        boolean rs3 = submit3.get();

        ser.shutdown();
    }
}
