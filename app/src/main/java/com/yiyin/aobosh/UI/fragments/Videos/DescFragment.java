package com.yiyin.aobosh.UI.fragments.Videos;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.LessonDetail;
import com.yiyin.aobosh.bean.RecommendLesson;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.commons.CommonParameters;
import com.yiyin.aobosh.commons.HttpURL;
import com.yiyin.aobosh.utils.SHA;
import com.yiyin.aobosh.utils.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DescFragment extends Fragment {

    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;
    private UserInfo mUserInfo;
    private RecommendLesson.LessonBean mLessonBean;
    private LessonDetail mLessonDetail;
    private WebView lesson_descript,teacher_detail_content;
    private TextView bookname_tv,difficulty_tv;

    private int lessonID = 1;

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

                    setViewForResult(true,"");
                    break;

                case LOAD_DATA_FAILE:

                    setViewForResult(false,"查询数据失败~");
                    break;

                case NET_ERROR:

                    setViewForResult(false,"网络异常,请稍后重试~");
                    break;
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_desc, container, false);
        mContext = getContext();

        init();
        return mView;
    }

    private void init() {

        initView();
        initData();
    }

    private void initView() {


    }

    private void initData() {

        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();
//        mLessonBean = ((LessonActivity) getActivity()).getLessonBean();
        lessonID = mLessonBean.getId();

        getlessonsonDesc(mUserInfo.getUid(), lessonID);
        
    }

    // 根据获取结果显示view
    private void setViewForResult(boolean isSuccess,String msg) {

        if (isSuccess) {
            mView.findViewById(R.id.lesson_detail).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.not_data).setVisibility(View.GONE);
            setViewContent();

        } else {
            mView.findViewById(R.id.lesson_detail).setVisibility(View.GONE);
            mView.findViewById(R.id.not_data).setVisibility(View.VISIBLE);
            ((TextView) mView.findViewById(R.id.not_data_tv)).setText(msg);
        }
    }

    private void setViewContent() {
        bookname_tv.setText("课程名称："+mLessonDetail.getBookname());
        difficulty_tv.setText("课程难度："+mLessonDetail.getDifficulty());
        lesson_descript.loadDataWithBaseURL(null,mLessonDetail.getDescript(),"text/html","uft-8",null);
        teacher_detail_content.loadDataWithBaseURL(null,mLessonDetail.getTeacherdes(),"text/html","uft-8",null);
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


}
