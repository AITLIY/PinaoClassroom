package com.yiyin.aobosh.activitys.mine;

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

public class ChangePasswordActivity extends Activity implements View.OnClickListener {

    private Context mContext;
    private RequestQueue requestQueue;
    private UserInfo mUserInfo;
    private EditText old_pwd_et,new_pwd_et,comfirm_pwd_et;
    private TextView change_pwd_commit;

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

                    ToastUtil.show(mContext, "修改密码成功");
                    startActivity(new Intent(mContext, HomepageActivity.class));
                    finish();
                    break;

                case LOAD_DATA_FAILE1:

                    String text = (String) msg.obj;
                    LogUtils.i("ChangePasswordActivity: text " + text);
                    ToastUtil.show(mContext, text);
                    break;

                case LOAD_DATA_FAILE2:

                    ToastUtil.show(mContext, "登录失败");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

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

        old_pwd_et =  (EditText) findViewById(R.id.old_pwd_et);
        new_pwd_et =  (EditText) findViewById(R.id.new_pwd_et);
        comfirm_pwd_et =  (EditText) findViewById(R.id.comfirm_pwd_et);

        change_pwd_commit =  (TextView) findViewById(R.id.change_pwd_commit);
    }

    private void initListner() {

        change_pwd_commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.change_pwd_commit:

                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtil.show(mContext,"当前无网络");
                    return;
                }

                String oldPwd = old_pwd_et.getText().toString();
                String newPwd = new_pwd_et.getText().toString();
                String comPwd = comfirm_pwd_et.getText().toString();

                if ("".equals(oldPwd)||"".equals(newPwd)||"".equals(comPwd)) {

                    ToastUtil.show(mContext,"密码不能为空");
                    return;
                }

                if (comPwd.length() < 6 || comPwd.length() > 12) {

                    ToastUtil.show(mContext,"密码长度应为6~12位");
                    return;
                }

                if (!newPwd.equals(comPwd)) {

                    ToastUtil.show(mContext,"两次新密码输入不一致");
                    return;
                }

                userLogin(mUserInfo.getUid(),mUserInfo.getMobile(),comPwd,oldPwd);

                break;
        }
    }

    //--------------------------------------请求服务器数据-------------------------------------------

    // 请求登录
    private void userLogin(final int uid,final String mobile, final String password, final String ypwd) {

        String url = HttpURL.OAUTH_MODIFYPSW_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("ChangePasswordActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS);

                        } else {

                            String msg = jsonObject.getString("msg");
                            LogUtils.i("ChangePasswordActivity: msg " + msg);
                            mHandler.obtainMessage(LOAD_DATA_FAILE1,msg).sendToTarget();
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
                LogUtils.e("ChangePasswordActivity: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(LOAD_DATA_FAILE2);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {
                    String token = "Oauthmodifypsw" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("ChangePasswordActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("mobile", mobile);
                    obj.put("password", password);
                    obj.put("ypwd", ypwd);
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
