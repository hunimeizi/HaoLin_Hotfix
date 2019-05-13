# HaoLin_Hotfix -- Tinker热修复

  ## 关于Tinker 热修复
  ### 热修复的优势
    
   ① 无需重新发布版本，省时省力
     
   ② 用户无感修复，也无需下载最新应用，代价小
 
   ③ 修复成功率搞，把损失降到最低
     
 ### Tinker可以完成哪些修复
 
   ① 代码修复
    
   ② 资源修复
    
   ③ SO库修复
    
 ![image](https://user-gold-cdn.xitu.io/2019/5/12/16aac8066c534b52?w=1335&h=702&f=png&s=189568)
  
 - 热修复流程图
 
 ![image](https://user-gold-cdn.xitu.io/2019/5/12/16aac806a4074ace?w=1363&h=583&f=png&s=226917)
 
 * 类加载器BaseDexClassLoader的子类是DexClassLoader，这个类主要的方法是一个数据pathList，DexElements的数组中有class.dex是主包，还有很多**.dex文件，原因是因为javaC会把我们的java文件编译成class文件，再由class文件通过sdk/build-tools/版本号/dx.bat工具转换成dex。
 * 创建我们自己的类加载器加载修复包例如classes2.dex（这个文件是从服务器进行下载）然后保存到私有目录中（data/app/....）
 * 将我们自己的dex和系统的dex进行合并，生成一个新的dexElements数组，并把我们自己的dex放在数组最前面，这样优先级最高，这样就不会加载到错误的类了
 * 通过反射技术Reflect将我们新的dexElements赋值给系统的pathList
 
 ## 配置Gradle
 
   ① 在build.gradle defaultConfig里添加
     
             //开启分包
              multiDexEnabled true
              //设置分包配置
              multiDexKeepFile file('multidex-config.txt')
     
     
 
  
   ② 在build.gradle android里添加
    
    ```
             dexOptions {
                         javaMaxHeapSize "4g"
                         preDexLibraries = false
                         additionalParameters = [//配置multiDex参数
                                       '--multi-dex',
                                       //每个包内方法数上限
                                       '--set-max-idx-number=50000',
                                       //打包到主 classes.dex的文件列表
                                       '--minimal-main-dex'
                                         ]
                         }
    ```
    
   ③ 添加multidex 支持依赖
    
    ```
          implementation 'com.android.support:multidex:1.0.3'
    ```
    
   ④ 引入 module
   
    ```
          implementation project(':HaoLinHotFixLibrary')
    ```
    
   ⑤ 创建 multidex-config.txt 文件 app目录下 做一些主要类的引用
   
      ```
      com/haolin/hotfix/MainActivity.class
      com/haolin/hotfix/base/BaseActivity.class
      com/haolin/hotfix/base/BaseApplication.class
      
      ```
 
 ## 用法
 
 ### 初始化
 
 *  基础用法 Application需要继承MultiDexApplication
 
     `      
     BaseApplication extends MultiDexApplication
     `
     
 ### 创建私有目录
 
 
         ```
         //修复包  现不做网络下载 从手机里拿
         File sourceFile = new File
         (Environment.getExternalStorageDirectory(), Constants.DEX_NAME);
 
         //目标路径 私有目录
         File targetFile = new File(getDir(Constants.DEX_DIR, Context.MODE_PRIVATE)
         .getAbsolutePath() + File.separator + Constants.DEX_NAME);
 
         if (targetFile.exists()){
             targetFile.delete();
         }
         try {
             FileUtils.copyFile(sourceFile, targetFile);
             FixDexUtils.loadFixedDex(this);
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
    ```
  
  ### 加载热修复文件并通过反射技术插桩
  
  
  ```
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
             if (file.getName().endsWith(Constants.DEX_SUFFIX) &&
             !"class.dex".equals(file.getName())) {
 
                 loadeDex.add(file);
             }
         }
         //模拟类加载器
         createDexClassLoader(context, fileDir);
     }
 
     //创建加载补丁的DexClassLoad 类加载器
     private static void createDexClassLoader(Context context, File fileDir) {
         //创建解压目录
         String optimizedDir = fileDir.getAbsolutePath() + 
          File.separator + "opt_dex";
         //创建目录
         File fopt = new File(optimizedDir);
         if (!fopt.exists()) {
             //创建多级目录
             fopt.mkdirs();
         }
         for (File dex : loadeDex) {
             //自有的类加载器
             DexClassLoader classLoader = new DexClassLoader
             (dex.getAbsolutePath(), optimizedDir, null, context.getClassLoader());
             //每循环一次 修复一次（插装）
             hotFix(classLoader, context);
         }
     }
 
     private static void hotFix(DexClassLoader classLoader, Context context) {
         //获取系统的pathClassLoader
         PathClassLoader pathLoader = (PathClassLoader) context.getClassLoader();
         try {
             //获取自有的dexElement数组
             Object myElements = ReflectUtils.getDexElements(ReflectUtils.
             getPathList(classLoader));
             //获取系统的dexElement数组
             Object systemElements = ReflectUtils.getDexElements(ReflectUtils.
             getPathList(pathLoader));
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
 ```
 ### 修复
 
 将修复好的apk包利用解压工具打开，里面会有classes2.dex文件，与旧版本apk和该classes2.dex文件同时复制到手机里
 **在实际开发项目中应该将classes2.dex文件放在服务器，进行下载修复，我这只是方面demo**主要了解下核心原理，主
 要就是插桩技术。
   
   [Demo地址](https://github.com/hunimeizi/HaoLin_Hotfix)
 
 ### 总结
   Tinker热修复主要运用framework层技术，了解插桩原理，核心代码就是反射技术实现
   ```
         //获取系统的pathClassLoader
         PathClassLoader pathLoader = (PathClassLoader) context.getClassLoader();
         try {
             //获取自有的dexElement数组
             Object myElements = ReflectUtils.getDexElements(ReflectUtils.getPathList
             (classLoader));
             //获取系统的dexElement数组
             Object systemElements = ReflectUtils.getDexElements(ReflectUtils.
             getPathList(pathLoader));
             //合并并且生成新的dexElements数组
             Object dexElements = ArrayUtils.combineArray(myElements, systemElements);
             //获取系统的pathList
             Object systemPathList = ReflectUtils.getPathList(pathLoader);
             //通过反射技术，将新的dexElements 数组赋值给系统的pathList
             ReflectUtils.setField(systemPathList, systemPathList.getClass(), dexElements);
         } catch (Exception e) {
             e.printStackTrace();
         }
 ```
 ### 感谢