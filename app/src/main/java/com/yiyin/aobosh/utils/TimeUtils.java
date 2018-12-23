package com.yiyin.aobosh.utils;

import com.lidroid.xutils.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ALIY on 2018/12/16 0016.
 */

public class TimeUtils {

    //获取当前时间
    public static String getCurrentTime(String pattern) {

        SimpleDateFormat df = new SimpleDateFormat(pattern);
        String time =  df.format(new Date());
        LogUtils.i("TimeUtils: time " + time);
        return time;
    }
}
