package com.yiyin.aobosh.activitys.yiYinClassroom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.RecommendLesson;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.bean.VideoBean;
import com.yiyin.aobosh.commons.CommonParameters;
import com.yiyin.aobosh.commons.HttpURL;
import com.yiyin.aobosh.fragments.Videos.DescFragment;
import com.yiyin.aobosh.fragments.Videos.EvaluateFragment;
import com.yiyin.aobosh.fragments.Videos.SonlistFragment;
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

public class LessonActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private RecommendLesson.LessonBean mLessonBean;
    private RequestQueue requestQueue;
    private UserInfo mUserInfo;

    private VideoPlayer videoPlayer;
    private VideoPlayerController controller;

    private ImageView play_bg, play_start;
    private LinearLayout sonlist_ll, desc_ll, evaluate_ll,btn_menu_ll,write_comment_ll,submit_comment;
    private TextView sonlist_tv, desc_tv, evaluate_tv,start_study;
    private View sonlist_v, desc_v, evaluate_v;
    private EditText comment_et;

    private LinearLayout advisory_ll,collect_ll;
    private ViewPager viewPager;
    private List<Fragment> fragmentsList;
    private DescFragment mDescFragment;
    private SonlistFragment mSonlistFragment;
    private EvaluateFragment mEvaluateFragment;
    private int mCurrentItemId = 1;

    private static final int LOAD_DATA_SUCCESS = 101;
    private static final int LOAD_DATA_FAILE = 102;
    private static final int LOAD_DATA_SUCCESS2 = 201;
    private static final int LOAD_DATA_FAILE2 = 202;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    String info = (String) msg.obj;
                    ToastUtil.show(mContext, info);

                    break;

                case LOAD_DATA_FAILE:

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
        intiFragment();
        initViewPager();
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
        sonlist_ll = findViewById(R.id.sonlist_ll);
        sonlist_tv = findViewById(R.id.sonlist_tv);
        sonlist_v = findViewById(R.id.sonlist_v);
        //目录
        desc_ll = findViewById(R.id.desc_ll);
        desc_tv = findViewById(R.id.desc_tv);
        desc_v = findViewById(R.id.desc_v);
        //评价
        evaluate_ll = findViewById(R.id.evaluate_ll);
        evaluate_tv = findViewById(R.id.evaluate_tv);
        evaluate_v = findViewById(R.id.evaluate_v);

        comment_et = findViewById(R.id.comment_et);

        advisory_ll = findViewById(R.id.advisory_ll);
        collect_ll = findViewById(R.id.collect_ll);
        start_study = findViewById(R.id.start_study);
        btn_menu_ll = findViewById(R.id.btn_menu_ll);
        write_comment_ll = findViewById(R.id.write_comment_ll);
        submit_comment = findViewById(R.id.submit_comment);

        TabOnClickListener listtener = new TabOnClickListener();
        sonlist_ll.setOnClickListener(listtener);
        desc_ll.setOnClickListener(listtener);
        evaluate_ll.setOnClickListener(listtener);

        play_start.setOnClickListener(this);
        advisory_ll.setOnClickListener(this);
        collect_ll.setOnClickListener(this);
        start_study.setOnClickListener(this);
        submit_comment.setOnClickListener(this);

