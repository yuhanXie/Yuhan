/*
 * CMB Confidential
 *
 *  Copyright (C) 2018 China Merchants Bank Co., Ltd. All rights reserved.
 *
 *  No part of this file may be reproduced or transmitted in any form or by any
 *  means, electronic, mechanical, photocopying, recording, or otherwise, without
 *  prior written permission of China Merchants Bank Co., Ltd.
 */

package com.example.yuhan.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * @author lifeng
 * @version 1.0 2017/1/3
 * @description 提供一些常用的工具方法
 */

public class DisplayUtils {
    private static DisplayMetrics displayMetrics = null;

    /**
     * dp转化为px
     *
     * @param dip 输入参数为dp
     * @return 转换后的像素值
     */
    public static final int dip2px(float dip) {
        if (null == displayMetrics) {
            displayMetrics = Resources.getSystem().getDisplayMetrics();
        }
        return (int) (displayMetrics.density * dip);
    }

    /**
     * dp转px
     * @param sp 输入的sp
     * @return 转换后的像素值
     */
    public static int sp2px(float sp) {
        if (null == displayMetrics) {
            displayMetrics = Resources.getSystem().getDisplayMetrics();
        }
        return (int) (displayMetrics.density * sp + 0.5f);
    }

    /**
     * 获取屏幕绝对宽度 (pixels)
     *
     * @return 屏幕宽度 (pixels)
     */
    public static int getScreenWidth() {
        if (null == displayMetrics) {
            displayMetrics = Resources.getSystem().getDisplayMetrics();
        }
        return displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕绝对高度 (pixels)
     *
     * @return 屏幕宽度 (pixels)
     */
    public static int getScreenHeight() {
        if (null == displayMetrics) {
            displayMetrics = Resources.getSystem().getDisplayMetrics();
        }
        return displayMetrics.heightPixels;
    }

    /**
     * 判断输入是否为空
     *
     * @param str 输入的字符串
     * @return 是否为空
     */
    public static final boolean isStrEmpty(String str) {
        return str == null || str.length() == 0;
    }
}