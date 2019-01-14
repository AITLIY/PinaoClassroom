package com.yiyin.aobosh.UI.activitys.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.bean.WxPayBean;
import com.yiyin.aobosh.commons.CommonParameters;
import com.yiyin.aobosh.commons.HttpURL;
import com.yiyin.aobosh.utils.SHA;
import com.yiyin.aobosh.utils.TimeUtils;
import com.yiyin.aobosh.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PayOrderActivity extends Activity {

    private Context mContext;
    private RequestQueue requestQueue;
    private UserInfo mUserInfo;

    private LinearLayout pay_ll;
    private WxPayBean mWxPayBean;

    private static final int LOAD_DATA_SUCCESS = 101;
    private static final int LOAD_DATA_FAILE = 102;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    wxPay();
                    break;

                case LOAD_DATA_FAILE:

                    ToastUtil.show(mContext, "查询数据失败");
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
        setContentView(R.layout.activity_pay_order);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        init();
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pay_ll = findViewById(R.id.pay_ll);

        pay_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                if (intent != null) {
                    String out_trade_no = intent.getStringExtra("out_trade_no");
                    String total_fee = intent.getStringExtra("total_fee");

                    total_fee = total_fee.replace(".","");
                    int fee = Integer.parseInt(total_fee);
                    total_fee = fee+"";

                    LogUtils.i("PayOrderActivity: out_trade_no " + out_trade_no);
                    LogUtils.i("PayOrderActivity: total_fee " + total_fee);
                    getPrePayOrder(GlobalParameterApplication.body,out_trade_no,"1",GlobalParameterApplication.attach);
                }
            }
        });
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();

    }

    private void wxPay() {

        PayReq req = new PayReq();//PayReq就是订单信息对象
        //给req对象赋值
        req.appId = mWxPayBean.getAppid();//APPID
        req.partnerId = mWxPayBean.getPartnerid();//    商户号
        req.prepayId = mWxPayBean.getPrepayid();//  预付款ID
        req.nonceStr = mWxPayBean.getNoncestr();//随机数
        req.timeStamp = mWxPayBean.getTimestamp()+"";//时间戳
        req.packageValue = "Sign=WXPay";//固定值Sign=WXPay
        req.sign = mWxPayBean.getSign();//签名
        GlobalParameterApplication.mWxApi.sendReq(req);
        finish();
    }

    // 购买会员订单支付状态
    private void getPrePayOrder(final String body,final String out_trade_no,final String total_fee,final String attach) {

        String url = HttpURL.WXPAY_GETPREPAYORDER_URL;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("PayOrderActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");

                            mWxPayBean = new Gson().fromJson(data, WxPayBean.class);
                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS);

                        }  else {

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
                LogUtils.e("PayOrderActivity: volleyError1 " + volleyError.getMessage());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Wxpaygetprepayorder" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("VipServiceActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("body", body);
                    obj.put("out_trade_no", out_trade_no);
                    obj.put("total_fee", total_fee);
                    obj.put("attach", attach);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("PayOrderActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }


}
