package com.haolin.hotfix.err;

import android.content.Context;
import android.widget.Toast;

/**
 * 作者：haoLin_Lee on 2019/04/19 11:31
 * 邮箱：Lhaolin0304@sina.com
 * class:
 */
public class ParamsSort {

    public void math(Context context) {
        int i = 10;
        int j = 0;
        Toast.makeText(context, "math>>>>" + i / j, Toast.LENGTH_SHORT).show();
    }
}
