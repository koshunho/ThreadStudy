package com.huang.syn;

public class UnsafeBank {
    public static void main(String[] args) {
        Account account = new Account(100, "结婚基金");

        Drawing you = new Drawing(account, 50,"你");
        Drawing gf = new Drawing(account, 100,"女朋友");


        you.start();
        gf.start();
    }
}

class Account{
    int balance; //余额
    String name; //卡名

    public Account(int balance, String name) {
        this.balance = balance;
        this.name = name;
    }
}

//模拟取款
class Drawing extends Thread{

    Account account;

    int drawingMoney; //取了多少钱

    int nowMoney; //现在手里有多少钱

    //调用父类的构造器的时候必须放在第一行
    public Drawing(Account account, int drawingMoney, String name) {
        super(name);
        this.account = account;
        this.drawingMoney = drawingMoney;
    }

    @Override
    public void run() {

        //！！！！！！！！！！！！！！锁的对象就是变化的量，需要增删改的对象！！！！！！！！！！！
        synchronized (account){
            //判断有没有钱
            if (account.balance - drawingMoney < 0) {
                System.out.println(Thread.currentThread().getName() + "想取钱。但是钱都不够取不了");
                return;
            }

            // 放大问题发生性
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //卡内余额 = 余额-取的钱
            account.balance = account.balance - drawingMoney;

            //手里的钱
            nowMoney = nowMoney + drawingMoney;

            System.out.println(account.name + "余额为：" + account.balance);
            System.out.println(Thread.currentThread().getName() + "手里的钱：" + nowMoney);
        }
    }
}