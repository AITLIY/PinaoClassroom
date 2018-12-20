package com.yiyin.aobosh.application;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.yiyin.aobosh.bean.LessonCategory;

import java.util.List;

/**
 * Created by ALIY on 2018/12/9 0009.
 * 全局信息设置
 */

public class GlobalParameterApplication extends Application {

    public static List<LessonCategory> lessonCategory;       //课程分类对象的集合


    private static GlobalParameterApplication instance;  //Application实例
    public static GlobalParameterApplication getInstance() {
        return instance;
    }

    private RequestQueue mRequestQueue;  //Volley网络请求队列
    public RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
            mRequestQueue.getCache().clear();
        }

        return mRequestQueue;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
