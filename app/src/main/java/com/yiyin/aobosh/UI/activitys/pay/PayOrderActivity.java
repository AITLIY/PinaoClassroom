package com.yiyin.aobosh.UI.activitys.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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
import com.yiyin.aobosh.bean.LessonDetail;
import com.yiyin.aobosh.bean.RecommendLesson;
import com.yiyin.aobosh.bean.UserInfo;
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

    private int lessonID;
    private TextView bookname_iv,ordersn_iv,b_iv,marketprice_tv,price_tv;

    private FindOrderBean mFindOrderBean;

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

                    setViewText();
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
        bookname_iv = findViewById(R.id.bookname_iv);
        ordersn_iv = findViewById(R.id.ordersn_iv);
        b_iv = findViewById(R.id.b_iv);
        marketprice_tv = findViewById(R.id.marketprice_tv);
        price_tv = findViewById(R.id.price_tv);
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();

        Intent intent = getIntent();
        if (intent != null) {
            lessonID = intent.getIntExtra("lessonid",0);
            LogUtils.i("PayOrderActivity: PayOrderActivity " + lessonID);
        }
        getFindOrder(mUserInfo.getUid(), lessonID);

    }

    private void setViewText() {
        bookname_iv.setText(mFindOrderBean.getBookname());
        ordersn_iv.setText(mFindOrderBean.getOrdersn());
//        b_iv.setText(mFindOrderBean.getBookname());
        marketprice_tv.setText("¥"+mFindOrderBean.getMarketprice());
        price_tv.setText("¥"+mFindOrderBean.getPrice());

    }



    //--------------------------------------请求服务器数据-------------------------------------------

    // 我的课程列表点击后订单信息
    private void getFindOrder(final int uid, final int lessonid) {

        String url = HttpURL.LESSONSON_FINDORDER_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("PayOrderActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            mFindOrderBean = new Gson().fromJson(data, FindOrderBean.class);

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
                LogUtils.e("PayOrderActivity: volleyError1 " + volleyError.toString());
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

                LogUtils.i("PayOrderActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
