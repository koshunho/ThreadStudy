package com.huang.juc.function;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

// 流 是什么？是数据渠道，用于操作数据源（集合、数组等）所生成的元素序列
// 数据源：Collection, IO流
// 中间操作：filter, distinct, map, sorted, limit(截取)..
// 终端操作：收集为Collection, 收集为Array， 收集为String, count 计算元素数量， max, min
public class Stream {
    /*
    * 题目：找出同时满足一下条件的用户：
    * 1、全部满足偶数ID
    * 2.年龄大于24
    * 3.用户名转为大写
    * 4.用户名字母倒排序
    * 5.只输出一个用户名字 limit*/
    public static void main(String[] args) {
        User u1 = new User(11, "a", 23);
        User u2 = new User(12, "b", 24);
        User u3 = new User(13, "c", 25);
        User u4 = new User(14, "d", 26);
        User u5 = new User(16, "e", 27);

        List<User> users = Arrays.asList(u1, u2, u3, u4, u5);

        /*
        * 1.首先需要将users转化成stream流
        * 2.然后将用户过滤出来，这里用到一个函数式接口Predicate<? super T>
        * 3. 这里面传递的参数，就是Stream流的泛型类型User。这里就可以直接返回用户id为偶数的用户
        * 4.通过forEach进行遍历，直接简化输出System.out::println，等价于System.out.println(u); */
        users.stream()
                .filter((user -> user.getId() % 2 == 0))
                .filter(user-> user.getAge() > 24)
                .map(user-> user.getUsername().toUpperCase())
                .sorted((user1,user2)-> user2.compareTo(user1))
                .limit(5)
                .forEach(System.out::println); //相当于forEach(user->System.out.println(user));
                                               //里面是一个Consumer<T> : 参数类型T，返回void，重写accept()

    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class User{
    private int id;

    private String username;

    private int age;
}