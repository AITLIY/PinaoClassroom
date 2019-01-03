package com.yiyin.aobosh.application;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.yiyin.aobosh.bean.LessonCategory;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.commons.CommonParameters;
import com.yiyin.aobosh.utils.Sputils;

import java.util.List;

/**
 * Created by ALIY on 2018/12/9 0009.
 * 全局信息设置
 */

public class GlobalParameterApplication extends Application {

    private static GlobalParameterApplication instance;  //Application实例
    public static GlobalParameterApplication getInstance() {
        return instance;
    }

    private RequestQueue mRequestQueue;                 // Volley网络请求队列
    public RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
            mRequestQueue.getCache().clear();
        }
        return mRequestQueue;
    }

    public void setUserInfo(UserInfo userInfo) {        //存储本地的用户对象
        Sputils.putObject(getApplicationContext(), userInfo);
    }

    public UserInfo getUserInfo() {                     //获取本地的用户对象
        return Sputils.getObject(getApplicationContext(), UserInfo.class);
    }

    public void clearUserInfo() {                       // 清空本地存储的用户信息
        Sputils.clear(getApplicationContext());
    }

    public void setLoginStatus(boolean status) {        // 设置用户登录状态

        if (status)
            Sputils.put(getApplicationContext(), CommonParameters.LOGINSTATUS, status);
        else
            Sputils.put(getApplicationContext(), CommonParameters.LOGINSTATUS, status);
    }

    public boolean getLoginStatus() {                   // 获取用户登录状态
        return Sputils.getSpBoolean(getApplicationContext(), CommonParameters.LOGINSTATUS, false);
    }

    public static List<LessonCategory> lessonCategory;       //课程分类对象的集合
    public static boolean isShowComment;                     //是否显示评论

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
