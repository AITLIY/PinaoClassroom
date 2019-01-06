package com.yiyin.aobosh.UI.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.commons.CommonParameters;
import com.yiyin.aobosh.commons.HttpURL;
import com.yiyin.aobosh.utils.SHA;
import com.yiyin.aobosh.utils.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends Activity {

    private Context mContext;
    private RequestQueue requestQueue;

    private static final int LOAD_DATA_SUCCESS = 101;
    private static final int LOAD_DATA_FAILE1 = 102;
    private static final int LOAD_DATA_FAILE2 = 103;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    GlobalParameterApplication.getInstance().setLoginStatus(true);
                    mHandler.postDelayed(new SplashTask(), 2000);
                    break;

                case LOAD_DATA_FAILE1:

                    GlobalParameterApplication.getInstance().clearUserInfo();   // 清空本地存储的用户信息
//                    new AlertDialog.Builder(mContext)
//                            .setTitle("提示")
//                            .setMessage("登录已失效，是否重新登录？")
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    Intent intent = new Intent(mContext, LoginActivity.class);
//                                    startActivity(intent);
//                                    finish();
//
//                                }
//                            })
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
                                    mHandler.postDelayed(new SplashTask(), 2000);
//                                }
//                            })
//                            .show();
                    break;

                case LOAD_DATA_FAILE2:

                    GlobalParameterApplication.getInstance().setLoginStatus(false);
                    mHandler.postDelayed(new SplashTask(), 2000);
                    break;
            }

        }
    };

    private class SplashTask implements Runnable {

        @Override
        public void run() {
            LogUtils.d("SplashActivity : start HomepageActivity");

            Intent intent = new Intent(mContext, HomepageActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView bg = findViewById(R.id.splash_bg);
        Glide.with(this).load(R.drawable.icon_bg_splash).centerCrop().into(bg);

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        UserInfo user = GlobalParameterApplication.getInstance().getUserInfo();

        if (user != null) {

            autoLogin(user.getToken());
        } else {
            mHandler.postDelayed(new SplashTask(), 2000);
        }

    }

    // 自动登录
    private void autoLogin(final String usertoken) {

        String url = HttpURL.INDEX_OAUTH_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("SplashActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS);

                        } else if ("4002".equals(code)){

                            mHandler.sendEmptyMessage(LOAD_DATA_FAILE1);
                        } else {

                            mHandler.sendEmptyMessage(LOAD_DATA_FAILE2);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE2);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("SplashActivity: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(LOAD_DATA_FAILE2);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Indexoauth" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("SplashActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("token", usertoken);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("SplashActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
