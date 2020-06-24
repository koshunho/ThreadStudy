package com.huang.juc.function;

import java.util.function.Predicate;

// Predicate<T>: 参数类型T，返回类型boolean。重写test()
// 作用：确定类型为T的对象是否满足某约束，并返回boolean值
public class TestPredicate {
    public static void main(String[] args) {
        Predicate<String> predicate = s-> s.isEmpty();

        System.out.println(predicate.test("NMSL"));
    }
}
