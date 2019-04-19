package com.haolin.hotfix.library.utils;

import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

/**
 * 作者：haoLin_Lee on 2019/04/19 12:22
 * 邮箱：Lhaolin0304@sina.com
 * class:
 */
public class ReflectUtils {


    private static Object getField(Object obj, Class<?> clazz, String field)
            throws NoSuchFieldException, IllegalAccessException {

        Field localField = clazz.getDeclaredField(field); //getDeclaredField 获取当前的所有修饰方法
        localField.setAccessible(true); //设置私有方法可访问
        return localField.get(obj);
    }

    /**
     * 给某属性赋值，并设置私有可访问
     *
     * @param systemPathList 该属性所属类的对象
     * @param aClass         该属性的所属类
     * @param dexElements    值
     */
    public static void setField(Object systemPathList, Class<?> aClass, Object dexElements)
            throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
        Field localField = aClass.getDeclaredField("dexElements");
        localField.setAccessible(true);
        localField.set(systemPathList, dexElements);
    }

    /**
     * 通过反射技术获取BaseDexClassLoader对象中的pathList对象
     *
     * @param classLoader 对象
     * @return 对象
     */
    public static Object getPathList(Object classLoader) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(classLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    /**
     * 通过反射技术获取BaseDexClassLoader对象中的pathList对象 再获取dexElements对象
     *
     * @param pathList 对象
     * @return 对象
     */
    public static Object getDexElements(Object pathList) throws NoSuchFieldException, IllegalAccessException {
        return getField(pathList, pathList.getClass(), "dexElements");
    }
}
