package com.huang.syn;

// 多个线程持有对方需要的资源，然后形成僵持
/*
产生死锁的4个条件：
1.互斥：一个资源每次只能被一个进程使用。
2.请求与保持：一个进程因请求资源而阻塞时，对已获得的资源保持不放。  拿到镜子时，我还想拿口红，贪
3.不可强占：已获得的资源，在未使用完之前，不可强行剥夺。          我没拿到镜子，我也不能强抢你的镜子
4.循环等待：若干进程之间形成一个头尾相接的循环等待资源关系        我想要你的，你也想要我的
*/
public class DeadLock {
    public static void main(String[] args) {
        MakeUp jolin = new MakeUp(0, "蔡依林");
        MakeUp kun = new MakeUp(1, "蔡徐坤");

        jolin.start();
        kun.start();
    }
}

class LipStick{

}

class Mirror{

}

// 锁中锁，导致出现了两个对象以上的锁，然后都锁着不放，就造成死锁
// 只需要把锁分开写，一个锁里只锁一个对象
class MakeUp extends Thread{

    //需要的资源只有一份，用static保证一份
    static LipStick lipStick = new LipStick();
    static Mirror mirror = new Mirror();

    int choice;
    String girlName;

    public MakeUp(int choice, String girlName){
        this.choice = choice;
        this.girlName = girlName;
    }

    @Override
    public void run() {
        //化妆
        try {
            cosmetic();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //互相持有对方的锁，就是需要拿到对方的资源
    private void cosmetic() throws InterruptedException {
        if(choice == 0){
            synchronized (lipStick){  //获得口红的锁
                System.out.println(this.girlName + "获得口红的锁");
                Thread.sleep(1000);
                synchronized (mirror){  //1s后想获得镜子的锁
                    System.out.println(this.girlName + "获得镜子的锁");
                }
            }
        }else{
            synchronized (mirror){  //获得口红的锁
                System.out.println(this.girlName + "获得镜子的锁");
                Thread.sleep(1000);
                synchronized (lipStick){  //2s后想获得镜子的锁
                    System.out.println(this.girlName + "获得口红的锁");
                }
            }
        }
    }
}