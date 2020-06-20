package com.huang.demo;

import com.huang.utils.WebDownloader;

public class TestDownloaderByThread extends Thread{
    private String url;
    private String name;

    public TestDownloaderByThread(String url, String name) {
        this.url = url;
        this.name = name;
    }

    @Override
    public void run() {
        WebDownloader webDownloader = new WebDownloader();
        webDownloader.download(url, name);
        System.out.println("下载了文件名为："+name);
    }

    public static void main(String[] args) {
        TestDownloaderByThread t1 = new TestDownloaderByThread("https://ci.phncdn.com/www-static/images/pornhub_logo_straight.png?cache=2020061802", "pornhub.jpg");
        TestDownloaderByThread t2 = new TestDownloaderByThread("https://cdn1-s-hw-e1.xtube.com/v3_img/logo_xtube.png?cb=1699", "Xtube.jpg");
        TestDownloaderByThread t3 = new TestDownloaderByThread("https://static-t.xhcdn.com/logo/43/desktop-light.svg", "Xhamster.jpg");

        t1.start();
        t2.start();
        t3.start();

    }
}
