package com.yanfuchang.autotest.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.yanfuchang.autotest.functions.Function;

import java.lang.Class;

/**
 * 查找指定类的同级目录下的所有子类
 */
public class ClassFinderUtil {
    //存储实例化类对象
    private static List<Class<?>> classesInstance = new ArrayList<Class<?>>();

    /**
     * 根据类的Class对象，获取其所有子类的Class对象
     */
    public static List<Class<?>> getAllAssignedClass(Class<?> cls) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Class<?> c : getClasses(cls)) {
            if (cls.isAssignableFrom(c) && !cls.equals(c)) {
//            	System.out.println(c.toString());
                classes.add(c);
            }
        }
        return classes;
    }

    /**
     * 根据类的Class对象，获取当前路径下所有类的实例化对象
     */
    public static List<Class<?>> getClasses(Class<?> cls) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        String pk = cls.getPackage().getName();
        String path = pk.replace('.', '/');
        try {
            String dirPath = cls.getClassLoader().getResource(path).getPath();
            classes = getClasses(new File(dirPath), pk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * 同一路径下得到所有类的实例化对象
     */
    private static List<Class<?>> getClasses(File dir, String pk) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                getClasses(f, pk + "." + f.getName());
            }
            String name = f.getName();
            if (name.endsWith(".class")) {
                try {
                    classesInstance.add(Class.forName(pk + "." + name.substring(0, name.length() - 6)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return classesInstance;
    }

    public static void main(String[] args) {
        getAllAssignedClass(Function.class);
    }
}
