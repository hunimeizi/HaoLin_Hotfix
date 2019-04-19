package com.haolin.hotfix;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;

import com.haolin.hotfix.base.BaseActivity;
import com.haolin.hotfix.err.ParamsSort;
import com.haolin.hotfix.library.FixDexUtils;
import com.haolin.hotfix.library.utils.Constants;
import com.haolin.hotfix.library.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 作者：haoLin_Lee on 2019/04/19 10:59
 * 邮箱：Lhaolin0304@sina.com
 * class:
 */
public class SecondActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }


    public void btnShowOnClick(View view) {
        ParamsSort paramsSort = new ParamsSort();
        paramsSort.math(SecondActivity.this);
    }

    public void btnFixOnClick(View view) {
        //修复包  现不做网络下载 从手机里拿
        File sourceFile = new File(Environment.getExternalStorageDirectory(), Constants.DEX_NAME);

        //目标路径 私有目录
        File targetFile = new File(getDir(Constants.DEX_DIR, Context.MODE_PRIVATE).getAbsolutePath() + File.separator + Constants.DEX_NAME);

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
}
