# HaoLin_Hotfix -- Tinker热修复

 ## 关于Tinker 热修复
- 配置Gradle

  ① 在build.gradle defaultConfig里添加
    ```xml
            //开启分包
             multiDexEnabled true
             //设置分包配置
             multiDexKeepFile file('multidex-config.txt')
    ```
   ② 在build.gradle android里添加
   ```xml
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
   ```xml
         implementation 'com.android.support:multidex:1.0.3'
   ```
   ④ 引入 module
   ```xml
         implementation project(':HaoLinHotFixLibrary')
   ```
   ⑤ 创建 multidex-config.txt 文件 app目录下 做一些主要类的引用
   ```xml
            com/haolin/hotfix/MainActivity.class
            com/haolin/hotfix/base/BaseActivity.class
            com/haolin/hotfix/base/BaseApplication.class
  ```
## 用法
### 初始化
- 基础用法 Application需要继承MultiDexApplication

    ```java
    BaseApplication extends MultiDexApplication
    ```
- 修复

    ```java
    在ParamsSort类里将j的赋值修改为1后，打包新版APK用压缩文件打开，会发现有classes2.dex文件，该文件可给服务器端，编写网络下载
    推给用户，直接进行修复，目前该功能没有实现,只需将该dex复制到手机内存根目录下，点击旧版本apk中修
    复按钮，即刻修复成功
    ```