//        if (videoPlayer.isIdle()) {
//            ToastUtil.show(mContext, "要点击播放后才能进入小窗口");
//        } else {
//            videoPlayer.enterTinyWindow();
//        }
//
//        videoPlayer.enterVerticalScreenScreen();
//        videoPlayer.enterFullScreen();
//        videoPlayer.restart();
    }

    private void intiFragment() {
        fragmentsList = new ArrayList<>();

        mDescFragment = new DescFragment();
        mSonlistFragment = new SonlistFragment();
        mEvaluateFragment = new EvaluateFragment();

        fragmentsList.add(mDescFragment);
        fragmentsList.add(mSonlistFragment);
        fragmentsList.add(mEvaluateFragment);
    }

    private void initViewPager() {

        viewPager = findViewById(R.id.setting_viewpager);
        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager(), fragmentsList));
        if (GlobalParameterApplication.isShowComment) {
            viewPager.setCurrentItem(2);
            changeTabItemStyle(evaluate_ll);
            showCommentView(true);
        } else {
            viewPager.setCurrentItem(1);
            showCommentView(false);
        }
        //        viewPager.setPageMargin(PxUtils.dpToPx(12,this));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        mCurrentItemId = sonlist_ll.getId();
                        changeTabItemStyle(sonlist_ll);
                        showCommentView(false);
                        break;

                    case 1:
                        mCurrentItemId = desc_ll.getId();
                        changeTabItemStyle(desc_ll);
                        showCommentView(false);
                        break;

                    case 2:
                        mCurrentItemId = evaluate_ll.getId();
                        changeTabItemStyle(evaluate_ll);

                        if (GlobalParameterApplication.isShowComment){
                            showCommentView(true);
                        } else {
                            showCommentView(false);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });
    }

    private void initData() {

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            //获取里面的Persion里面的数据
            mLessonBean = (RecommendLesson.LessonBean) bundle.getSerializable("LessonBean");
            LogUtils.i("LessonActivity: lessonBean id " + mLessonBean.getId());
    }

        initAudio();
    }

    public RecommendLesson.LessonBean getLessonBean() {
        return mLessonBean;
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


    class TabOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            if (mCurrentItemId == view.getId()) {
                return;
            }
            mCurrentItemId = view.getId();

            switch (mCurrentItemId) {
                case R.id.sonlist_ll:
                    viewPager.setCurrentItem(0);
                    break;

                case R.id.desc_ll:
                    viewPager.setCurrentItem(1);
                    break;

                case R.id.evaluate_ll:
                    viewPager.setCurrentItem(2);

                    break;
            }

            changeTabItemStyle(view);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.advisory_ll:
                //todo

                break;

            case R.id.collect_ll:
                saveCollect(mUserInfo.getUid(),mLessonBean.getId(),CommonParameters.LESSON);
                break;

            case R.id.play_start:

                play_bg.setVisibility(View.GONE);
                play_start.setVisibility(View.GONE);
                playAudio();
                break;

            case R.id.start_study:

                play_bg.setVisibility(View.GONE);
                play_start.setVisibility(View.GONE);
                playAudio();
                break;

            case R.id.submit_comment:

                String comment = comment_et.getText().toString();

                if ("".equals(comment)){
                    ToastUtil.show(mContext,"评论输入不能为空");
                    return;
                }

                subEvaluate(mUserInfo.getUid(),mLessonBean.getOrdersn(),comment);
                break;
        }

    }

    private void showCommentView(boolean isShow) {

        if (isShow) {
            btn_menu_ll.setVisibility(View.GONE);
            write_comment_ll.setVisibility(View.VISIBLE);

        } else {
            btn_menu_ll.setVisibility(View.VISIBLE);
            write_comment_ll.setVisibility(View.GONE);
        }
    }

    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        sonlist_v.setBackgroundColor(view.getId() ==  R.id.sonlist_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.white));
        desc_v.setBackgroundColor(view.getId() == R.id.desc_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.white));
        evaluate_v.setBackgroundColor(view.getId() == R.id.evaluate_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.white));

        sonlist_tv.setTextColor(view.getId() == R.id.sonlist_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.black));
        desc_tv.setTextColor(view.getId() == R.id.desc_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.black));
        evaluate_tv.setTextColor(view.getId() == R.id.evaluate_ll ? getResources().getColor(R.color.btn_selected) : getResources().getColor(R.color.black));
    }

    public class TabAdapter extends FragmentPagerAdapter {

        List<Fragment> Fragments;

        public TabAdapter(FragmentManager fm, List fragments) {
            super(fm);
            Fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return Fragments.get(position);
        }

        @Override
        public int getCount() {
            return Fragments.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            Fragment fragment = Fragments.get(position);
            if (fragment != null)
                fragment.onDestroyView();
        }
    }


    //--------------------------------------请求服务器数据-------------------------------------------

    // 收藏点
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

                            mHandler.obtainMessage(LOAD_DATA_SUCCESS,msg).sendToTarget();

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
