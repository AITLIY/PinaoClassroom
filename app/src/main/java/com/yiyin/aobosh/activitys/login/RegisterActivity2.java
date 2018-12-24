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
import com.yiyin.aobosh.utils.SHA;
import com.yiyin.aobosh.utils.TimeUtils;
import com.yiyin.aobosh.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity2 extends Activity  implements View.OnClickListener {
    
    private Context mContext;
    private RequestQueue requestQueue;

    private EditText register_user_ed,register_password1_ed,register_password2_ed;
    private LinearLayout register_user_commit;
    private String mMobile;

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
        setContentView(R.layout.activity_register2);

        mContext = this;
        init();
    }

    private void init() {

        initView();
        initListner();
        initDate();
    }

    private void initView() {

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        register_user_ed =  (EditText) findViewById(R.id.register_user_ed);
        register_password1_ed =  (EditText) findViewById(R.id.register_password1_ed);
        register_password2_ed =  (EditText) findViewById(R.id.register_password2_ed);
        register_user_commit =  (LinearLayout) findViewById(R.id.register_user_commit);
    }

    private void initListner() {

        register_user_commit.setOnClickListener(this);
    }

    private void initDate() {
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        Intent intent = getIntent();
        if (intent != null){
            mMobile = intent.getStringExtra("mobile");
            LogUtils.i("RegisterActivity2: getStringExtra mobile " + mMobile);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.register_user_commit:

                String userName = register_user_ed.getText().toString();
                String passWord1 = register_password1_ed.getText().toString();
                String passWord2 = register_password2_ed.getText().toString();

                if ("".equals(userName)||"".equals(passWord1)||"".equals(passWord2)) {

                    ToastUtil.show(mContext,"用户名或密码不能为空");
                    return;
                }

                if (!passWord1.equals(passWord2)) {

                    ToastUtil.show(mContext,"两次密码输入不一致");
                    return;
                }

                userRegister(mMobile,userName,passWord2);
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
