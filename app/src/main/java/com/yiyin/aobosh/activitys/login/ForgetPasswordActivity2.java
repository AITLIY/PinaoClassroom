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

public class ForgetPasswordActivity2 extends Activity  implements View.OnClickListener {

    private Context mContext;
    private RequestQueue requestQueue;

    private EditText new_password1_ed,new_password2_ed;
    private LinearLayout forget_password_commit;

    private static final int LOAD_DATA_SUCCESS = 101;
    private static final int LOAD_DATA_FAILE1 = 102;
    private static final int LOAD_DATA_FAILE2 = 103;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    startActivity(new Intent(mContext, RegisterSuccessActivity.class));
                    break;

                case LOAD_DATA_FAILE1:

                    String text = (String) msg.obj;
                    LogUtils.i("RegisterActivity2: text " + text);
                    ToastUtil.show(mContext, text);
                    break;

                case LOAD_DATA_FAILE2:

                    ToastUtil.show(mContext,"注册失败");
                    break;

            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password2);

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

        new_password1_ed =  (EditText) findViewById(R.id.new_password1_ed);
        new_password2_ed =  (EditText) findViewById(R.id.new_password2_ed);
        forget_password_commit =  (LinearLayout) findViewById(R.id.forget_password_commit);
    }

    private void initListner() {

        forget_password_commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.forget_password_commit:
                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtil.show(mContext,"当前无网络");
                    return;
                }

                startActivity(new Intent(mContext, PasswordSuccessActivity.class));
                break;
        }
    }

    // 用户注册
    private void userRegister(final String mobile, final String name,final String password) {

        String url = HttpURL.OAUTH_REGISTER_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("RegisterActivity2: result2 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS);

                        }else if ("4004".equals(code)) {

                            String msg = jsonObject.getString("msg");
                            LogUtils.i("RegisterActivity2: msg " + msg);
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
                LogUtils.e("RegisterActivity2: volleyError2 " + volleyError.toString());
                mHandler.sendEmptyMessage(LOAD_DATA_FAILE2);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Oauthregister" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("RegisterActivity2: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("mobile", mobile);
                    obj.put("name", name);
                    obj.put("password", password);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("RegisterActivity2 json2 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }
}
