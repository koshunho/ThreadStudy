package com.huang.syn;

//信号灯法
public class TestPC_Semaphore {
    public static void main(String[] args) {
        TV tv = new TV();
        new Player(tv).start();
        new Watcher(tv).start();
    }
}

class Player extends Thread{
    TV tv;

    public Player(TV tv){
        this.tv = tv;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            if(i % 2 == 0){
                tv.play("Pornhub正在播放 "+ i);
            }else{
                tv.play("Xhamster正在播放 "+ i);
            }
        }
    }
}

class Watcher extends Thread{
    TV tv;

    public Watcher(TV tv){
        this.tv = tv;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            tv.watch();
        }
    }
}

//说的不是直播！是录播（爆笑）
class TV{
    //演员表演，观众等待 F
    //观众观看，演员等待 T
    String show;
    boolean flag = true;

    //表演
    public synchronized void play(String show){
        if(!flag){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("演员表演了："+show);
        this.show = show;
        flag = !flag;
        this.notifyAll();
    }

    //观看
    public synchronized void watch(){
        if(flag){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("观看了:"+show);
        flag = !flag;
        //通知演员表演
        this.notifyAll();
    }
}