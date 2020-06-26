package com.huang.juc.single;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

// 可恶的反射
// 反射很霸道，可以无视private修饰的构造方法，可以直接在外面newInstance,破坏辛辛苦苦写的单例。
public class HatefulReflection {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        DoubleCheckedLocking instance1 = DoubleCheckedLocking.getInstance();

        Constructor<DoubleCheckedLocking> declaredConstructor = DoubleCheckedLocking.class.getDeclaredConstructor(null);

        declaredConstructor.setAccessible(true);

        DoubleCheckedLocking instance2 = declaredConstructor.newInstance();

        System.out.println(instance1);

        System.out.println(instance2);

        System.out.println(instance1 == instance2);
    }
}
