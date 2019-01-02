package com.yiyin.aobosh.fragments.Videos;


import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.R;

import com.yiyin.aobosh.activitys.yiYinClassroom.LessonActivity;
import com.yiyin.aobosh.adapter.VideoBeanAdapter;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.RecommendLesson;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.bean.VideoBean;
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
 * A simple {@link Fragment} subclass.
 */
public class SonlistFragment extends Fragment implements AdapterView.OnClickListener,AdapterView.OnItemClickListener {
    
    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;
    private UserInfo mUserInfo;
    private RecommendLesson.LessonBean mLessonBean;

    private VideoBean mVideoBean ;                              // 课程视频对象
    private List<VideoBean.ListBean> mListBeans;                // 章节搜索结果的集合
    private List<VideoBean.ListBean> mShowList;                 // 章节显示结果的集合
    private PullToRefreshListView lesson_item_list;             // 章节列表容器
    private VideoBeanAdapter adapter;
    private TextView lesson_title,example_tv,teach_tv,all_tv;

    private static final int SEARCH_LESSON_PARAMETER  = 10;        //参数查询
    private static final int SEARCH_LESSON_PULL_UP = 20;           //上拉加载
    private int mSearchType = 10;  // 查询的标志
    private int page = 1;
    private int lessonID;
    private String Suffix_type  = CommonParameters.ALL2;            // 当前类型

    private static final int LOAD_DATA_SUCCESS = 101;
    private static final int LOAD_DATA_FAILE = 103;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    if (mSearchType==SEARCH_LESSON_PARAMETER) {

                        if (mListBeans.size()>0){
                            setViewForResult(true,"");

                        } else {
                            setViewForResult(false,"没有获取到课程信息~");
                        }
                    }
                    break;

                case LOAD_DATA_FAILE:

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_sonlist, container, false);

        init();
        return mView;
    }

    private void init() {

        initView();
        initData();
    }

    private void initView() {

        lesson_title = mView.findViewById(R.id.lesson_title);
        example_tv = mView.findViewById(R.id.example_tv);
        teach_tv = mView.findViewById(R.id.teach_tv);
        all_tv = mView.findViewById(R.id.all_tv);

        example_tv.setOnClickListener(this);
        teach_tv.setOnClickListener(this);
        all_tv.setOnClickListener(this);

        initPullListView();
    }

    private void initData() {

        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();
        mLessonBean = ((LessonActivity) getActivity()).getLessonBean();
        lessonID = mLessonBean.getId();
        lesson_title.setText(mLessonBean.getBookname());

        mShowList = new ArrayList<>();
        adapter = new VideoBeanAdapter(mContext, mShowList);
        lesson_item_list.setAdapter(adapter);
        lesson_item_list.setOnItemClickListener(this);
        getLessonsonFindson(mUserInfo.getUid(), lessonID, Suffix_type, page);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        adapter.setID(mShowList.get(position-1).getId());
        adapter.notifyDataSetChanged();
        LogUtils.i("SonlistFragment: onItemClick " + mShowList.get(position-1).getVideourl());
        ((LessonActivity)getActivity()).setAudio(mListBeans.get(position-1));
        ((LessonActivity)getActivity()).playAudio();
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
                getLessonsonFindson(mUserInfo.getUid(), lessonID, Suffix_type, page); // 下拉刷新搜索
                setViewForResult(true,"");
                LogUtils.i("SonlistFragment: onPullDownToRefresh 下拉" + page + "页");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;

                mSearchType = SEARCH_LESSON_PULL_UP;
                getLessonsonFindson(mUserInfo.getUid(), lessonID, Suffix_type, page);  // 上拉加载搜索

                LogUtils.i("SonlistFragment: onPullUpToRefresh 下拉" + page + "页");
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

            case R.id.example_tv:

                typeForSort(CommonParameters.EXAMPLE);
                break;

            case R.id.teach_tv:

                typeForSort(CommonParameters.TEACH);
                break;

            case R.id.all_tv:

                typeForSort(CommonParameters.ALL);
                break;
        }

    }

    // 选择课程类型
    private void typeForSort(String type) {
        page= 1;
        Suffix_type = type;
        mSearchType = SEARCH_LESSON_PARAMETER;
        getLessonsonFindson(mUserInfo.getUid(), lessonID, Suffix_type, page); //根据类型
    }

    // 根据获取结果显示view
    private void setViewForResult(boolean isSuccess,String msg) {

        if (isSuccess) {
            mView.findViewById(R.id.not_data).setVisibility(View.GONE);
            mView.findViewById(R.id.not_data_tv);

        } else {
            mView.findViewById(R.id.not_data).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.not_data_tv);
            ((TextView) mView.findViewById(R.id.not_data_tv)).setText(msg);
        }
    }

    // 更新课程列表数据
    private void upDataLessonListView() {

        switch (mSearchType) {

            case SEARCH_LESSON_PARAMETER:

                mShowList.clear();
                mShowList.addAll(mListBeans);

                if (mShowList.size()>0) {
                    ((LessonActivity) getActivity()).setAudio(mShowList.get(0));
                    ((LessonActivity) getActivity()).setPlayBg(mVideoBean.getPoster());
                    adapter.setID(mShowList.get(0).getId());
                }

                LogUtils.i("SonlistFragment: SEARCH_LESSON_FOR_PARAMETER "  + mShowList.size());

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

                mShowList.addAll(mListBeans);
                LogUtils.i("SonlistFragment: SEARCH_LESSON_PULL_UP " + mShowList.size());

                adapter.notifyDataSetChanged();
                lesson_item_list.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lesson_item_list.onRefreshComplete();
                        if (mListBeans.size()==0){
                            ToastUtil.show(mContext,"没有更多结果");
                        }
                    }
                }, 1000);
                break;
        }

    }


    //--------------------------------------请求服务器数据-------------------------------------------
    
    // 获取视频数据
    private void getLessonsonFindson(final int uid, final int lessonid, final String suffix, final int pindex) {
        mListBeans = new ArrayList<>();
        String url = HttpURL.LESSONSON_SONLIST_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("SonlistFragment: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            LogUtils.i("SonlistFragment: data " + data);
                            if (!"[]".equals(data)){
                                mVideoBean = new Gson().fromJson(data, VideoBean.class);
                                mListBeans = mVideoBean.getList();
                            }

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
                LogUtils.e("SonlistFragment: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Lessonsonsonlist" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("SonlistFragment: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("lessonid", lessonid);
                    obj.put("suffix", suffix);
                    obj.put("pindex", pindex);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("SonlistFragment json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
