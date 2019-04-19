package com.haolin.hotfix.library;

import android.content.Context;

import com.haolin.hotfix.library.utils.ArrayUtils;
import com.haolin.hotfix.library.utils.Constants;
import com.haolin.hotfix.library.utils.ReflectUtils;

import java.io.File;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * 作者：haoLin_Lee on 2019/04/19 11:43
 * 邮箱：Lhaolin0304@sina.com
 * class: 加载热修复文件
 */
public class FixDexUtils {

    private static HashSet<File> loadeDex = new HashSet<>();

    static {
        //修复之前清空集合
        loadeDex.clear();
    }

    public static void loadFixedDex(Context context) {

        File fileDir = context.getDir(Constants.DEX_DIR, Context.MODE_PRIVATE);
        //循环私有目录的所有文件
        File[] listFiles = fileDir.listFiles();
        for (File file : listFiles) {
            if (file.getName().endsWith(Constants.DEX_SUFFIX) && !"class.dex".equals(file.getName())) {

                loadeDex.add(file);
            }
        }
        //模拟类加载器
        createDexClassLoader(context, fileDir);
    }

    //创建加载补丁的DexClassLoad 类加载器
    private static void createDexClassLoader(Context context, File fileDir) {
        //创建解压目录
        String optimizedDir = fileDir.getAbsolutePath() + File.separator + "opt_dex";
        //创建目录
        File fopt = new File(optimizedDir);
        if (!fopt.exists()) {
            //创建多级目录
            fopt.mkdirs();
        }
        for (File dex : loadeDex) {
            //自有的类加载器
            DexClassLoader classLoader = new DexClassLoader(dex.getAbsolutePath()
                    , optimizedDir, null, context.getClassLoader());
            //每循环一次 修复一次（插装）
            hotFix(classLoader, context);
        }
    }

    private static void hotFix(DexClassLoader classLoader, Context context) {
        //获取系统的pathClassLoader
        PathClassLoader pathLoader = (PathClassLoader) context.getClassLoader();
        try {
            //获取自有的dexElement数组
            Object myElements = ReflectUtils.getDexElements(ReflectUtils.getPathList(classLoader));
            //获取系统的dexElement数组
            Object systemElements = ReflectUtils.getDexElements(ReflectUtils.getPathList(pathLoader));
            //合并并且生成新的dexElements数组
            Object dexElements = ArrayUtils.combineArray(myElements, systemElements);
            //获取系统的pathList
            Object systemPathList = ReflectUtils.getPathList(pathLoader);
            //通过反射技术，将新的dexElements 数组赋值给系统的pathList
            ReflectUtils.setField(systemPathList, systemPathList.getClass(), dexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
