package com.yiyin.aobosh.activitys.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.activitys.HomepageActivity;
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

public class LoginActivity extends Activity implements View.OnClickListener {

    private Context mContext;
    private RequestQueue requestQueue;
    private EditText username_ed,password_ed;
    private LinearLayout login_ll,weixin_login_ll;
    private TextView forget_password,register_new_user;

    private static final int LOAD_DATA_SUCCESS = 101;
    private static final int LOAD_DATA_FAILE1 = 102;
    private static final int LOAD_DATA_FAILE2 = 103;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    ToastUtil.show(mContext, "登录成功");
                    GlobalParameterApplication.getInstance().setLoginStatus(true);
                    startActivity(new Intent(mContext, HomepageActivity.class));
                    finish();
                    break;

                case LOAD_DATA_FAILE1:

                    String text = (String) msg.obj;
                    LogUtils.i("LoginActivity: text " + text);
                    ToastUtil.show(mContext, text);
                    break;

                case LOAD_DATA_FAILE2:

                    ToastUtil.show(mContext, "登录失败");
                    break;

                case NET_ERROR:

                    ToastUtil.show(mContext, "网络异常,请稍后重试");
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
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

        username_ed = findViewById(R.id.username_ed);
        password_ed = findViewById(R.id.password_ed);
        login_ll = findViewById(R.id.login_ll);
        weixin_login_ll = findViewById(R.id.weixin_login_ll);
        forget_password = findViewById(R.id.forget_password);
        register_new_user = findViewById(R.id.register_new_user);
    }

    private void initListner() {

        login_ll.setOnClickListener(this);
        weixin_login_ll.setOnClickListener(this);
        forget_password.setOnClickListener(this);
        register_new_user.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.login_ll:

                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtil.show(mContext,"当前无网络");
                    return;
                }

                String userName = username_ed.getText().toString();
                String passWord = password_ed.getText().toString();

                if ("".equals(userName)||"".equals(passWord)) {

                    ToastUtil.show(mContext,"用户账号或密码不能为空");
                    return;
                }

                if (passWord.length() < 6 || passWord.length() > 12) {

                    ToastUtil.show(mContext,"密码长度应为6~12位");
                    return;
                }

                userLogin(userName, passWord);

                break;

            case R.id.weixin_login_ll:

                break;

            case R.id.forget_password:

                startActivity(new Intent(mContext, ForgetPasswordActivity1.class));
                break;

            case R.id.register_new_user:

                startActivity(new Intent(mContext, RegisterActivity1.class));
                break;
        }
    }

    //--------------------------------------请求服务器数据-------------------------------------------

    // 请求登录
    private void userLogin(final String mobile, final String password) {

        String url = HttpURL.OAUTH_LOGIN_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("LoginActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            UserInfo userInfo = new Gson().fromJson(data, UserInfo.class);

                            GlobalParameterApplication.getInstance().setUserInfo(userInfo);

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS);

                        } else if ("4004".equals(code)) {

                            String msg = jsonObject.getString("msg");
                            LogUtils.i("LoginActivity: msg " + msg);
                            mHandler.obtainMessage(LOAD_DATA_FAILE1,msg).sendToTarget();
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
                LogUtils.e("LoginActivity: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Oauthlogin" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("LoginActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("mobile", mobile);
                    obj.put("password", password);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("LoginActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }
}
