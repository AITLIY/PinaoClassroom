package com.yiyin.aobosh.UI.activitys.mine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.adapter.LevelVipAdapter;
import com.yiyin.aobosh.adapter.MemberVipAdapter;
import com.yiyin.aobosh.adapter.VipOrderAdapter;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.bean.VipOrderBean;
import com.yiyin.aobosh.bean.VipShow;
import com.yiyin.aobosh.commons.CommonParameters;
import com.yiyin.aobosh.commons.HttpURL;
import com.yiyin.aobosh.utils.SHA;
import com.yiyin.aobosh.utils.TimeUtils;
import com.yiyin.aobosh.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VipServiceActivity extends Activity {

    private Context mContext;
    private RequestQueue requestQueue;
    private UserInfo mUserInfo;

    private List<VipShow.MemberVipListBean> mMemberVipListBeans;    // 我的会员
    private List<VipShow.LevelListBean> mLevelListBeans;            // 会员类别
    private List<VipOrderBean> mVipOrderBeans;                      // 会员订单

    private RecyclerView Member_list_rv,level_list_rv;
    private TextView is_vip,not_vip;
    private LinearLayout have_vip_card;

    private MemberVipAdapter mAdapter1;
    private LevelVipAdapter mAdapter2;

    private VipOrderAdapter mAdapter3;
    private RecyclerView order_list_rv;

    //    private VipOrderAdapter2 mAdapter3;
    //    private ListView order_list_rv;

    private static final int LOAD_DATA_SUCCESS1 = 101;
    private static final int LOAD_DATA_FAILE1 = 102;
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

                case LOAD_DATA_SUCCESS1:

                    showVipUI(true);
                    break;

                case LOAD_DATA_FAILE1:

                    showVipUI(false);
                    break;

                case LOAD_DATA_SUCCESS2:

                    initVipDataView();
                    break;

                case LOAD_DATA_FAILE2:

                    break;

                case LOAD_DATA_SUCCESS3:

                    initVipDataView2();
                    break;

                case LOAD_DATA_FAILE3:

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
        setContentView(R.layout.activity_vip_service);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.app_title_bar), true);

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

        is_vip = findViewById(R.id.is_vip);
        not_vip = findViewById(R.id.not_vip);
        have_vip_card = findViewById(R.id.have_vip_card);
        Member_list_rv = findViewById(R.id.Member_list_rv);
        level_list_rv = findViewById(R.id.level_list_rv);
        order_list_rv = findViewById(R.id.order_list_rv);

        have_vip_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,VipCardActivity.class));
                finish();
            }
        });

//        order_list_rv.setLayoutManager(new LinearLayoutManager(this){
//            @Override
//            public boolean canScrollVertically() {
//                //解决ScrollView里存在多个RecyclerView时滑动卡顿的问题
//                //如果你的RecyclerView是水平滑动的话可以重写canScrollHorizontally方法
//                return false;
//            }
//        });
//        //解决数据加载不完的问题
//        order_list_rv.setNestedScrollingEnabled(false);
//        order_list_rv.setHasFixedSize(true);
//        //解决数据加载完成后, 没有停留在顶部的问题
//        order_list_rv.setFocusable(false);

    }

    private void initData() {

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();

        mMemberVipListBeans = new ArrayList<>();
        mLevelListBeans = new ArrayList<>();
        mVipOrderBeans = new ArrayList<>();

        getVipBuy(mUserInfo.getUid());
        getVipShow(mUserInfo.getUid());
        getVipOrder(mUserInfo.getUid());
    }

    private void showVipUI(boolean isVip) {

        if (isVip) {

            is_vip.setVisibility(View.VISIBLE);
            not_vip.setVisibility(View.GONE);
        } else {

            is_vip.setVisibility(View.GONE);
            not_vip.setVisibility(View.VISIBLE);
        }
    }

    private void initVipDataView() {

        mAdapter1 = new MemberVipAdapter(mMemberVipListBeans);
        Member_list_rv.setLayoutManager(new LinearLayoutManager(mContext));
        Member_list_rv.setAdapter(mAdapter1);

        mAdapter2 = new LevelVipAdapter(mLevelListBeans);
        level_list_rv.setLayoutManager(new LinearLayoutManager(mContext));
        level_list_rv.setAdapter(mAdapter2);
    }

    private void initVipDataView2() {

        mAdapter3 = new VipOrderAdapter(mVipOrderBeans);
//        mAdapter3 = new VipOrderAdapter2(mContext,mVipOrderBeans);
        order_list_rv.setLayoutManager(new LinearLayoutManager(mContext));
        order_list_rv.setAdapter(mAdapter3);

    }

    //--------------------------------------请求服务器数据-------------------------------------------

    // 购买会员订单支付状态
    private void getVipBuy(final int uid) {

        String url = HttpURL.VIP_BUY_URL;
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

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS1);

                        } else if ("4100".equals(code)){

                            mHandler.sendEmptyMessage(LOAD_DATA_FAILE1);
                        } else {

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
                LogUtils.e("VipServiceActivity: volleyError1 " + volleyError.getMessage());
                mHandler.sendEmptyMessage(LOAD_DATA_FAILE1);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Vipbuy" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("VipServiceActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
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
    
    // 获取会员等级列表和用户会员等级
    private void getVipShow(final int uid) {

        String url = HttpURL.VIP_SHOW_URL;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("VipServiceActivity: result2 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            VipShow vipShow = new Gson().fromJson(data, VipShow.class);

                            mLevelListBeans = vipShow.getLevel_list();
                            mMemberVipListBeans = vipShow.getMemberVip_list();

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS2);
                            return;
                        }

                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE2);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE2);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("VipServiceActivity: volleyError2 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {
                    String token = "Vipshow" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("VipServiceActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("VipServiceActivity json2 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // VIP订单
    private void getVipOrder(final int uid) {

        String url = HttpURL.VIP_VIPORDER_URL;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("VipServiceActivity: result3 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            mVipOrderBeans = new Gson().fromJson(data, new TypeToken<List<VipOrderBean>>() {}.getType());

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS3);
                            return;
                        }

                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE3);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE3);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("VipServiceActivity: volleyError3 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {
                    String token = "Vipviporder" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("VipServiceActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("VipServiceActivity json3 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }
}
