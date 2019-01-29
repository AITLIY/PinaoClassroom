package com.yiyin.aobosh.UI.activitys.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.FindOrderBean;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.bean.VipOrderBean;
import com.yiyin.aobosh.commons.CommonParameters;
import com.yiyin.aobosh.commons.HttpURL;
import com.yiyin.aobosh.utils.SHA;
import com.yiyin.aobosh.utils.TimeUtils;
import com.yiyin.aobosh.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CeatOrderActivity extends Activity {

    private Context mContext;
    private RequestQueue requestQueue;
    private UserInfo mUserInfo;

    private int lessonID;
    private int vipID;
    private TextView bookname_iv,ordersn_iv,b_iv,marketprice_tv,price_tv,to_pay;

    private FindOrderBean mFindOrderBean;
    private VipOrderBean mVipOrderBean;

    private String mOutTradeNO;
    private String mTotalFee;

    private static final int LOAD_DATA_SUCCESS = 101;
    private static final int LOAD_DATA_FAILE = 102;
    private static final int LOAD_DATA_SUCCESS2 = 201;
    private static final int LOAD_DATA_FAILE2 = 202;
    private static final int LOAD_DATA_SUCCESS3 = 301;
    private static final int LOAD_DATA_FAILE3 = 302;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    setViewText();
                    break;

                case LOAD_DATA_FAILE:

                    ToastUtil.show(mContext, "查询数据失败");
                    break;

                case LOAD_DATA_SUCCESS2:

                    setViewText();
                    break;

                case LOAD_DATA_FAILE2:

                    ToastUtil.show(mContext, "查询数据失败");
                    break;

                case LOAD_DATA_SUCCESS3:

                    setViewText2();
                    break;

                case LOAD_DATA_FAILE3:

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
        setContentView(R.layout.activity_creat_order);
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

        bookname_iv = findViewById(R.id.bookname_iv);
        ordersn_iv = findViewById(R.id.ordersn_iv);
        b_iv = findViewById(R.id.b_iv);
        marketprice_tv = findViewById(R.id.marketprice_tv);
        price_tv = findViewById(R.id.price_tv);
        to_pay = findViewById(R.id.to_pay);

        to_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(mContext,PayOrderActivity.class);
                LogUtils.i("CeatOrderActivity: mOutTradeNO " + mOutTradeNO);
                LogUtils.i("CeatOrderActivity: mTotalFee " + mTotalFee);
                intent1.putExtra("out_trade_no",mOutTradeNO);
                intent1.putExtra("total_fee",mTotalFee);
                startActivity(intent1);
                finish();
            }
        });
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();

        Intent intent = getIntent();

        if (intent == null) {
            return;
        }

        if (GlobalParameterApplication.attach.equals(CommonParameters.LESSON_ORDER)){
            GlobalParameterApplication.body = CommonParameters.BUY_LESSON;
            lessonID = intent.getIntExtra("lessonid",0);
            if (!GlobalParameterApplication.isHasOrder) {
                LogUtils.i("CeatOrderActivity: CreatOrder " + lessonID);
                CreatOrder(mUserInfo.getUid(), lessonID);

            } else {
                LogUtils.i("CeatOrderActivity: getFindOrder " + lessonID);
                getFindOrder(mUserInfo.getUid(), lessonID);
            }

        } else if (GlobalParameterApplication.attach.equals(CommonParameters.VIP_ORDER)){
            GlobalParameterApplication.body = CommonParameters.BUY_VIP;
            LogUtils.i("CeatOrderActivity: buyVip " + vipID);
            vipID = intent.getIntExtra("levelid",0);
            buyVip(mUserInfo.getUid(), vipID);
        }


    }

    private void setViewText() {
        bookname_iv.setText(mFindOrderBean.getBookname());
        ordersn_iv.setText(mFindOrderBean.getOrdersn());
//        b_iv.setText(mFindOrderBean.getBookname());
        marketprice_tv.setText("¥"+mFindOrderBean.getMarketprice());
        price_tv.setText("¥"+mFindOrderBean.getPrice());

    }

    private void setViewText2() {
        bookname_iv.setText(mVipOrderBean.getLevel_name());
        ordersn_iv.setText(mVipOrderBean.getOrdersn());
        marketprice_tv.setText("¥"+mVipOrderBean.getVipmoney());
        price_tv.setText("¥"+mVipOrderBean.getVipmoney());

    }


    //--------------------------------------请求服务器数据-------------------------------------------

    // 创建课程订单信息
    private void CreatOrder(final int uid, final int lessonid) {

        String url = HttpURL.LESSONSON_CREATEORDER_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("CeatOrderActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            mFindOrderBean = new Gson().fromJson(data, FindOrderBean.class);
                            mOutTradeNO = mFindOrderBean.getOrdersn();
                            mTotalFee = mFindOrderBean.getPrice();
                            GlobalParameterApplication.out_trade_no = mFindOrderBean.getOrdersn();

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS);

                        } else if ("4009".equals(code)){

                            getFindOrder(mUserInfo.getUid(), lessonID);

                        }else {

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
                LogUtils.e("CeatOrderActivity: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Lessonsoncreateorder" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("DescFragment: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("lessonid", lessonid);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("CeatOrderActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 我的课程列表点击后订单信息
    private void getFindOrder(final int uid, final int lessonid) {

        String url = HttpURL.LESSONSON_FINDORDER_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("getFindOrder: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            mFindOrderBean = new Gson().fromJson(data, FindOrderBean.class);
                            mOutTradeNO = mFindOrderBean.getOrdersn();
                            mTotalFee = mFindOrderBean.getPrice();
                            GlobalParameterApplication.out_trade_no = mFindOrderBean.getOrdersn();

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS2);

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
                LogUtils.e("getFindOrder: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Lessonsonfindorder" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("DescFragment: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("lessonid", lessonid);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("getFindOrder json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 购买会员时生成的订单信息
    private void buyVip(final int uid,final int levelid) {

        String url = HttpURL.VIP_BUYVIP_URL;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("VipServiceActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            mVipOrderBean = new Gson().fromJson(data, VipOrderBean.class);
                            mOutTradeNO = mVipOrderBean.getOrdersn();
                            mTotalFee = mVipOrderBean.getVipmoney();
                            GlobalParameterApplication.out_trade_no = mVipOrderBean.getOrdersn();

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS3);

                        }  else {

                            mHandler.sendEmptyMessage(LOAD_DATA_FAILE3);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE3);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("VipServiceActivity: volleyError1 " + volleyError.getMessage());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Vipbuyvip" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("VipServiceActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("levelid", levelid);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("VipServiceActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
