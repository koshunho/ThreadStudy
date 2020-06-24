package com.huang.juc.function;

import java.util.function.Consumer;

// Consumer<T> : 参数类型T，返回void，重写accept()
// 对类型为T的对象应用操作
public class TestConsumer {
    public static void main(String[] args) {
        Consumer<String> consumer = s-> System.out.println(s + "NMSL");

        consumer.accept("NMSL");
    }
}
