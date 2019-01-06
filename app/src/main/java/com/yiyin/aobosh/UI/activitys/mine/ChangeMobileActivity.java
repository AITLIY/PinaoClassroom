package com.yiyin.aobosh.UI.activitys.mine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.githang.statusbar.StatusBarCompat;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.commons.CommonParameters;
import com.yiyin.aobosh.commons.HttpURL;
import com.yiyin.aobosh.utils.NetworkUtils;
import com.yiyin.aobosh.utils.SHA;
import com.yiyin.aobosh.utils.TimeUtils;
import com.yiyin.aobosh.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeMobileActivity extends Activity implements View.OnClickListener {

    private Context mContext;
    private RequestQueue requestQueue;
    private UserInfo mUserInfo;
    private EditText new_mobile_et, captcha_et;
    private TextView old_mobile_et,get_captcha,change_mobile_commit;

    private static final int LOAD_DATA_SUCCESS1 = 101;
    private static final int LOAD_DATA_FAILE1 = 102;
    private static final int LOAD_DATA_SUCCESS2 = 201;
    private static final int LOAD_DATA_FAILE2 = 202;
    private static final int LOAD_DATA_FAILE21 = 203;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS1:

                    num1 = 60;
                    mHandler.postDelayed(myTask, 0);
                    break;

                case LOAD_DATA_FAILE1:

                    ToastUtil.show(mContext, "获取失败");
                    break;

                case LOAD_DATA_SUCCESS2:

                    startActivity(new Intent(mContext, ChangeMoileSuccessActivity.class));
                    break;

                case LOAD_DATA_FAILE2:

                    String text = (String) msg.obj;
                    LogUtils.i("RegisterActivity1: text " + text);
                    ToastUtil.show(mContext, text);
                    break;

                case LOAD_DATA_FAILE21:

                    ToastUtil.show(mContext, "修改失败");
                    break;

                case NET_ERROR:

                    ToastUtil.show(mContext, "网络异常,请稍后重试");
                    break;

            }
        }
    };

    private long num1;
    private Runnable myTask = new Runnable() {

        @Override
        public void run() {
            get_captcha.setText("重新发送("+num1+")");        // 提示剩余时间
            get_captcha.setEnabled(false);                    //禁止再次点击发送验证码

            num1--;                                           //默认最大为60每隔一秒发送一个
            if (num1 >= 0) {
                mHandler.postDelayed(this, 1000);
            } else {
                get_captcha.setText(R.string.get_captcha);
                get_captcha.setEnabled(true);
                mHandler.removeCallbacksAndMessages(null);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.app_title_bar), true);

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();

        init();
    }

    private void init() {

        initView();
        initListner();
    }


    private void initView() {

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new_mobile_et = findViewById(R.id.new_mobile_et);
        captcha_et = findViewById(R.id.captcha_et);
        old_mobile_et = findViewById(R.id.old_mobile_et);
        get_captcha = findViewById(R.id.get_captcha);
        change_mobile_commit = findViewById(R.id.change_mobile_commit);

        old_mobile_et.setText(mUserInfo.getMobile()); 
    }

    private void initListner() {

        get_captcha.setOnClickListener(this);
        change_mobile_commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        
        String mobile = new_mobile_et.getText().toString();
        
        switch (v.getId()) {

            case R.id.get_captcha:

                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtil.show(mContext,"当前无网络");
                    return;
                }

                getSmsCaptcha(mobile);

                break;
        
 
            case R.id.change_mobile_commit:

                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtil.show(mContext,"当前无网络");
                    return;
                }

                String captcha = captcha_et.getText().toString();

                if ("".equals(mobile) || "".equals(captcha)) {

                    ToastUtil.show(mContext, "手机号或验证码不能为空");
                    return;
                }
                
                changeMpbile(mUserInfo.getUid(),mUserInfo.getMobile(), mobile, captcha);

                break;
        }
    }

    //--------------------------------------请求服务器数据-------------------------------------------

    // 获取验证码
    private void getSmsCaptcha(final String mobile) {

        String url = HttpURL.SMS_SENDSMSMOBILE_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("ChangeMobileActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            JSONObject json = new JSONObject(data);
                            String captcha = json.getString("code");
                            LogUtils.i("ChangeMobileActivity: captcha " + captcha);

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS1);
                            return;
                        }
                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE1);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("ChangeMobileActivity: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Smssendsmsmobile" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("ChangeMobileActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("nmobile", mobile);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("ChangeMobileActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 请求修改手机
    private void changeMpbile(final int uid,final String mobile, final String nmobile, final String code) {

        String url = HttpURL.OAUTH_MODIFYMBL_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("ChangePasswordActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS2);

                        } else {

                            String msg = jsonObject.getString("msg");
                            LogUtils.i("ChangePasswordActivity: msg " + msg);
                            mHandler.obtainMessage(LOAD_DATA_FAILE2, msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE21);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("ChangePasswordActivity: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {
                    String token = "Oauthmodifymbl" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("ChangePasswordActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("mobile", mobile);
                    obj.put("nmobile", nmobile);
                    obj.put("code", code);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("ChangePasswordActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}