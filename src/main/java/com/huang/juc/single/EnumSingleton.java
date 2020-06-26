package com.huang.juc.single;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/*枚举类的所有实例必须放在第一行显示，不需使用new，不需显示调用构造方法。
每个变量都是public static final修饰的，最终以分号结束。
我们可以充分利用枚举默认构造方法私有化的性质来实现单例。
由于里面的成员变量都是final修饰的，因此不会有线程不安全的问题。*/
public enum EnumSingleton {
    instance;

    public EnumSingleton getInstance(){
        return instance;
    }
}

// 枚举是目前最推荐的单例模式的写法。不需要开发自己保证线程的安全，同时可以防止反射破坏
class TestEnumSingleton{
    public static void main(String[] args) {
        EnumSingleton instance1 = EnumSingleton.instance;

        EnumSingleton instance2 = EnumSingleton.instance;

        System.out.println("正常情况下，判断两个实例是否相等: " + (instance1 == instance2));
    }
}

// 试一下用反射破坏
class TestReflection{
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // 这里有坑！自身的类没有无参构造方法，需要传一个String和int
        // Constructor<EnumSingleton> declaredConstructor = EnumSingleton.class.getDeclaredConstructor();

        Constructor<EnumSingleton> constructor = EnumSingleton.class.getDeclaredConstructor(String.class, int.class);

        constructor.setAccessible(true);

        EnumSingleton instance = constructor.newInstance();
        // 输出java.lang.IllegalArgumentException: Cannot reflectively create enum objects
        // 真滴破坏不了哦
    }
}