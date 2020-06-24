package com.huang.juc.function;

import java.util.function.Supplier;

// Supplier<T>:无参数，返回类型为T。重写get()
// 作用：返回类型为T的对象
public class TestSupplier {
    public static void main(String[] args) {
        Supplier<String> supplier = ()-> "NMSL";

        System.out.println(supplier.get());
    }
}
