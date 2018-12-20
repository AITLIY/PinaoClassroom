package com.yiyin.aobosh.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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
import com.yiyin.aobosh.Adapter.LessonListAdapter;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.Utils.SHA;
import com.yiyin.aobosh.Utils.TimeUtils;
import com.yiyin.aobosh.Utils.ToastUtil;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.LessonSearch;
import com.yiyin.aobosh.common.CommonParameters;
import com.yiyin.aobosh.common.HttpURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllClassFragment extends Fragment implements View.OnClickListener {

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;

    private TextView search_lesson_et,lesson_sort_tv,lesson_filtrate_tv;            // 文字查找 排序 筛选TextView
    private LinearLayout lesson_sort_ll,lesson_filtrate_ll;                         // 排序和筛选点击
    private ImageView lesson_sort_iv,lesson_filtrate_iv;                            // 排序和筛选箭头
    private LinearLayout sort_bg,sort_container;                                    // 排序列表背景和容器
    private LinearLayout synthesize_sort,free_sort,price_sort,hot_sort,score_sort;  // 综合 免费 价格 人气 好评排序
    private RecyclerView lesson_category_rv;                                        // 筛选列表容器
    private PullToRefreshListView lesson_item_list;                                 // 课程列表容器

    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;

    private ArrayList<LessonSearch> mLessonSearches;          //课程搜索结果的集合
    private ArrayList<LessonSearch> mShowList;                //课程显示结果的集合
    private LessonListAdapter adapter;
    private int page = 1;

    private static final int SEARCH_LESSON_PARAMETER  = 10;        //参数查询
    private static final int SEARCH_LESSON_PULL_UP = 20;           //上拉加载
    private int mSearchType = 10;  // 查询的标志
    
    private String mSort = "";
    private String mCateId = ""; 
    private String mKeyword = "";
    private String mPindex = "1";

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    upDataLessonListView();
                    break;

                case LOAD_DATA1_FAILE:

                    lesson_item_list.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lesson_item_list.onRefreshComplete();
                        }
                    }, 1000);
                    ToastUtil.show(mContext,"查询数据失败");
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_all_class, container, false);
        mContext = getContext();

        init();
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
    }

    private void init() {
        initView();
        initListener();
        initData();
    }

    private void initView() {
        search_lesson_et = (TextView) mView.findViewById(R.id.search_lesson_et);
        lesson_sort_tv = (TextView) mView.findViewById(R.id.lesson_sort_tv);
        lesson_filtrate_tv = (TextView) mView.findViewById(R.id.lesson_filtrate_tv);

        lesson_sort_ll = (LinearLayout) mView.findViewById(R.id.lesson_sort_ll);
        lesson_filtrate_ll = (LinearLayout) mView.findViewById(R.id.lesson_filtrate_ll);
        lesson_sort_iv = (ImageView) mView.findViewById(R.id.lesson_sort_iv);
        lesson_filtrate_iv = (ImageView) mView.findViewById(R.id.lesson_filtrate_iv);
        sort_bg = (LinearLayout) mView.findViewById(R.id.sort_bg);
        sort_container = (LinearLayout) mView.findViewById(R.id.sort_container);

        synthesize_sort = (LinearLayout) mView.findViewById(R.id.synthesize_sort);
        free_sort = (LinearLayout) mView.findViewById(R.id.free_sort);
        price_sort = (LinearLayout) mView.findViewById(R.id.price_sort);
        hot_sort = (LinearLayout) mView.findViewById(R.id.hot_sort);
        score_sort = (LinearLayout) mView.findViewById(R.id.score_sort);

        lesson_category_rv = (RecyclerView) mView.findViewById(R.id.lesson_category_rv);
        lesson_item_list = (PullToRefreshListView) mView.findViewById(R.id.lesson_item_list);

        initPullListView();
    }

    private void initListener() {

        search_lesson_et.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        search_lesson_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH ||(event.getKeyCode()== KeyEvent.KEYCODE_ENTER&&event.getAction() == KeyEvent.ACTION_DOWN)) {
                    String search_keyword = search_lesson_et.getText().toString().trim();
                    LogUtils.i("AllClassFragment: search_key " + search_keyword);

                    if ("".equals(search_keyword)){
                        return false;
                    }

                    mCateId = "";
                    mKeyword = search_keyword;
                    page= 1;
                    mPindex = page + "";
                    mSearchType = SEARCH_LESSON_PARAMETER;
                    getLessonData(mSort,mCateId,mKeyword, mPindex);

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                    }

                    return true;
                }
                return false;
            }
        });

        lesson_sort_ll.setOnClickListener(this);
        lesson_filtrate_ll.setOnClickListener(this);
        lesson_sort_ll.setOnClickListener(this);
        synthesize_sort.setOnClickListener(this);
        free_sort.setOnClickListener(this);
        price_sort.setOnClickListener(this);
        hot_sort.setOnClickListener(this);
        score_sort.setOnClickListener(this);

    }


    private void initData() {
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mShowList = new ArrayList<>();
        adapter = new LessonListAdapter(mContext, mShowList);
        lesson_item_list.setAdapter(adapter);
        getLessonData(mSort,mCateId,mKeyword, mPindex);
    }

    private void isShowlessonSort(boolean isShow) {
        if (isShow){
            lesson_sort_iv.setImageResource(R.drawable.icon_tab_sort_top);
            sort_bg.setVisibility(View.VISIBLE);
        }else {
            lesson_sort_iv.setImageResource(R.drawable.icon_tab_sort_bottom);
            sort_bg.setVisibility(View.GONE);
        }

    }

    private void isShowlessonfiltrate(boolean isShow) {
        if (isShow){
            lesson_filtrate_iv.setImageResource(R.drawable.icon_tab_sort_top);
            lesson_category_rv.setVisibility(View.VISIBLE);
        }else {
            lesson_filtrate_iv.setImageResource(R.drawable.icon_tab_sort_bottom);
            lesson_category_rv.setVisibility(View.GONE);
        }

    }

    private boolean isSort;
    private boolean isfiltrate;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.lesson_sort_ll:

                if (isSort) {
                    isSort = false;
                    isShowlessonSort(false);
                } else {
                    isSort = true;
                    isShowlessonSort(true);
                    isShowlessonfiltrate(false);
                }

                break;

            case R.id.lesson_filtrate_ll:

                if (isfiltrate) {
                    isfiltrate = false;
                    isShowlessonfiltrate(false);
                } else {
                    isfiltrate = true;
                    isShowlessonfiltrate(true);
                    isShowlessonSort(false);

//                    GlobalParameterApplication.lessonCategory;
                }

                break;

            case R.id.synthesize_sort:
                isShowlessonSort(false);
                lesson_sort_tv.setText(getString(R.string.synthesize_sort));
                typeForSort("");

                break;

            case R.id.free_sort:
                isShowlessonSort(false);
                lesson_sort_tv.setText(getString(R.string.free_sort));
                typeForSort(CommonParameters.FREE);
                break;

            case R.id.price_sort:
                isShowlessonSort(false);
                lesson_sort_tv.setText(getString(R.string.price_sort));
                typeForSort(CommonParameters.PRICE);
                break;

            case R.id.hot_sort:
                isShowlessonSort(false);
                lesson_sort_tv.setText(getString(R.string.hot_sort));
                typeForSort(CommonParameters.HOT);
                break;

            case R.id.score_sort:
                isShowlessonSort(false);
                lesson_sort_tv.setText(getString(R.string.score_sort));
                typeForSort(CommonParameters.SCORE);
                break;
        }
    }

    // 根据类型排序
    private void typeForSort(String type) {
        mSort = type;
        page= 1;
        mPindex = page + "";
        mSearchType = SEARCH_LESSON_PARAMETER;
        getLessonData(mSort,mCateId,mKeyword, mPindex);
    }

    // 初始化列表
    private void initPullListView() {

        setListView();

        lesson_item_list.setMode(PullToRefreshBase.Mode.BOTH);

        lesson_item_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {  //拉动时
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page= 1;
                mPindex = page + "";
                mSearchType = SEARCH_LESSON_PARAMETER;
                getLessonData(mSort,mCateId,mKeyword, mPindex);
                LogUtils.i("AllClassFragment: onPullDownToRefresh 下拉" + page + "页");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                mPindex = page + "";
                mSearchType = SEARCH_LESSON_PULL_UP;
                getLessonData(mSort,mCateId,mKeyword, mPindex);
                LogUtils.i("AllClassFragment: onPullUpToRefresh 下拉" + page + "页");
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

    /**
     * 初始化列表控件上下拉的状态
     */
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

    private void upDataLessonListView() {

        switch (mSearchType) {

            case SEARCH_LESSON_PARAMETER:

                mShowList.clear();
                mShowList.addAll(mLessonSearches);
                LogUtils.i("AllClassFragment: SEARCH_LESSON_FOR_PARAMETER "  + mShowList.size());

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
                LogUtils.i("AllClassFragment: SEARCH_LESSON_PULL_UP " + mShowList.size());

                adapter.notifyDataSetChanged();
                lesson_item_list.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lesson_item_list.onRefreshComplete();
                    }
                }, 1000);
                break;
        }
    }


    //--------------------------------------请求服务器数据-------------------------------------------

    // 1.获取课程数据
    private void getLessonData(final String sort, final String cateId, final String keyword, final String pindex) {

        String url = HttpURL.LESSONSON_SEARCH_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("AllClassFragment: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            mLessonSearches = new Gson().fromJson(data, new TypeToken<List<LessonSearch>>(){}.getType());
                            LogUtils.i("AllClassFragment: mLessonSearches.size " + mLessonSearches.size());

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
                LogUtils.e("AllClassFragment: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(LOAD_DATA1_FAILE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Lessonsonsearch" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("AllClassFragment: token " + token);
                    String sha_token = SHA.encryptToSHA(token);
                    
                    obj.put("access_token", sha_token);
                    obj.put("sort", sort);
                    obj.put("cate_id", cateId);
                    obj.put("keyword", keyword);
                    obj.put("pindex", pindex);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("AllClassFragment json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
