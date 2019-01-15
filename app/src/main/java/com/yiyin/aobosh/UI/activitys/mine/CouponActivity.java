package com.yiyin.aobosh.UI.activitys.mine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.adapter.CouponAdapter;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.CouponBean;
import com.yiyin.aobosh.bean.UserInfo;
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

public class CouponActivity extends Activity implements View.OnClickListener{

    private Context mContext;
    private RequestQueue requestQueue;
    private UserInfo mUserInfo;
    private RelativeLayout all_lesson_ll,wait_payment_ll,already_payment_ll;
    private TextView all_lesson_tv,wait_payment_tv,already_payment_tv;
    private View all_lesson_v,wait_payment_v,already_payment_v;

    private PullToRefreshListView lesson_item_list;            // 课程列表容器
    private ArrayList<CouponBean> mLessonSearches;          //课程搜索结果的集合
    private ArrayList<CouponBean> mShowList;                //课程显示结果的集合
    private CouponAdapter adapter;

    private static final int SEARCH_LESSON_PARAMETER  = 10;        //参数查询
    private static final int SEARCH_LESSON_PULL_UP = 20;           //上拉加载
    private int mSearchType = 10;  // 查询的标志
    private int page = 1;
    private String Current_type = CommonParameters.VALID;                // 当前类型

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;
    private static final int NET_ERROR = 404;


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    if (mSearchType==SEARCH_LESSON_PARAMETER) {

                        if (mLessonSearches.size()>0){
                            setViewForResult(true,"");

                        } else {
                            setViewForResult(false,"您还没有获得任何优惠券~");
                        }
                    }
                    break;

                case LOAD_DATA1_FAILE:

