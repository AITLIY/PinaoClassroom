package com.yiyin.aobosh.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.UI.activitys.HomepageActivity;
import com.yiyin.aobosh.UI.activitys.mine.VipServiceActivity;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.FindOrderBean;
import com.yiyin.aobosh.commons.CommonParameters;
import com.yiyin.aobosh.commons.HttpURL;
import com.yiyin.aobosh.utils.SHA;
import com.yiyin.aobosh.utils.TimeUtils;
import com.yiyin.aobosh.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private Context mContext;
    private RequestQueue requestQueue;


    private static final int LOAD_DATA_SUCCESS = 201;
    private static final int LOAD_DATA_FAILE = 202;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    ToastUtil.show(mContext, "支付成功");
                    if (GlobalParameterApplication.attach.equals(CommonParameters.LESSON_ORDER)) {
                        startActivity(new Intent(mContext, HomepageActivity.class));
                    } else if (GlobalParameterApplication.attach.equals(CommonParameters.VIP_ORDER)) {
                        startActivity(new Intent(mContext, VipServiceActivity.class));
                    }

                    finish();
                    break;

                case LOAD_DATA_FAILE:

                    ToastUtil.show(mContext, "支付失败");
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
        setContentView(R.layout.activity_wxpay_entry);

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        GlobalParameterApplication.mWxApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtils.d("微信支付 onPayFinish, errCode = " + baseResp.errCode);

        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            int errCord = baseResp.errCode;
            if (errCord == 0) {
                LogUtils.d("微信支付 支付成功！");
                if (GlobalParameterApplication.attach.equals(CommonParameters.LESSON_ORDER)) {
                    LogUtils.i("CeatOrderActivity: WxpPayOrderQuery " + GlobalParameterApplication.attach + " " + GlobalParameterApplication.out_trade_no);
                    WxpPayOrderQuery(GlobalParameterApplication.out_trade_no,GlobalParameterApplication.attach);

                } else if (GlobalParameterApplication.attach.equals(CommonParameters.VIP_ORDER)){
                    LogUtils.i("CeatOrderActivity: WxpPayOrderQuery " + GlobalParameterApplication.attach + " " + GlobalParameterApplication.out_trade_no);
                    WxpPayOrderQuery(GlobalParameterApplication.out_trade_no,GlobalParameterApplication.attach);

                }

            } else {
                LogUtils.d("微信支付 支付失败");
                mHandler.sendEmptyMessage(LOAD_DATA_FAILE);
                finish();
            }
            //这里接收到了返回的状态码可以进行相应的操作，如果不想在这个页面操作可以把状态码存在本地然后finish掉这个页面，这样就回到了你调起支付的那个页面
            //获取到你刚刚存到本地的状态码进行相应的操作就可以了
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        GlobalParameterApplication.mWxApi.handleIntent(intent, this);
    }

    //--------------------------------------请求服务器数据-------------------------------------------

    // 查询订单
    private void WxpPayOrderQuery(final String out_trade_no,final String attach) {

        String url = HttpURL.WXPAY_ORDERQUERY_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("WXPayEntryActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
//                            mFindOrderBean = new Gson().fromJson(data, FindOrderBean.class);

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS);

                        } else {

                            mHandler.sendEmptyMessage(LOAD_DATA_FAILE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("WXPayEntryActivity: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Wxpayorderquery" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("DescFragment: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("out_trade_no", out_trade_no);
                    obj.put("attach", attach);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("WXPayEntryActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
