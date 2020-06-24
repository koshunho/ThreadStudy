package com.huang.juc.function;

import java.util.function.Function;

// Function<T, R> : 函数型接口，参数类型T，返回类型R，重写apply()。
// 作用：对类型为T的对象操作，并返回结果。结果是R类型的对象
public class TestFunction {
    public static void main(String[] args) {
/*        Function function = new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return 1024;
            }
        };*/

        //先写泛型，然后(参数)->{逻辑}
        Function<String, Integer> function = s-> s.length();

        Function<String, String> reverse = s-> new StringBuffer(s).reverse().toString();

        System.out.println(function.apply("abc"));

        System.out.println(reverse.apply("ダンスが済んだ"));
    }
}
