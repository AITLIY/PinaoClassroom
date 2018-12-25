package com.yiyin.aobosh.activitys.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.application.GlobalParameterApplication;
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

public class RegisterActivity1 extends Activity  implements View.OnClickListener {

    private Context mContext;
    private RequestQueue requestQueue;

    private EditText register_phone_ed,register_captcha_ed;
    private Button register_get_captcha;
    private LinearLayout register_user_next;

    private static final int LOAD_DATA_SUCCESS1 = 101;
    private static final int LOAD_DATA_FAILE1 = 102;
    private static final int LOAD_DATA_SUCCESS2 = 201;
    private static final int LOAD_DATA_FAILE2 = 202;
    private static final int LOAD_DATA_FAILE21 = 203;

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

                    Intent intent = new Intent(mContext, RegisterActivity2.class);
                    String mobile = (String) msg.obj;
                    LogUtils.i("RegisterActivity1: mobile " + mobile);
                    intent.putExtra("mobile", mobile);
                    startActivity(intent);
                    break;

                case LOAD_DATA_FAILE2:

                    String text = (String) msg.obj;
                    LogUtils.i("RegisterActivity1: text " + text);
                    ToastUtil.show(mContext, text);
                    break;

                case LOAD_DATA_FAILE21:

                    ToastUtil.show(mContext, "验证失败");
                    break;
            }
        }
    };

    private long num1;
    private Runnable myTask = new Runnable() {

        @Override
        public void run() {
            register_get_captcha.setText("重新发送("+num1+")");        // 提示剩余时间
            register_get_captcha.setEnabled(false);                    //禁止再次点击发送验证码
         
            num1--;                                                    //默认最大为60每隔一秒发送一个
            if (num1 >= 0) {
                mHandler.postDelayed(this, 1000);
            } else {
                register_get_captcha.setText(R.string.get_captcha);
                register_get_captcha.setEnabled(true);
                mHandler.removeCallbacksAndMessages(null);
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        register_phone_ed =  (EditText) findViewById(R.id.register_phone_ed);
        register_captcha_ed =  (EditText) findViewById(R.id.register_captcha_ed);
        register_get_captcha =  (Button) findViewById(R.id.register_get_captcha);
        register_user_next =  (LinearLayout) findViewById(R.id.register_user_next);
    }

    private void initListner() {

        register_get_captcha.setOnClickListener(this);
        register_user_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        String phone = register_phone_ed.getText().toString();

        switch (v.getId()) {

            case R.id.register_get_captcha:

                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtil.show(mContext,"当前无网络");
                    return;
                }

                if ("".equals(phone)) {

                    ToastUtil.show(mContext,"手机号不能为空");
                    return;
                }
                
                getSmsCaptcha(phone);
                break;

            case R.id.register_user_next:

                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtil.show(mContext,"当前无网络");
                    return;
                }

                String captcha = register_captcha_ed.getText().toString();

                if ("".equals(phone)||"".equals(captcha)) {

                    ToastUtil.show(mContext,"手机号或验证码不能为空");
                    return;
                }

                checkSmsCaptcha(phone,captcha);
                break;

        }
    }

    //--------------------------------------请求服务器数据-------------------------------------------

    // 获取验证码
    private void getSmsCaptcha(final String mobile) {

        String url = HttpURL.SMS_SENDSMSCAPTCHA_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("RegisterActivity1: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            JSONObject json = new JSONObject(data);
                            String captcha = json.getString("code");
                            LogUtils.i("RegisterActivity1: captcha " + captcha);

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
                LogUtils.e("RegisterActivity1: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(LOAD_DATA_FAILE1);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Smssendsmscaptcha" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("RegisterActivity1: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("mobile", mobile);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("RegisterActivity1 json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 检查验证码
    private void checkSmsCaptcha(final String mobile, final String code) {

        String url = HttpURL.OAUTH_MODIFYMBL_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("RegisterActivity1: result2 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String msg = jsonObject.getString("msg");

                            if ("验证成功".equals(msg)) {

                                mHandler.obtainMessage(LOAD_DATA_SUCCESS2,mobile).sendToTarget();
                                return;
                            }

                            mHandler.obtainMessage(LOAD_DATA_FAILE2,msg).sendToTarget();
                            return;

                        } else if ("4004".equals(code)) {

                            String msg = jsonObject.getString("msg");
                            LogUtils.i("RegisterActivity1: msg " + msg);
                            mHandler.obtainMessage(LOAD_DATA_FAILE2,msg).sendToTarget();
                        } else {

                            mHandler.sendEmptyMessage(LOAD_DATA_FAILE21);
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
                LogUtils.e("RegisterActivity1: volleyError2 " + volleyError.toString());
                mHandler.sendEmptyMessage(LOAD_DATA_FAILE21);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Smschecksmscaptcha" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("RegisterActivity1: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("mobile", mobile);
                    obj.put("code", code);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("RegisterActivity1 json2 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }
}

