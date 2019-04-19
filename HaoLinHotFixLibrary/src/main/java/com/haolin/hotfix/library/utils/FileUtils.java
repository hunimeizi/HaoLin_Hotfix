package com.haolin.hotfix.library.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 作者：haoLin_Lee on 2019/04/19 11:47
 * 邮箱：Lhaolin0304@sina.com
 * class: 文件工具类
 */
public class FileUtils {

    /**
     * 复制文件
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     */
    public static void copyFile(File sourceFile, File targetFile)
            throws IOException {

        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        FileOutputStream outPut = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(outPut);

        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }

        outBuff.flush();

        inBuff.close();
        outBuff.close();
        outPut.close();
        input.close();
    }
}
