package com.yiyin.aobosh.UI.fragments.homes;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.yiyin.aobosh.Interface.CateIdSearchInterface;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.UI.activitys.login.LoginActivity;
import com.yiyin.aobosh.UI.activitys.yiyinClassroom.LessonActivity;
import com.yiyin.aobosh.adapter.LessonCategoryAdapter2;
import com.yiyin.aobosh.adapter.LessonListAdapter;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.LessonCategory;
import com.yiyin.aobosh.bean.RecommendLesson;
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
public class AllClassFragment extends Fragment implements View.OnClickListener, CateIdSearchInterface {

    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;

    private EditText search_lesson_et;                                              // 文字查找TextView
    private TextView lesson_sort_tv,lesson_filtrate_tv;                             // 排序 筛选TextView
    private LinearLayout lesson_sort_ll,lesson_filtrate_ll;                         // 排序和筛选点击
    private ImageView lesson_sort_iv,lesson_filtrate_iv;                            // 排序和筛选箭头
    private LinearLayout sort_bg;                                                   // 排序列表背景
    private LinearLayout synthesize_sort,free_sort,price_sort,hot_sort,score_sort;  // 综合 免费 价格 人气 好评排序
    private LinearLayout filtrate_bg;                                               // 筛选列表背景
    private RecyclerView lesson_category_rv;                                        // 筛选列表容器
    private PullToRefreshListView lesson_item_list;                                 // 课程列表容器


    private ArrayList<RecommendLesson.LessonBean> mLessonSearches;          //课程搜索结果的集合
    private ArrayList<RecommendLesson.LessonBean> mShowList;                //课程显示结果的集合
    private LessonListAdapter adapter;
    private LessonCategoryAdapter2 adapter2;
    private int page = 1;

    private static final int SEARCH_LESSON_PARAMETER  = 10;        //参数查询
    private static final int SEARCH_LESSON_PULL_UP = 20;           //上拉加载
    private int mSearchType = 10;  // 查询的标志
    
    private String mSort = "";
    private String mCateId = ""; 
    private String mKeyword = "";

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
                            setViewForResult(false,"没有您要找的课程信息~");
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
        search_lesson_et = mView.findViewById(R.id.search_lesson_et);
        lesson_sort_tv = mView.findViewById(R.id.lesson_sort_tv);
        lesson_filtrate_tv = mView.findViewById(R.id.lesson_filtrate_tv);

        lesson_sort_ll = mView.findViewById(R.id.lesson_sort_ll);
        lesson_filtrate_ll = mView.findViewById(R.id.lesson_filtrate_ll);
        lesson_sort_iv = mView.findViewById(R.id.lesson_sort_iv);
        lesson_filtrate_iv = mView.findViewById(R.id.lesson_filtrate_iv);
        sort_bg = mView.findViewById(R.id.sort_bg);

        synthesize_sort = mView.findViewById(R.id.synthesize_sort);
        free_sort = mView.findViewById(R.id.free_sort);
        price_sort = mView.findViewById(R.id.price_sort);
        hot_sort = mView.findViewById(R.id.hot_sort);
        score_sort = mView.findViewById(R.id.score_sort);