                    lesson_item_list.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lesson_item_list.onRefreshComplete();
                            setViewForResult(false,"查询数据失败~");
                        }
                    }, 1000);
                    break;

                case NET_ERROR:

                    lesson_item_list.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lesson_item_list.onRefreshComplete();
                            setViewForResult(false,"网络异常,请稍后重试~");
                        }
                    }, 1000);
                    break;
            }
            upDataLessonListView();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
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

        all_lesson_ll = findViewById(R.id.all_lesson_ll);
        wait_payment_ll = findViewById(R.id.wait_payment_ll);
        already_payment_ll = findViewById(R.id.already_payment_ll);

        all_lesson_tv = findViewById(R.id.all_lesson_tv);
        wait_payment_tv = findViewById(R.id.wait_payment_tv);
        already_payment_tv = findViewById(R.id.already_payment_tv);

        all_lesson_v = findViewById(R.id.all_lesson_v);
        wait_payment_v = findViewById(R.id.wait_payment_v);
        already_payment_v = findViewById(R.id.already_payment_v);

        all_lesson_ll.setOnClickListener(this);
        wait_payment_ll.setOnClickListener(this);
        already_payment_ll.setOnClickListener(this);

        initPullListView();
    }

    private void initData() {

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mShowList = new ArrayList<>();
        adapter = new CouponAdapter(mContext, mShowList);
        lesson_item_list.setAdapter(adapter);
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();
        getLessonData(mUserInfo.getUid(), Current_type);
    }


    // 初始化列表
    private void initPullListView() {

        lesson_item_list = findViewById(R.id.lesson_item_list);
        setListView();

        lesson_item_list.setMode(PullToRefreshBase.Mode.BOTH);
        lesson_item_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {  //拉动时
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page= 1;

                mSearchType = SEARCH_LESSON_PARAMETER;
                getLessonData(mUserInfo.getUid(),Current_type); // 下拉刷新搜索
                LogUtils.i("CouponActivity: onPullDownToRefresh 下拉" + page + "页");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;

                mSearchType = SEARCH_LESSON_PULL_UP;
                //                 getLessonData(mUserInfo.getUid(),CommonParameters.UNIACID); // 上拉加载搜索
                lesson_item_list.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lesson_item_list.onRefreshComplete();
                        ToastUtil.show(mContext,"没有更多结果");
                    }
                }, 1000);
                LogUtils.i("CouponActivity: onPullUpToRefresh 上拉" + page + "页");
            }
        });

        lesson_item_list.setOnItemClickListener(new AdapterView.OnItemClickListener() { //点击item时
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        lesson_item_list.setOnScrollListener(new AbsListView.OnScrollListener() {  //列表滑动时
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int tempPos = lesson_item_list.getRefreshableView().getFirstVisiblePosition();

                //                if (tempPos > 0) {
                //                    goTop.setVisibility(View.VISIBLE);
                //                } else {
                //                    goTop.setVisibility(View.GONE);
                //                }

            }
        });

        //        goTop.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                lesson_item_list.getRefreshableView().smoothScrollToPosition(0);//移动到首部
        //            }
        //        });

    }

    //初始化列表控件上下拉的状态
    private void setListView() {

        ILoadingLayout startLabels = lesson_item_list.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = lesson_item_list.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("正在载入...");// 刷新时
        endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        //        headView = LayoutInflater.from(this).inflate(R.layout.headview, null);
        //        listview.getRefreshableView().addHeaderView(headView);//为ListView添加头布局
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.all_lesson_ll:

                typeForSort(CommonParameters.VALID);
                break;

            case R.id.wait_payment_ll:

                typeForSort(CommonParameters.FAILURE);
                break;

            case R.id.already_payment_ll:

                typeForSort(CommonParameters.OVERDUE);
                break;
        }
        changeTabItemStyle(v);
    }

    // 选择排序方式排序
    private void typeForSort(String type) {
        page= 1;

        Current_type = type;
        mSearchType = SEARCH_LESSON_PARAMETER;
        getLessonData(mUserInfo.getUid(),type); // 通过类型排序查找
    }


    private void changeTabItemStyle(View view) {

        all_lesson_v.setVisibility(view.getId() == R.id.all_lesson_ll ? View.VISIBLE:View.GONE);
        wait_payment_v.setVisibility(view.getId() == R.id.wait_payment_ll ? View.VISIBLE:View.GONE);
        already_payment_v.setVisibility(view.getId() == R.id.already_payment_ll ? View.VISIBLE:View.GONE);

        all_lesson_tv.setTextColor(view.getId() == R.id.all_lesson_ll ? getResources().getColor(R.color.price_bg) : getResources().getColor(R.color.black));
        wait_payment_tv.setTextColor(view.getId() == R.id.wait_payment_ll ? getResources().getColor(R.color.price_bg) : getResources().getColor(R.color.black));
        already_payment_tv.setTextColor(view.getId() == R.id.already_payment_ll ? getResources().getColor(R.color.price_bg) : getResources().getColor(R.color.black));

    }
    
    // 根据获取结果显示view
    private void setViewForResult(boolean isSuccess,String msg) {

        if (isSuccess) {
            findViewById(R.id.not_data).setVisibility(View.GONE);

        } else {
            findViewById(R.id.not_data).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.not_data_tv)).setText(msg);
        }
    }

    // 更新课程列表数据
    private void upDataLessonListView() {

        switch (mSearchType) {

            case SEARCH_LESSON_PARAMETER:

                mShowList.clear();
                mShowList.addAll(mLessonSearches);
                LogUtils.i("CouponActivity: SEARCH_LESSON_FOR_PARAMETER "  + mShowList.size());

                adapter.notifyDataSetChanged();
                lesson_item_list.getRefreshableView().smoothScrollToPosition(0);//移动到首部
                lesson_item_list.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lesson_item_list.onRefreshComplete();
                    }
                }, 1000);
                break;

            case SEARCH_LESSON_PULL_UP:

                adapter.addLast(mLessonSearches);
                LogUtils.i("CouponActivity: SEARCH_LESSON_PULL_UP " + mShowList.size());

                adapter.notifyDataSetChanged();
                lesson_item_list.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lesson_item_list.onRefreshComplete();
                        ToastUtil.show(mContext,"没有更多结果");
                    }
                }, 1000);
                break;
        }
    }

    //--------------------------------------请求服务器数据-------------------------------------------

    // 我的优惠券
    private void getLessonData(final int uid, final String status) {
        mLessonSearches= new ArrayList();
        String url = HttpURL.COUPON_COUPON_URL;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("CouponActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            mLessonSearches = new Gson().fromJson(data, new TypeToken<List<CouponBean>>(){}.getType());
                            LogUtils.i("CouponActivity: mLessonSearches.size " + mLessonSearches.size());

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            return;
                        }
                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILE);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("CouponActivity: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Couponcoupon" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("CouponActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token); 

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("status", status);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("CouponActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }
}
