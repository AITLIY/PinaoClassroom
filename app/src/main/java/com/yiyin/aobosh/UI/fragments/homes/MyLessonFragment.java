package com.yiyin.aobosh.UI.fragments.homes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.Interface.OrderClickInterface;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.UI.activitys.pay.CeatOrderActivity;

import com.yiyin.aobosh.UI.activitys.yiyinClassroom.LessonActivity;
import com.yiyin.aobosh.UI.activitys.yiyinClassroom.LessonActivity2;
import com.yiyin.aobosh.adapter.LessonOrderAdapter;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.LessonOrder;
import com.yiyin.aobosh.bean.RecommendLesson;
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

/**
 * Created by ALIY on 2019/1/5 0005.
 */


public class MyLessonFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;
    private UserInfo mUserInfo;

    private PullToRefreshListView lesson_item_list;          // 课程列表容器
    private ArrayList<LessonOrder> mLessonOrder;          // 课程搜索结果的集合
    private ArrayList<LessonOrder> mShowList;                // 课程显示结果的集合
    private LessonOrderAdapter adapter;

    private RelativeLayout all_lesson_ll,wait_payment_ll,already_payment_ll;
    private TextView all_lesson_tv,wait_payment_tv,already_payment_tv;
    private View all_lesson_v,wait_payment_v,already_payment_v;

    private static final int SEARCH_LESSON_PARAMETER  = 10;        //参数查询
    private static final int SEARCH_LESSON_PULL_UP = 20;           //上拉加载
    private int mSearchType = 10;  // 查询的标志
    private int page = 1;
    private int Current_type = CommonParameters.ALL;            // 当前类型

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;    
    private static final int LOAD_DATA_SUCCESS2 = 201;
    private static final int LOAD_DATA_FAILE2 = 202;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    if (mSearchType==SEARCH_LESSON_PARAMETER) {

                        if (mLessonOrder.size()>0){
                            setViewForResult(true,"");

                        } else {
                            setViewForResult(false,"没有任何课程信息~");
                        }
                    }
                    upDataLessonListView();
                    break;

                case LOAD_DATA1_FAILE:

                    lesson_item_list.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lesson_item_list.onRefreshComplete();
                            setViewForResult(false,"查询数据失败~");
                        }
                    }, 1000);
                    upDataLessonListView();
                    break;


                case LOAD_DATA_SUCCESS2:
                    
                    ToastUtil.show(mContext,"操作成功");
                    getLessonData(mUserInfo.getUid(),Current_type);
                    break;

                case LOAD_DATA_FAILE2:

                    ToastUtil.show(mContext,"操作失败");
                    break;

                case NET_ERROR:

                    lesson_item_list.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lesson_item_list.onRefreshComplete();
                            setViewForResult(false,"网络异常,请稍后重试~");
                        }
                    }, 1000);
                    upDataLessonListView();
                    break;
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_my_class, container, false);

        init();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();
    }

    private void init() {

        initView();
        initData();
    }

    private void initView() {

        all_lesson_ll = mView.findViewById(R.id.all_lesson_ll);
        wait_payment_ll = mView.findViewById(R.id.wait_payment_ll);
        already_payment_ll = mView.findViewById(R.id.already_payment_ll);

        all_lesson_tv = mView.findViewById(R.id.all_lesson_tv);
        wait_payment_tv = mView.findViewById(R.id.wait_payment_tv);
        already_payment_tv = mView.findViewById(R.id.already_payment_tv);

        all_lesson_v = mView.findViewById(R.id.all_lesson_v);
        wait_payment_v = mView.findViewById(R.id.wait_payment_v);
        already_payment_v = mView.findViewById(R.id.already_payment_v);

        all_lesson_ll.setOnClickListener(this);
        wait_payment_ll.setOnClickListener(this);
        already_payment_ll.setOnClickListener(this);

        initPullListView();
    }

    private void initData() {

        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mShowList = new ArrayList<>();
        adapter = new LessonOrderAdapter(mContext, mShowList,new OrderClick());
        lesson_item_list.setAdapter(adapter);
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();
        getLessonData(mUserInfo.getUid(), CommonParameters.ALL);
    }


    class OrderClick implements OrderClickInterface {

        @Override
        public void onOrder(LessonOrder order) {
            LogUtils.i("MyLessonFragment: onItemClick " + order.getId());

            Intent intent = new Intent(mContext, LessonActivity2.class);

            RecommendLesson.LessonBean lessonBean = new RecommendLesson.LessonBean();
            lessonBean.setId(order.getLessonid());
            lessonBean.setBookname(order.getBookname());
            lessonBean.setOrder(order.getId()+"");

            Bundle bundle = new Bundle();
            bundle.putSerializable("LessonBean", lessonBean);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        }

        @Override
        public void onCancel(LessonOrder order) {
            lessonCancelOrder(mUserInfo.getUid(), order.getId());
        }

        @Override
        public void onPay(LessonOrder order) {
            GlobalParameterApplication.isHasOrder = true;
            GlobalParameterApplication.attach = CommonParameters.LESSON_ORDER;
            Intent intent = new Intent(mContext,CeatOrderActivity.class);
            intent.putExtra("lessonid",order.getLessonid());
            startActivity(intent);
        }

        @Override
        public void onEvaluate(LessonOrder order) {
            GlobalParameterApplication.isShowComment = true;
            Intent intent = new Intent(mContext, LessonActivity2.class);

            RecommendLesson.LessonBean lessonBean = new RecommendLesson.LessonBean();
            lessonBean.setId(order.getLessonid());
            lessonBean.setBookname(order.getBookname());
            lessonBean.setOrder(order.getId()+"");

            Bundle bundle = new Bundle();
            bundle.putSerializable("LessonBean", lessonBean);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
        }
    }

    // 初始化列表
    private void initPullListView() {

        lesson_item_list = mView.findViewById(R.id.lesson_item_list);
        setListView();

        lesson_item_list.setMode(PullToRefreshBase.Mode.BOTH);
        lesson_item_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {  //拉动时
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page= 1;

                mSearchType = SEARCH_LESSON_PARAMETER;
                getLessonData(mUserInfo.getUid(),Current_type); // 下拉刷新搜索
                setViewForResult(true,"");
                LogUtils.i("MyLessonFragment: onPullDownToRefresh 下拉" + page + "页");
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
                LogUtils.i("MyLessonFragment: onPullUpToRefresh 上拉" + page + "页");
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

                typeForSort(CommonParameters.ALL);
                break;

            case R.id.wait_payment_ll:

                typeForSort(CommonParameters.NOT_PAID);
                break;

            case R.id.already_payment_ll:

                typeForSort(CommonParameters.PAID);
                break;
        }
        changeTabItemStyle(v);
    }

    // 选择订单类型
    private void typeForSort(int type) {
        page= 1;
        Current_type = type;
        mSearchType = SEARCH_LESSON_PARAMETER;
        getLessonData(mUserInfo.getUid(),type);
    }

    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        all_lesson_v.setVisibility(view.getId() ==  R.id.all_lesson_ll ? View.VISIBLE:View.GONE);
        wait_payment_v.setVisibility(view.getId() == R.id.wait_payment_ll ? View.VISIBLE:View.GONE);
        already_payment_v.setVisibility(view.getId() == R.id.already_payment_ll ? View.VISIBLE:View.GONE);

        all_lesson_tv.setTextColor(view.getId() == R.id.all_lesson_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.black));
        wait_payment_tv.setTextColor(view.getId() == R.id.wait_payment_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.black));
        already_payment_tv.setTextColor(view.getId() == R.id.already_payment_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.black));
    }

    // 根据获取结果显示view
    private void setViewForResult(boolean isSuccess,String msg) {

        if (isSuccess) {
            mView.findViewById(R.id.not_data).setVisibility(View.GONE);

        } else {
            mView.findViewById(R.id.not_data).setVisibility(View.VISIBLE);
            ((TextView) mView.findViewById(R.id.not_data_tv)).setText(msg);
        }
    }

    // 更新课程列表数据
    private void upDataLessonListView() {

        switch (mSearchType) {

            case SEARCH_LESSON_PARAMETER:

                mShowList.clear();
                mShowList.addAll(mLessonOrder);
                LogUtils.i("MyLessonFragment: SEARCH_LESSON_FOR_PARAMETER "  + mShowList.size());

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

                adapter.addLast(mLessonOrder);
                LogUtils.i("MyLessonFragment: SEARCH_LESSON_PULL_UP " + mShowList.size());

                adapter.notifyDataSetChanged();
                lesson_item_list.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lesson_item_list.onRefreshComplete();
                        if (mLessonOrder.size()==0){
                            ToastUtil.show(mContext,"没有更多结果");
                        }
                    }
                }, 1000);
                break;
        }
    }


    //--------------------------------------请求服务器数据-------------------------------------------

    // 获取我的的课程
    private void getLessonData(final int uid, final int status) {

        String url = HttpURL.MYLESSON_MYLESSON_URL;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("MyLessonFragment: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            mLessonOrder = new Gson().fromJson(data, new TypeToken<List<LessonOrder>>(){}.getType());
                            LogUtils.i("MyLessonFragment: mLessonOrder.size " + mLessonOrder.size());

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
                LogUtils.e("MyLessonFragment: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Mylessonmylesson" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("MyLessonFragment: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("status", status);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("MyLessonFragment json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 取消我的的课程
    private void lessonCancelOrder(final int uid, final int orderid) {

        String url = HttpURL.LESSONSON_CANCLEORDER_URL;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("lessonCancelOrder: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            mLessonOrder = new Gson().fromJson(data, new TypeToken<List<LessonOrder>>(){}.getType());
                            LogUtils.i("lessonCancelOrder: mLessonOrder.size " + mLessonOrder.size());

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
                LogUtils.e("lessonCancelOrder: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(LOAD_DATA_FAILE2);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Lessonsoncancleorder" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("lessonCancelOrder: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("orderid", orderid);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("lessonCancelOrder json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }
    
}
