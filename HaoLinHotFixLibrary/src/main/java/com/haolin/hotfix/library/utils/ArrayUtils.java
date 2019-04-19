package com.haolin.hotfix.library.utils;

import java.lang.reflect.Array;

/**
 * 作者：haoLin_Lee on 2019/04/19 11:44
 * 邮箱：Lhaolin0304@sina.com
 * class:
 */
public class ArrayUtils {
    /**
     * 合并数组
     *
     * @param arrayLhs 前数组
     * @param arrayRhs 后数组
     * @return 对象
     */
    public static Object combineArray(Object arrayLhs, Object arrayRhs) {
        //获取一个数组的class对象，通过Array.newInstance()
        Class<?> localClass = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);
        int j = i + Array.getLength(arrayRhs);
        Object result = Array.newInstance(localClass, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }
}