        filtrate_bg = mView.findViewById(R.id.filtrate_bg);
        lesson_category_rv = mView.findViewById(R.id.lesson_category_rv);
        lesson_item_list = mView.findViewById(R.id.lesson_item_list);

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
                    mSearchType = SEARCH_LESSON_PARAMETER;
                    getLessonData(mSort,mCateId,mKeyword, page); // 通过关键字搜索

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
        sort_bg.setOnClickListener(this);
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
        getLessonData(mSort,mCateId,mKeyword, page); // 初始化搜索
    }


    // 是否显示排序列表
    private void isShowlessonSort(boolean isShow) {

        if (isShow){
            lesson_sort_iv.setImageResource(R.drawable.icon_tab_sort_top);
            sort_bg.setVisibility(View.VISIBLE);
            isSort = true;
        }else {
            lesson_sort_iv.setImageResource(R.drawable.icon_tab_sort_bottom);
            sort_bg.setVisibility(View.GONE);
            isSort = false;
        }
    }

    // 是否显示筛选列表
    private void isShowlessonfiltrate(boolean isShow) {
        if (isShow){
            lesson_filtrate_iv.setImageResource(R.drawable.icon_tab_sort_top);
            filtrate_bg.setVisibility(View.VISIBLE);
            isfiltrate = true;
        }else {
            lesson_filtrate_iv.setImageResource(R.drawable.icon_tab_sort_bottom);
            filtrate_bg.setVisibility(View.GONE);
            isfiltrate = false;
        }

    }

    private boolean isSort;
    private boolean isfiltrate;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.lesson_sort_ll:

                if (isSort) {

                    isShowlessonSort(false);

                } else {
                    isShowlessonSort(true);
                    isShowlessonfiltrate(false);
                }

                break;

            case R.id.lesson_filtrate_ll:

                if (isfiltrate) {

                    isShowlessonfiltrate(false);

                } else {
                    isShowlessonfiltrate(true);
                    isShowlessonSort(false);

                    if (GlobalParameterApplication.lessonCategory!=null)
                        initLessonCategoryList(GlobalParameterApplication.lessonCategory);
                }

                break;

            case R.id.sort_bg:

                isShowlessonSort(false);
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

    // 选择排序方式排序
    private void typeForSort(String type) {
        mSort = type;
        page= 1;
        mSearchType = SEARCH_LESSON_PARAMETER;
        getLessonData(mSort,mCateId,mKeyword, page); // 通过类型排序搜索
    }

    // 初始化列表
    private void initPullListView() {

        setListView();

        lesson_item_list.setMode(PullToRefreshBase.Mode.BOTH);

        lesson_item_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {  //拉动时
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page= 1;

                mSearchType = SEARCH_LESSON_PARAMETER;
                getLessonData(mSort,mCateId,mKeyword, page); // 下拉刷新搜索
                setViewForResult(true,"");
                LogUtils.i("AllClassFragment: onPullDownToRefresh 下拉" + page + "页");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;

                mSearchType = SEARCH_LESSON_PULL_UP;
                getLessonData(mSort,mCateId,mKeyword, page); // 上拉加载搜索

                LogUtils.i("AllClassFragment: onPullUpToRefresh 下拉" + page + "页");
            }
        });

        lesson_item_list.setOnItemClickListener(new AdapterView.OnItemClickListener() { //点击item时
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                    startActivity(new Intent(mContext, LoginActivity.class));
                    return;
                } else {
                    RecommendLesson.LessonBean lessonBean = mShowList.get(position-1);
                    LogUtils.i("AllClassFragment: ItemClick position " + position);
                    Intent intent = new Intent(mContext,LessonActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("LessonBean",lessonBean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
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

    // 更新分类
    private void initLessonCategoryList(List<LessonCategory> LessonCategorys) {
        LogUtils.i("AllClassFragment: initLessonCategoryList size"  + LessonCategorys.size());
        adapter2 = new LessonCategoryAdapter2(mContext, LessonCategorys,this);

        final StaggeredGridLayoutManager staggeredGridLayoutManager = new NewStaggeredGridLayoutManager(4, OrientationHelper.VERTICAL);
        lesson_category_rv.setLayoutManager(staggeredGridLayoutManager);
        lesson_category_rv.setAdapter(adapter2);
    }

    @Override
    public void onSearch(LessonCategory.SonlistBean sonlistBean) {

        isShowlessonfiltrate(false);
        lesson_filtrate_tv.setText(sonlistBean.getName());

        mCateId = sonlistBean.getId()+"";
        mKeyword = "";
        mSort = "";
        page= 1;

        mSearchType = SEARCH_LESSON_PARAMETER;
        getLessonData(mSort,mCateId,mKeyword, page); // 通过id搜索
    }

    static class  NewStaggeredGridLayoutManager extends StaggeredGridLayoutManager {

        public NewStaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public NewStaggeredGridLayoutManager(int spanCount, int orientation) {
            super(spanCount, orientation);
        }
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

                mShowList.addAll(mLessonSearches);
//                adapter.addLast(mLessonSearches);
                LogUtils.i("AllClassFragment: SEARCH_LESSON_PULL_UP " + mShowList.size());

                adapter.notifyDataSetChanged();
                lesson_item_list.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lesson_item_list.onRefreshComplete();
                        if (mLessonSearches.size()==0){
                            ToastUtil.show(mContext,"没有更多结果");
                        }
                    }
                }, 1000);
                break;
        }
    }

    //--------------------------------------请求服务器数据-------------------------------------------

    // 获取课程数据列表
    private void getLessonData(final String sort, final String cateId, final String keyword, final int pindex) {
        mLessonSearches= new ArrayList();
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
                            mLessonSearches = new Gson().fromJson(data, new TypeToken<List<RecommendLesson.LessonBean>>(){}.getType());

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
                mHandler.sendEmptyMessage(NET_ERROR);
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
