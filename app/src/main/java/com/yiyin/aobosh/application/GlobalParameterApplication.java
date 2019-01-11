package com.yiyin.aobosh.application;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.util.LogUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yiyin.aobosh.bean.LessonCategory;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.commons.CommonParameters;
import com.yiyin.aobosh.utils.SpUtils;

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
        SpUtils.putObject(getApplicationContext(), userInfo);
    }

    public UserInfo getUserInfo() {                     //获取本地的用户对象
        return SpUtils.getObject(getApplicationContext(), UserInfo.class);
    }

    public void clearUserInfo() {                       // 清空本地存储的用户信息
        SpUtils.clear(getApplicationContext());
    }

    public void setLoginStatus(boolean status) {        // 设置用户登录状态

        if (status)
            SpUtils.put(getApplicationContext(), CommonParameters.LOGINSTATUS, status);
        else
            SpUtils.put(getApplicationContext(), CommonParameters.LOGINSTATUS, status);
    }

    public boolean getLoginStatus() {                   // 获取用户登录状态
        return SpUtils.getSpBoolean(getApplicationContext(), CommonParameters.LOGINSTATUS, false);
    }

    public static List<LessonCategory> lessonCategory;       //课程分类对象的集合
    public static boolean isShowComment;                     //是否显示评论
    public static boolean isHasOrder;                        //是否已存在订单
    public static String attach;                             //订单类型
    public static String body;                               //购买类型
    public static String out_trade_no;                       //订单号

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registToWX();
    }

    public static IWXAPI mWxApi;

    private void registToWX() {
        LogUtils.d("微信登录 : registToWX()");
        //AppConst.WEIXIN.APP_ID是指你应用在微信开放平台上的AppID，记得替换。
        mWxApi = WXAPIFactory.createWXAPI(this, CommonParameters.APPID, false);
        // 将该app注册到微信
        mWxApi.registerApp("wx3f889385b49ca1b8");
    }

}
