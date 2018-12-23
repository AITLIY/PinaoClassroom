package com.yiyin.aobosh.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ALIY on 2018/12/23 0023.
 */

public class NetworkUtils {

    /**
     * 检查当前是否连接
     *
     * @param context
     * @return true表示当前网络处于连接状态，否则返回false
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
