package com.yiyin.aobosh.UI.activitys.yiyinClassroom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.UI.activitys.login.LoginActivity;
import com.yiyin.aobosh.adapter.LessonListAdapter;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.RecommendLesson;
import com.yiyin.aobosh.bean.TeacherDetailBean;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.commons.CommonParameters;
import com.yiyin.aobosh.commons.HttpURL;
import com.yiyin.aobosh.utils.SHA;
import com.yiyin.aobosh.utils.TimeUtils;
import com.yiyin.aobosh.utils.ToastUtil;
import com.yiyin.aobosh.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherActivity extends Activity implements View.OnClickListener {
    
    private Context mContext;
    private RequestQueue requestQueue;
    private UserInfo mUserInfo;

    private WebView teacher_detail_content;
    private PullToRefreshListView lesson_item_list;                         // 课程列表容器
    private ArrayList<RecommendLesson.LessonBean> mLessonSearches;          //课程搜索结果的集合
    private ArrayList<RecommendLesson.LessonBean> mShowList;                //课程显示结果的集合
    private LessonListAdapter adapter;

    private ArrayList<TeacherDetailBean> mTeacherDetailBeans;          //课程搜索结果的集合

    private TextView teacheer_title,teacher_name,lesson_total,student_num;
    private LinearLayout star_ll;
    private ImageView star_img;
    private CircleImageView teacher_icon;
    private RelativeLayout all_lesson_ll,teacher_detail_ll;
    private TextView all_lesson_tv,teacher_detail_tv;
    private View all_lesson_v,teacher_detail_v;


    private static final int SEARCH_LESSON_PARAMETER  = 10;        //参数查询
    private static final int SEARCH_LESSON_PULL_UP = 20;           //上拉加载
    private int mSearchType = 10;  // 查询的标志
    private int page = 1;
    private int teacherID = 2;

    private static final int LOAD_DATA_SUCCESS = 101;
    private static final int LOAD_DATA_FAILE = 102;
    private static final int LOAD_DATA_SUCCESS1 = 201;
    private static final int LOAD_DATA_FAILE1 = 202;
    private static final int LOAD_DATA_SUCCESS2 = 301;
    private static final int LOAD_DATA_FAILE2 = 302;
    private static final int NET_ERROR = 404;


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    if (mSearchType==SEARCH_LESSON_PARAMETER) {

                        if (mLessonSearches.size()>0){
                            setViewForResult(true,"");

                        } else {
                            setViewForResult(false,"没有任何课程信息~");
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

                case LOAD_DATA_SUCCESS1:

                    String info = (String) msg.obj;
                    ToastUtil.show(mContext, info);
                    star_img.setImageResource("收藏成功".equals(info) ? R.drawable.icon_tab_star1 : R.drawable.icon_tab_star0);
                    break;

                case LOAD_DATA_FAILE1:

                    ToastUtil.show(mContext, "操作失败");
                    break;

                case LOAD_DATA_SUCCESS2:

                    setViewContent();
                    break;

                case LOAD_DATA_FAILE2:

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
        setContentView(R.layout.activity_teacher);
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

        teacheer_title = findViewById(R.id.teacheer_title);
        teacher_icon = findViewById(R.id.teacher_icon);
        teacher_name = findViewById(R.id.teacher_name);
        lesson_total = findViewById(R.id.lesson_total);
        student_num = findViewById(R.id.student_num);
        star_ll = findViewById(R.id.star_ll);
        star_img = findViewById(R.id.star_img);
        teacher_detail_content = findViewById(R.id.teacher_detail_content);

        all_lesson_ll = findViewById(R.id.all_lesson_ll);
        all_lesson_tv = findViewById(R.id.all_lesson_tv);
        all_lesson_v = findViewById(R.id.all_lesson_v);
        teacher_detail_ll = findViewById(R.id.teacher_detail_ll);
        teacher_detail_tv = findViewById(R.id.teacher_detail_tv);
        teacher_detail_v = findViewById(R.id.teacher_detail_v);


        star_ll.setOnClickListener(this);
        all_lesson_ll.setOnClickListener(this);
        teacher_detail_ll.setOnClickListener(this);

        initPullListView();
    }

    private void setViewContent() {

        Glide.with(mContext)
                .load(mTeacherDetailBeans.get(0).getTeacherphoto())
                .error(R.drawable.icon_tab_usericon)
                .into(teacher_icon);

        teacheer_title.setText(mTeacherDetailBeans.get(0).getTeacher());
        teacher_name.setText(mTeacherDetailBeans.get(0).getTeacher());
        lesson_total.setText(mTeacherDetailBeans.get(0).getTotal());
        student_num.setText(mTeacherDetailBeans.get(0).getStudent_num()+"");
        star_img.setImageResource(mTeacherDetailBeans.get(0).getIscollect() == 1 ? R.drawable.icon_tab_star1 : R.drawable.icon_tab_star0);

        teacher_detail_content.loadDataWithBaseURL(null,mTeacherDetailBeans.get(0).getTeacherdes(),"text/html","uft-8",null);
    }

    private void initData() {

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mShowList = new ArrayList<>();
        adapter = new LessonListAdapter(mContext, mShowList);
        lesson_item_list.setAdapter(adapter);
        lesson_item_list.setOnItemClickListener(new ItemClick());
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();

        Intent intent = getIntent();
        if (intent != null) {
            teacherID = intent.getIntExtra("teacherid",0);

            LogUtils.i("TeacherActivity: teacherID " + teacherID);
        }

        getLessonData(teacherID,mUserInfo.getUid(),page);
        getTeacherDetail(teacherID,mUserInfo.getUid());
    }

    class ItemClick implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                startActivity(new Intent(mContext, LoginActivity.class));
                return;
            } else {
                RecommendLesson.LessonBean lessonBean = mShowList.get(position-1);
                Intent intent = new Intent(mContext,LessonActivity2.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("LessonBean",lessonBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }

        }
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
                getLessonData(teacherID,mUserInfo.getUid(),page); // 下拉刷新搜索
                setViewForResult(true,"");
                LogUtils.i("TeacherActivity: onPullDownToRefresh 下拉" + page + "页");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;

                mSearchType = SEARCH_LESSON_PULL_UP;
                getLessonData(teacherID,mUserInfo.getUid(),page); // 上拉加载搜索

                LogUtils.i("TeacherActivity: onPullUpToRefresh 上拉" + page + "页");
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

            case R.id.star_ll:

                saveCollect(mUserInfo.getUid(),teacherID,CommonParameters.TEACHER);
                break;
            case R.id.all_lesson_ll:

                changeTabItemStyle(v);
                break;

            case R.id.teacher_detail_ll:

                changeTabItemStyle(v);
                break;
        }

    }

    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        all_lesson_tv.setTextColor(view.getId() == R.id.all_lesson_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.black));
        teacher_detail_tv.setTextColor(view.getId() == R.id.teacher_detail_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.black));

        all_lesson_v.setVisibility(view.getId() ==  R.id.all_lesson_ll ? View.VISIBLE:View.GONE);
        teacher_detail_v.setVisibility(view.getId() == R.id.teacher_detail_ll ? View.VISIBLE:View.GONE);

        lesson_item_list.setVisibility(view.getId() ==  R.id.all_lesson_ll ? View.VISIBLE:View.GONE);
        teacher_detail_content.setVisibility(view.getId() == R.id.teacher_detail_ll ? View.VISIBLE:View.GONE);
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
                LogUtils.i("TeacherActivity: SEARCH_LESSON_FOR_PARAMETER "  + mShowList.size());

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
                LogUtils.i("TeacherActivity: SEARCH_LESSON_PULL_UP " + mShowList.size());

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

    // 获取讲师课程列表
    private void getLessonData(final int id,final int uid, final int pindex) {
        mLessonSearches = new ArrayList<>();
        String url = HttpURL.TEACHER_TEACHERLESSON_URL;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("TeacherActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            mLessonSearches = new Gson().fromJson(data, new TypeToken<List<RecommendLesson.LessonBean>>(){}.getType());
                            LogUtils.i("TeacherActivity: mLessonSearches.size " + mLessonSearches.size());

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS);
                            return;
                        }
                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("TeacherActivity: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Teacherteacherlesson" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("TeacherActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("id", id);
                    obj.put("uid", uid);
                    obj.put("pindex", pindex);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("TeacherActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }


    // 获取讲师详情
    private void getTeacherDetail(final int id,final int uid) {

        String url = HttpURL.TEACHER_TEACHERDETAIL_URL;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("TeacherActivity: result2 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");

                            if (!"[]".equals(data)) {
                                mTeacherDetailBeans = new Gson().fromJson(data, new TypeToken<List<TeacherDetailBean>>() {}.getType());
                            }
                            LogUtils.i("TeacherActivity: mTeacherDetailBeans.size " + mTeacherDetailBeans.size());

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
                LogUtils.e("TeacherActivity: volleyError2 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Teacherteacherdetail" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("TeacherActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("id", id);
                    obj.put("uid", uid);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("TeacherActivity json2 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }


    // 收藏
    private void saveCollect(final int uid, final int outid, final String ctype) {

        String url = HttpURL.LESSONSON_COLLECT_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("LessonActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String msg = jsonObject.getString("msg");

                            mHandler.obtainMessage(LOAD_DATA_SUCCESS1,msg).sendToTarget();

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
                LogUtils.e("LessonActivity: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Lessonsoncollect" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("LessonActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("outid", outid);
                    obj.put("ctype", ctype);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("LessonActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
