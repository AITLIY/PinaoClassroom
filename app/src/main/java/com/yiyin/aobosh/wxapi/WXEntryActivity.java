package com.yiyin.aobosh.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.UI.activitys.HomepageActivity;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.bean.WXUserBean;
import com.yiyin.aobosh.commons.CommonParameters;
import com.yiyin.aobosh.commons.HttpURL;
import com.yiyin.aobosh.utils.SHA;
import com.yiyin.aobosh.utils.TimeUtils;
import com.yiyin.aobosh.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private Context mContext;
    private RequestQueue requestQueue;
    private WXUserBean mWXUserBean;

    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    private static final int LOAD_DATA_SUCCESS1 = 101;
    private static final int LOAD_DATA_FAILE1 = 102;
    private static final int LOAD_DATA_SUCCESS2 = 201;
    private static final int LOAD_DATA_FAILE2 = 202;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS1:

                    wxLogin(mWXUserBean);
                    break;

                case LOAD_DATA_FAILE1:

                    ToastUtil.show(mContext, "登录失败");
                    break;

                case LOAD_DATA_SUCCESS2:

                    ToastUtil.show(mContext, "登录成功");
                    GlobalParameterApplication.getInstance().setLoginStatus(true);
                    startActivity(new Intent(mContext, HomepageActivity.class));
                    finish();
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
        setContentView(R.layout.activity_wxentry);

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        //如果没回调onResp，八成是这句没有写
        GlobalParameterApplication.mWxApi.handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    //app发送消息给微信，处理返回消息的回调
    @Override
    public void onResp(BaseResp resp) {
        LogUtils.d("微信登录 : onResp()");
        LogUtils.d(resp.errStr);
        LogUtils.d("错误码 : " + resp.errCode + "");
        switch (resp.errCode) {

            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (RETURN_MSG_TYPE_SHARE == resp.getType()) ToastUtil.show(mContext,"分享失败");
                else ToastUtil.show(mContext,"登录失败");
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        //拿到了微信返回的code,立马再去请求access_token
                        String code = ((SendAuth.Resp) resp).code;
                        LogUtils.d("code = " + code);
                        getSession(code);
                        //就在这个地方，用网络库什么的或者自己封的网络api，发请求去咯，注意是get请求

                        break;

                    case RETURN_MSG_TYPE_SHARE:
                        ToastUtil.show(mContext,"微信分享成功");
                        finish();
                        break;
                }
                break;
        }
    }


    //--------------------------------------请求服务器数据-------------------------------------------


    // 微信授权
    private void getSession(final String code) {

        String url = HttpURL.OAUTH_GETSESSION_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("WXEntryActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            mWXUserBean = new Gson().fromJson(data, WXUserBean.class);

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS1);

                        }  else {

                            mHandler.sendEmptyMessage(LOAD_DATA_FAILE1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE1);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("WXEntryActivity: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Oauthgetsession" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("WXEntryActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("code", code);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("WXEntryActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 微信登录
    private void wxLogin(final WXUserBean wxUserBean) {

        String url = HttpURL.OAUTH_WXLOGIN_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("WXEntryActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            UserInfo userInfo = new Gson().fromJson(data, UserInfo.class);

                            GlobalParameterApplication.getInstance().setUserInfo(userInfo);

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS2);

                        }  else {

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
                LogUtils.e("WXEntryActivity: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Oauthwxlogin" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("WXEntryActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("unionid", wxUserBean.getUnionid());
                    obj.put("openid", wxUserBean.getOpenid());
                    obj.put("sex", wxUserBean.getSex());
                    obj.put("city", wxUserBean.getCity());
                    obj.put("province", wxUserBean.getProvince());
                    obj.put("headimgurl", wxUserBean.getHeadimgurl());
                    obj.put("privilege", wxUserBean.getPrivilege());
                    obj.put("nickname", wxUserBean.getNickname());
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("WXEntryActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }
    
    
}

