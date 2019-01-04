package com.yiyin.aobosh.activitys.yiYinClassroom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
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
import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.adapter.EvaluateBeanAdapter;
import com.yiyin.aobosh.adapter.VideoBeanAdapter;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.EvaluateBean;
import com.yiyin.aobosh.bean.LessonDetail;
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
import org.yczbj.ycvideoplayerlib.constant.ConstantKeys;
import org.yczbj.ycvideoplayerlib.controller.VideoPlayerController;
import org.yczbj.ycvideoplayerlib.inter.listener.OnVideoBackListener;
import org.yczbj.ycvideoplayerlib.manager.VideoPlayerManager;
import org.yczbj.ycvideoplayerlib.player.VideoPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener {

    private Context mContext;
    private RecommendLesson.LessonBean mLessonBean;
    private RequestQueue requestQueue;
    private UserInfo mUserInfo;

    private VideoPlayer videoPlayer;
    private VideoPlayerController controller;

    private ImageView play_bg, play_start;              //播放
    private LinearLayout lesson_detail_ll,lesson_list_ll,lesson_evaluate_ll;
    private LinearLayout desc_ll,sonlist_ll,evaluate_ll,btn_menu_ll,write_comment_ll,submit_comment;
    private TextView sonlist_tv, desc_tv, evaluate_tv,start_study;
    private View sonlist_v, desc_v, evaluate_v;
    private ImageView star_img;

    private EditText comment_et;                        //评论文字
    private LinearLayout advisory_ll,collect_ll;        //开始学习 /评论

    private LessonDetail mLessonDetail;                 //课程详情对象
    private TextView bookname_tv,difficulty_tv;
    private LinearLayout teacher_detail;
    private WebView lesson_descript,teacher_detail_content;


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
    private String Suffix_type  = CommonParameters.ALL2;            // 当前类型

    private List<EvaluateBean> mListBeans2;                // 课程评价搜索结果的集合
    private List<EvaluateBean> mShowList2;                 // 课程评价显示结果的集合
    private PullToRefreshListView lesson_item_list2;             // 列表容器
    private EvaluateBeanAdapter adapter2;

    private static final int SEARCH_LESSON_PARAMETER2  = 10;        //参数查询
    private static final int SEARCH_LESSON_PULL_UP2 = 20;           //上拉加载
    private int mSearchType2 = 10;  // 查询的标志
    private int page2 = 1;

    private int lessonID;
    private int mCurrentItemId = -1;

    private static final int LOAD_DATA_SUCCESS01 = 1001;
    private static final int LOAD_DATA_SUCCESS02 = 2001;
    private static final int LOAD_DATA_SUCCESS03 = 3001;
    private static final int LOAD_DATA_FAILE = 1002;

    private static final int LOAD_DATA_SUCCESS1 = 101;
    private static final int LOAD_DATA_FAILE1 = 102;
    private static final int LOAD_DATA_SUCCESS2 = 201;
    private static final int LOAD_DATA_FAILE2 = 202;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS01:

                    setViewContent();
                    break;

                case LOAD_DATA_SUCCESS02:

                    if (mSearchType==SEARCH_LESSON_PARAMETER) {

                        if (mListBeans.size()>0){
                            setViewForResult(true,"");

                        } else {
                            setViewForResult(false,"没有获取到课程信息~");
                        }
                    }
                    upDataLessonListView();
                    LogUtils.i("EvaluateFragment: sonlist_ll " );
                    break;

                case LOAD_DATA_SUCCESS03:

                    if (mSearchType2==SEARCH_LESSON_PARAMETER2) {

                        if (mListBeans2.size()>0){
                            setViewForResult2(true,"");

                        } else {
                            setViewForResult2(false,"还没有人评论信息~");
                        }
                    }
                    upDataLessonListView2();
                    break;

                case LOAD_DATA_FAILE:

                    lesson_item_list2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lesson_item_list2.onRefreshComplete();
                            setViewForResult2(false,"查询数据失败~");
                        }
                    }, 1000);
                    break;

                case LOAD_DATA_SUCCESS1:

                    String info = (String) msg.obj;
                    ToastUtil.show(mContext, info);
                    star_img.setImageResource("收藏成功".equals(info)  ? R.drawable.icon_tab_collect1: R.drawable.icon_tab_collect0);
                    break;

                case LOAD_DATA_FAILE1:

                    ToastUtil.show(mContext, "操作失败");
                    break;

                case LOAD_DATA_SUCCESS2:

                    ToastUtil.show(mContext, "评价成功");
                    break;

                case LOAD_DATA_FAILE2:

                    ToastUtil.show(mContext, "评价失败");
                    break;

                case NET_ERROR:

                    ToastUtil.show(mContext, "网络异常,请稍后重试");
                    break;
            }

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        VideoPlayerManager.instance().releaseVideoPlayer();
    }

    @Override
    public void onBackPressed() {
        if (VideoPlayerManager.instance().onBackPressed())
            return;
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (controller != null && controller.getLock()) {
                //如果锁屏，那就屏蔽返回键
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (controller != null) {
            controller.onPlayStateChanged(7);
        }
        GlobalParameterApplication.isShowComment = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        // 隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
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

        videoPlayer = findViewById(R.id.video_player);
        play_bg = findViewById(R.id.play_bg);
        play_start = findViewById(R.id.play_start);

        //详情
        lesson_detail_ll = findViewById(R.id.lesson_detail_ll);
        sonlist_ll = findViewById(R.id.sonlist_ll);
        sonlist_tv = findViewById(R.id.sonlist_tv);
        sonlist_v = findViewById(R.id.sonlist_v);
        //目录
        lesson_list_ll = findViewById(R.id.lesson_list_ll);
        desc_ll = findViewById(R.id.desc_ll);
        desc_tv = findViewById(R.id.desc_tv);
        desc_v = findViewById(R.id.desc_v);
        //评价
        lesson_evaluate_ll = findViewById(R.id.lesson_evaluate_ll);
        evaluate_ll = findViewById(R.id.evaluate_ll);
        evaluate_tv = findViewById(R.id.evaluate_tv);
        evaluate_v = findViewById(R.id.evaluate_v);

        //开始学习
        btn_menu_ll = findViewById(R.id.btn_menu_ll);
        advisory_ll = findViewById(R.id.advisory_ll);
        collect_ll = findViewById(R.id.collect_ll);
        start_study = findViewById(R.id.start_study);

        star_img = findViewById(R.id.star_img);

         //评论
        write_comment_ll = findViewById(R.id.write_comment_ll);
        comment_et = findViewById(R.id.comment_et);
        submit_comment = findViewById(R.id.submit_comment);

        //标题
        TabOnClickListener listtener = new TabOnClickListener();
        sonlist_ll.setOnClickListener(listtener);
        desc_ll.setOnClickListener(listtener);
        evaluate_ll.setOnClickListener(listtener);


//        if (videoPlayer.isIdle()) {
//            ToastUtil.show(mContext, "要点击播放后才能进入小窗口");
//        } else {
//            videoPlayer.enterTinyWindow();
//        }
//
//        videoPlayer.enterVerticalScreenScreen();
//        videoPlayer.enterFullScreen();
//        videoPlayer.restart();


        //课程详情
        bookname_tv = findViewById(R.id.bookname_tv);
        difficulty_tv = findViewById(R.id.difficulty_tv);
        teacher_detail = findViewById(R.id.teacher_detail);
        lesson_descript = findViewById(R.id.lesson_descript);
        teacher_detail_content = findViewById(R.id.teacher_detail_content);

        //课程列表
        lesson_title = findViewById(R.id.lesson_title);
        example_tv = findViewById(R.id.example_tv);
        teach_tv = findViewById(R.id.teach_tv);
        all_tv = findViewById(R.id.all_tv);
        initPullListView();
        //课程评价
        initPullListView2();

        play_start.setOnClickListener(this);
        advisory_ll.setOnClickListener(this);
        collect_ll.setOnClickListener(this);
        start_study.setOnClickListener(this);
        submit_comment.setOnClickListener(this);

        teacher_detail.setOnClickListener(this);

        example_tv.setOnClickListener(this);
        teach_tv.setOnClickListener(this);
        all_tv.setOnClickListener(this);

    }

    private void initData() {

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            mLessonBean = (RecommendLesson.LessonBean) bundle.getSerializable("LessonBean");
            LogUtils.i("LessonActivity: lessonBean id " + mLessonBean.getId());
        }
        lessonID = mLessonBean.getId();

        initAudio();

        if (GlobalParameterApplication.isShowComment) {
            changeTabItemStyle(evaluate_ll);
            mCurrentItemId = evaluate_ll.getId();
            showCommentView(true);
        } else {
            showCommentView(false);
            mCurrentItemId = sonlist_ll.getId();
        }

        getlessonsonDesc(mUserInfo.getUid(), mLessonBean.getId());

        lesson_title.setText(mLessonBean.getBookname());

        mShowList = new ArrayList<>();
        adapter = new VideoBeanAdapter(mContext, mShowList);
        lesson_item_list.setAdapter(adapter);
        lesson_item_list.setOnItemClickListener(this);
        getLessonsonFindson(mUserInfo.getUid(), lessonID, Suffix_type, page);

        mShowList2 = new ArrayList<>();
        adapter2 = new EvaluateBeanAdapter(mContext, mShowList2);
        lesson_item_list2.setAdapter(adapter2);
        getLessonsonEvaluate(mUserInfo.getUid(), lessonID, page2);
    }

    //显示评论还是开始学习
    private void showCommentView(boolean isShow) {

        if (isShow) {
            btn_menu_ll.setVisibility(View.GONE);
            write_comment_ll.setVisibility(View.VISIBLE);

        } else {
            btn_menu_ll.setVisibility(View.VISIBLE);
            write_comment_ll.setVisibility(View.GONE);
        }
    }

    public void setPlayBg(String url) {

        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.icon_img_error)//图片加载出来前，显示的图片
                .error(R.drawable.icon_img_error)//图片加载失败后，显示的图片
                .into(play_bg);
    }

    public void initAudio() {

        //设置播放类型
        videoPlayer.setPlayerType(ConstantKeys.IjkPlayerType.TYPE_IJK);

        //创建视频控制器
        controller = new VideoPlayerController(this);
        controller.setLoadingType(ConstantKeys.Loading.LOADING_QQ);
        controller.imageView().setBackgroundResource(R.color.black);
        controller.setOnVideoBackListener(new OnVideoBackListener() {
            @Override
            public void onBackClick() {
                onBackPressed();
            }
        });
        //设置视频控制器
        videoPlayer.setController(controller);
        //是否从上一次的位置继续播放
        videoPlayer.continueFromLastPosition(true);
        //设置播放速度
        videoPlayer.setSpeed(1.0f);
    }

    public void setAudio(VideoBean.ListBean listBean) {

        if (videoPlayer == null || listBean == null) {
            return;
        }

        videoPlayer.release();
        String videoTitle = listBean.getTitle();
        String urls = listBean.getVideourl();

        LogUtils.d("SonlistFragment 视频链接" + urls);

        //设置视频地址和请求头部
        videoPlayer.setUp(urls, null);
        controller.setTitle(videoTitle);

    }

    public void playAudio() {
        videoPlayer.start();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.advisory_ll:
                //todo

                break;

            case R.id.collect_ll:     //收藏课程
                saveCollect(mUserInfo.getUid(),mLessonBean.getId(),CommonParameters.LESSON);
                break;

            case R.id.play_start:       //开始播放

                play_bg.setVisibility(View.GONE);
                play_start.setVisibility(View.GONE);
                playAudio();
                break;

            case R.id.start_study:      //开始学习

                play_bg.setVisibility(View.GONE);
                play_start.setVisibility(View.GONE);
                playAudio();
                break;

            case R.id.submit_comment:       //提交评价

                String comment = comment_et.getText().toString();

                if ("".equals(comment)){
                    ToastUtil.show(mContext,"评论输入不能为空");
                    return;
                }

                subEvaluate(mUserInfo.getUid(),mLessonBean.getOrdersn(),comment);
                break;

            case R.id.teacher_detail:

                Intent intent = new Intent(mContext,TeacherActivity.class);
                intent.putExtra("teacherid",mLessonDetail.getTeacherid());
                startActivity(intent);

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


    class TabOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            if (mCurrentItemId == view.getId()) {
                return;
            }
            mCurrentItemId = view.getId();

            switch (mCurrentItemId) {
                case R.id.desc_ll:
//                    getlessonsonDesc(mUserInfo.getUid(), mLessonBean.getId());
                    break;

                case R.id.sonlist_ll:

                    break;

                case R.id.evaluate_ll:
//                    getLessonsonEvaluate(mUserInfo.getUid(), lessonID, page2);

                    break;
            }
            changeTabItemStyle(view);
        }
    }

    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        lesson_detail_ll.setVisibility(view.getId() ==  R.id.desc_ll ? View.VISIBLE:View.GONE);
        lesson_list_ll.setVisibility(view.getId() == R.id.sonlist_ll ? View.VISIBLE:View.GONE);
        lesson_evaluate_ll.setVisibility(view.getId() == R.id.evaluate_ll ? View.VISIBLE:View.GONE);

        sonlist_v.setBackgroundColor(view.getId() ==  R.id.sonlist_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.white));
        desc_v.setBackgroundColor(view.getId() == R.id.desc_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.white));
        evaluate_v.setBackgroundColor(view.getId() == R.id.evaluate_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.white));

        sonlist_tv.setTextColor(view.getId() == R.id.sonlist_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.black));
        desc_tv.setTextColor(view.getId() == R.id.desc_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.black));
        evaluate_tv.setTextColor(view.getId() == R.id.evaluate_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.black));
    }

    //-------------------------------------------课程详情--------------------------------------------


    private void setViewContent() {

        bookname_tv.setText("课程名称："+mLessonDetail.getBookname());
        difficulty_tv.setText("课程难度："+mLessonDetail.getDifficulty());
        lesson_descript.loadDataWithBaseURL(null,mLessonDetail.getDescript(),"text/html","uft-8",null);
        teacher_detail_content.loadDataWithBaseURL(null,mLessonDetail.getTeacherdes(),"text/html","uft-8",null);
    }


    //-------------------------------------------课程列表--------------------------------------------


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        adapter.setID(mShowList.get(position-1).getId());
        adapter.notifyDataSetChanged();
        LogUtils.i("SonlistFragment: onItemClick " + mShowList.get(position-1).getVideourl());
        setAudio(mShowList.get(position-1));
        playAudio();
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
                getLessonsonFindson(mUserInfo.getUid(), lessonID, Suffix_type, page); // 下拉刷新搜索

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
            findViewById(R.id.not_data).setVisibility(View.GONE);
            star_img.setImageResource(mVideoBean.getIscollect()==1  ? R.drawable.icon_tab_collect1: R.drawable.icon_tab_collect0);
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
                mShowList.addAll(mListBeans);

                if (mShowList.size()>0) {
                    setAudio(mShowList.get(0));
                    setPlayBg(mVideoBean.getPoster());
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
    
    //-------------------------------------------课程评价--------------------------------------------

    // 初始化列表
    private void initPullListView2() {

        lesson_item_list2 = findViewById(R.id.lesson_item_list2);
        setListView2();

        lesson_item_list2.setMode(PullToRefreshBase.Mode.BOTH);
        lesson_item_list2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {  //拉动时
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page2= 1;

                mSearchType2 = SEARCH_LESSON_PARAMETER2;
                getLessonsonEvaluate(mUserInfo.getUid(), lessonID, page2); // 下拉刷新搜索
                setViewForResult2(true,"");
                LogUtils.i("EvaluateFragment: onPullDownToRefresh 下拉" + page2 + "页");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page2++;

                mSearchType2 = SEARCH_LESSON_PULL_UP2;
                getLessonsonEvaluate(mUserInfo.getUid(), lessonID, page2);  // 上拉加载搜索

                LogUtils.i("EvaluateFragment: onPullUpToRefresh 下拉" + page2 + "页");
            }
        });

        lesson_item_list2.setOnItemClickListener(new AdapterView.OnItemClickListener() { //点击item时
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        lesson_item_list2.setOnScrollListener(new AbsListView.OnScrollListener() {  //列表滑动时
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int tempPos = lesson_item_list2.getRefreshableView().getFirstVisiblePosition();

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
        //                lesson_item_list2.getRefreshableView().smoothScrollToPosition(0);//移动到首部
        //            }
        //        });

    }

    //初始化列表控件上下拉的状态
    private void setListView2() {

        ILoadingLayout startLabels = lesson_item_list2.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = lesson_item_list2.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉刷新...");// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("正在载入...");// 刷新时
        endLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        //        headView = LayoutInflater.from(this).inflate(R.layout.headview, null);
        //        listview.getRefreshableView().addHeaderView(headView);//为ListView添加头布局
    }

    // 根据获取结果显示view
    private void setViewForResult2(boolean isSuccess,String msg) {

        if (isSuccess) {
            findViewById(R.id.not_data).setVisibility(View.GONE);

        } else {
            findViewById(R.id.not_data).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.not_data_tv)).setText(msg);
        }
    }

    // 更新课程列表数据
    private void upDataLessonListView2() {

        switch (mSearchType2) {

            case SEARCH_LESSON_PARAMETER2:

                mShowList2.clear();
                mShowList2.addAll(mListBeans2);

                LogUtils.i("EvaluateFragment: SEARCH_LESSON_FOR_PARAMETER "  + mShowList2.size());

                adapter2.notifyDataSetChanged();
                lesson_item_list2.getRefreshableView().smoothScrollToPosition(0);//移动到首部
                lesson_item_list2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lesson_item_list2.onRefreshComplete();
                    }
                }, 1000);
                break;

            case SEARCH_LESSON_PULL_UP2:

                mShowList2.addAll(mListBeans2);
                LogUtils.i("EvaluateFragment: SEARCH_LESSON_PULL_UP2 " + mShowList2.size());

                adapter2.notifyDataSetChanged();
                lesson_item_list2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lesson_item_list2.onRefreshComplete();
                        if (mListBeans2.size()==0){
                            ToastUtil.show(mContext,"没有更多结果");
                        }
                    }
                }, 1000);
                break;
        }
    }


    
    //--------------------------------------请求服务器数据-------------------------------------------
    
    // 获取课程详情
    private void getlessonsonDesc(final int uid, final int lessonid) {

        String url = HttpURL.LESSONSON_DESC_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("DescFragment: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            LogUtils.i("DescFragment: data " + data);
                            if (!"[]".equals(data)) {
                                mLessonDetail = new Gson().fromJson(data, LessonDetail.class);
                            }
                            //                            LogUtils.i("DescFragment: Descript " + mLessonDetail.getDescript());
                            //                            LogUtils.i("DescFragment: Teacherdes " + mLessonDetail.getTeacherdes());
                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS01);

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
                LogUtils.e("DescFragment: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Lessonsondesc" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("DescFragment: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("lessonid", lessonid);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("DescFragment json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 获取视频列表数据
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

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS02);

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

    // 获取评论列表
    private void getLessonsonEvaluate(final int uid, final int lessonid, final int pindex) {
        mListBeans2 = new ArrayList<>();
        String url = HttpURL.LESSONSON_EVALUATE_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("EvaluateFragment: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            LogUtils.i("EvaluateFragment: data " + data);
                            if (!"[]".equals(data)) {
                                mListBeans2 = new Gson().fromJson(data, new TypeToken<List<EvaluateBean>>() {
                                }.getType());
                            }

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS03);

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
                LogUtils.e("EvaluateFragment: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Lessonsonevaluate" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("EvaluateFragment: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("lessonid", lessonid);
                    obj.put("pindex", pindex);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("EvaluateFragment json1 " + obj.toString());

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


    // 提交课程订单评价
    private void subEvaluate(final int uid, final String order, final String content) {

        String url = HttpURL.EVALUATE_SUB_URL;
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

                            mHandler.obtainMessage(LOAD_DATA_SUCCESS2,msg).sendToTarget();

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
                LogUtils.e("LessonActivity: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Evaluatesub" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("LessonActivity: token " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("uid", uid);
                    obj.put("order", order);
                    obj.put("content", content);
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
