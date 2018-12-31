package com.yiyin.aobosh.activitys.yiYinClassroom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.yiyin.aobosh.bean.VideoBean;
import com.yiyin.aobosh.bean.RecommendLesson;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.commons.CommonParameters;
import com.yiyin.aobosh.commons.HttpURL;
import com.yiyin.aobosh.utils.SHA;
import com.yiyin.aobosh.utils.TimeUtils;
import com.yiyin.aobosh.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class YiYinClassroomActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnTouchListener {


    private List<VideoBean.ListBean> mListBeans;
    private Context mContext;
    private RequestQueue requestQueue;

    private SurfaceView mSurfaceView;
    private SeekBar mProgressbar;
    private MediaPlayer mMediaPlayer;
    private ProgressBar mloadinBar;

    private int page = 1;

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

                     playAudio();
                    break;

                case LOAD_DATA_FAILE:

                    ToastUtil.show(mContext, "失败");
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
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.app_title_bar), true);
        setContentView(R.layout.activity_yi_yin_classroom);

        init();
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {

        mloadinBar = findViewById(R.id.loading_bar);
        mProgressbar = findViewById(R.id.progress_bar);
        mSurfaceView = findViewById(R.id.video_sf);
        mProgressbar.setOnSeekBarChangeListener(this);
        mSurfaceView.setOnTouchListener(this);
    }


    private void initData() {

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            //获取里面的Persion里面的数据
            RecommendLesson.LessonBean lessonBean = (RecommendLesson.LessonBean) bundle.getSerializable("LessonBean");
            LogUtils.i("YiYinClassroomActivity: lessonBean id " + lessonBean.getId());
            UserInfo userInfo = GlobalParameterApplication.getInstance().getUserInfo();
            getLessonsonFindson(userInfo.getUid(), lessonBean.getId(), "", page);
        }

        mMediaPlayer = new MediaPlayer();
        // 拿到管理视频播放的控制器
        SurfaceHolder holder = mSurfaceView.getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            //视频控件销毁的时候调用  视频界面不存在了 Mediaplayer应该释放资源
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                    }
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            }

            @Override
            //视频控件准备完毕的时候调用
            public void surfaceCreated(SurfaceHolder holder) {
                mMediaPlayer.setDisplay(holder);
            }

            @Override
            //正在播放视频的时候调用  只要视频的界面一改变 就会调用该方法  使用频率非常高
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {

            }
        });
    }

    private void playAudio() {

        try {

            // 设置网络视频的路径
            Uri uri = Uri.parse(mListBeans.get(0).getVideourl());
            mMediaPlayer.setDataSource(mContext, uri);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                //耗时的网络请求操作完成了 就会调用该方法
                public void onPrepared(MediaPlayer mp) {
                    //界面加载完毕了 就隐藏加载框
                    mloadinBar.setVisibility(View.GONE);
                    mMediaPlayer.start();

                    //设置一个定时器 每半秒更新一次SeekBar当前的状态    mediaPlayer.getDuration()获取视频的播放长度
                    mProgressbar.setMax(mMediaPlayer.getDuration());

                    new Timer().schedule(new TimerTask() {

                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if (mMediaPlayer != null) {
                                        mProgressbar.setProgress(mMediaPlayer.getCurrentPosition());

                                        LogUtils.i("YiYinClassroomActivity: getVideotime " + mListBeans.get(0).getVideotime()
                                                + " getShow_time " + mListBeans.get(0).getShow_time()
                                                + " getAuto_show " + mListBeans.get(0).getAuto_show());
                                    }
                                }
                            });
                        }
                    }, 500, 500);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    //	拖动SeekBar完成的时候 调用
    public void onStopTrackingTouch(SeekBar seekBar) {
        //	拿到Seekbar当前的进度
        int progress = seekBar.getProgress();
        //	将Mediaplayer  seekTo跳到某一帧     开始播放
        mMediaPlayer.seekTo(progress);
        mMediaPlayer.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //		点击SurfaceView 隐藏进度条
        //		点击后显示---》过3秒---》自动隐藏
        mProgressbar.setVisibility(View.VISIBLE);
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        mProgressbar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }, 3000);
        return false;
    }


    //--------------------------------------请求服务器数据-------------------------------------------

    // 请求登录
    private void getLessonsonFindson(final int uid, final int lessonid, final String suffix, final int pindex) {

        String url = HttpURL.LESSONSON_SONLIST_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("YiYinClassroomActivity: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            VideoBean mVideoBean = new Gson().fromJson(data, VideoBean.class);
                            mListBeans = mVideoBean.getList();

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
                LogUtils.e("YiYinClassroomActivity: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Lessonsonsonlist" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("YiYinClassroomActivity: token " + token);
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

                LogUtils.i("YiYinClassroomActivity json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
