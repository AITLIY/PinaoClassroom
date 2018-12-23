package com.yiyin.aobosh.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.adapter.LessonCategoryAdapter;
import com.yiyin.aobosh.adapter.ViewPagerAdapter;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.utils.PxUtils;
import com.yiyin.aobosh.utils.SHA;
import com.yiyin.aobosh.utils.TimeUtils;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.Banner;
import com.yiyin.aobosh.bean.LessonCategory;
import com.yiyin.aobosh.bean.RecommendLesson;
import com.yiyin.aobosh.commons.CommonParameters;
import com.yiyin.aobosh.commons.HttpURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主页
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<ImageView> mImageList; //轮播的图ImageView集合
    private TextView mTvPagerTitle;     //轮播标题
    private List<View> mDots;           //轮播小点
    private int previousPosition = 0;   //前一个被选中的position
    private static final int DELAYED_TIME = 2000;//间隔时间
    // 在values文件夹下创建了ids.xml文件，并定义了4张轮播图对应的viewid，用于点击事件
    private int[] imgae_ids = new int[]{R.id.pager_image1, R.id.pager_image2, R.id.pager_image3, R.id.pager_image4, R.id.pager_image5};

    private GridView mGridView;
    private BaseAdapter mGridViewAdapter;

    private LinearLayout mContainer1, mContainer2, mContainer3;
    private TextView container1_title, container2_title, container3_title;
    private TextView container2_more, container3_more;

    private List<Banner> mBanners;                      //轮播图对象的集合
    private List<LessonCategory> mlessonCategory;       //课程分类对象的集合
    private List<RecommendLesson> mRecommendLessons;    //课程推荐对象的集合

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;
    private static final int LOAD_DATA2_SUCCESS = 201;
    private static final int LOAD_DATA2_FAILE = 202;
    private static final int LOAD_DATA3_SUCCESS = 301;
    private static final int LOAD_DATA3_FAILE = 302;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    initViewPagerData();
                    initAdapter();
                    autoPlayView();
                    break;

                case LOAD_DATA1_FAILE:

                    
                    break;

                case LOAD_DATA2_SUCCESS:

                    initAdapter2();
                    break;

                case LOAD_DATA2_FAILE:


                    break;

                case LOAD_DATA3_SUCCESS:

                    initRecommend();
                    break;

                case LOAD_DATA3_FAILE:


                    break;

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getContext();

        init();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void init() {

        initView();//初始化View
        initData();//初始化数据
    }

    private void initView() {
        mViewPager = (ViewPager) mView.findViewById(R.id.banner_vp);
        mTvPagerTitle = (TextView) mView.findViewById(R.id.tv_pager_title);

        mGridView = (GridView) mView.findViewById(R.id.class_category_gv);

        mContainer1 = (LinearLayout) mView.findViewById(R.id.container1);
        mContainer2 = (LinearLayout) mView.findViewById(R.id.container2);
        mContainer3 = (LinearLayout) mView.findViewById(R.id.container3);
        container1_title = (TextView) mView.findViewById(R.id.container1_title);
        container2_title = (TextView) mView.findViewById(R.id.container2_title);
        container3_title = (TextView) mView.findViewById(R.id.container3_title);
        container2_more = (TextView) mView.findViewById(R.id.container2_more);
        container3_more = (TextView) mView.findViewById(R.id.container3_more);
    }

    private void initData() {
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        getBannerData();
        getCategoryData();
        getRecommendData();
    }

    //-------------------------------------------轮播图---------------------------------------------------

    //初始化ViewPager的内部的view
    public void initViewPagerData() {

        //添加图片到图片列表里
        mImageList = new ArrayList<>();
        ImageView iv;
        for (int i = 0; i < mBanners.size(); i++) {
            iv = new ImageView(mContext);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setId(imgae_ids[i]);                         //给ImageView设置id
            iv.setOnClickListener(new pagerImageOnClick());//设置ImageView点击事件
            LogUtils.i("HomeFragment: Banner " + mBanners.get(i).getImg());

            Glide.with(mContext)
                    .load(mBanners.get(i).getImg())
                    .into(iv);

            mImageList.add(iv);
        }

        //添加轮播点
        LinearLayout container = (LinearLayout) mView.findViewById(R.id.ll_point_container);
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.white_poi);
        mDots = addDots(mImageList.size(), container, drawable);

    }

    //图片点击事件
    private class pagerImageOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pager_image1:
                    LogUtils.i("图片1被点击");
                    break;
                case R.id.pager_image2:
                    LogUtils.i("图片2被点击");
                    break;
                case R.id.pager_image3:
                    LogUtils.i("图片3被点击");
                    break;
                case R.id.pager_image4:
                    LogUtils.i("图片4被点击");
                    break;
                case R.id.pager_image5:
                    LogUtils.i("图片5被点击");
                    break;
            }
        }
    }

    //为ViewPager配置Adater
    public void initAdapter() {

        mViewPagerAdapter = new ViewPagerAdapter(mImageList, mViewPager);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //伪无限循环，滑到最后一张图片又从新进入第一张图片
                int newPosition = position % mImageList.size();
//                LogUtils.i("HomeFragment: Banner newPosition " + newPosition);
                //图片下面设置显示文本
//                mTvPagerTitle.setText(Banner.get(newPosition).getText);

                //设置轮播点
                View newView = mDots.get(newPosition);
                Drawable gray_poi = mContext.getResources().getDrawable(R.drawable.gray_poi);
                newView.setBackground(gray_poi);

                View oldView = mDots.get(previousPosition);
                Drawable white_poi = mContext.getResources().getDrawable(R.drawable.white_poi);
                oldView.setBackground(white_poi);

                // 把当前的索引赋值给前一个索引变量, 方便下一次再切换.
                previousPosition = newPosition;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setFirstLocation();
    }

    //设置ViewPager的默认选中
    private void setFirstLocation() {

        // 把ViewPager设置为默认选中Integer.MAX_VALUE / 2，从十几亿次开始轮播图片，达到无限循环目的;
        int m = (Integer.MAX_VALUE / 2) % mImageList.size();
        int currentPosition = Integer.MAX_VALUE / 2 - m;

        mViewPager.setCurrentItem(0);
    }

    //开启线程，自动播放
    private void autoPlayView() {

        if (runTask != null) {
            mHandler.removeCallbacks(runTask);
        }
        mHandler.postDelayed(runTask, DELAYED_TIME);
    }

    private Runnable runTask = new Runnable() {

        @Override
        public void run() {
            //            LogUtils.i("HomeFragment: Banner runTask " + mViewPager.getCurrentItem());
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            mHandler.postDelayed(this, DELAYED_TIME);

        }
    };

    //添加小点到list
    public List<View> addDots(int number, final LinearLayout  container, Drawable backgrount) {
        List<View> listDots = new ArrayList<>();
        int dotId;
        for (int i = 0; i < number; i++) {

            dotId = addDot(container, backgrount);
            listDots.add(mView.findViewById(dotId));
        }
        return listDots;
    }

    //设置轮播小点 并添加到container
    public int addDot(final LinearLayout container, Drawable backgount) {
        final View dot = new View(mContext);
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dotParams.width = PxUtils.dip2px(mContext, 6);
        dotParams.height = PxUtils.dip2px(mContext, 6);
        dotParams.setMargins(PxUtils.dip2px(mContext, 4), 0, PxUtils.dip2px(mContext, 4), 0);

        dot.setLayoutParams(dotParams);
        dot.setBackground(backgount);
        dot.setId(View.generateViewId());
        container.addView(dot); //添加小点到横向线性布局

        return dot.getId();
    }

    //-------------------------------------------课程分类--------------------------------------------

    //初始化GrdView的
    @SuppressLint("ClickableViewAccessibility")
    private void initAdapter2() {
        LogUtils.i("HomeFragment: Category mlessonCategory.size " + mlessonCategory.size());
        mGridViewAdapter = new LessonCategoryAdapter(getContext(),mlessonCategory);
        mGridView.setAdapter(mGridViewAdapter); // 为mGridView设置Adapter
        mGridView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return MotionEvent.ACTION_MOVE == event.getAction() ? true : false;// 设置mGridView不能滑动
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // 添加列表项被单击的监听器
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 显示被单击的图片
                LogUtils.i("HomeFragment: Category onItemClick " + position);

            }
        });

    }

    //-------------------------------------------课程推荐--------------------------------------------


    private void initRecommend() {
        LogUtils.i("HomeFragment: Recommend mRecommendLessons.size " + mRecommendLessons.size());

        for (RecommendLesson lesson : mRecommendLessons) {

            LogUtils.i("HomeFragment: Recommend RecommendLesson name " + lesson.getRec_name() + ", Displayorder " + lesson.getDisplayorder()
                    + ", Show_style " + lesson.getShow_style() + ", Is_show " + lesson.getIs_show());

            final List<RecommendLesson.LessonBean>  beans = lesson.getLesson();

            LogUtils.i("HomeFragment: Recommend LessonBean.size " + beans.size());

            if (lesson.getIs_show() == 1){

                switch (lesson.getShow_style()) {

                    case 3:
                        LinearLayout layout1 = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.recommend_type1,mContainer1,false);
                        mContainer1.addView(layout1);
                        container1_title.setText(lesson.getRec_name());

                        //item1
                        ImageView type1_img1 = (ImageView) layout1.findViewById(R.id.type1_img1);
                        TextView type1_title1 = (TextView) layout1.findViewById(R.id.type1_title1);
                        Glide.with(mContext)
                                .load(beans.get(0).getImages())
                                .into(type1_img1);
                        type1_title1.setText(beans.get(0).getBookname());

                        layout1.findViewById(R.id.type1_item1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                LogUtils.i("HomeFragment: Recommend LessonBean 点击了 " + beans.get(0).getBookname());
                            }
                        });

                        //item2
                        ImageView type1_img2 = (ImageView) layout1.findViewById(R.id.type1_img2);
                        TextView type1_title2 = (TextView) layout1.findViewById(R.id.type1_title2);
                        Glide.with(mContext)
                                .load(beans.get(1).getImages())
                                .into(type1_img2);
                        type1_title2.setText(beans.get(1).getBookname());

                        layout1.findViewById(R.id.type1_item2).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                LogUtils.i("HomeFragment: Recommend LessonBean 点击了 " + beans.get(1).getBookname());
                            }
                        });
                        break;

                        case 2:
                            LinearLayout layout2 = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.recommend_type2,mContainer2,false);
                            mContainer2.addView(layout2);
                            container2_title.setText(lesson.getRec_name());
                            container2_more.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LogUtils.i("HomeFragment: Recommend LessonBean 点击了 " + mRecommendLessons.get(1).getRec_name());
                                }
                            });


                            ImageView item = (ImageView) layout2.findViewById(R.id.type2_item1);
                            Glide.with(mContext)
                                    .load(beans.get(0).getImages())
                                    .into(item);
                            //item1
                            item.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LogUtils.i("HomeFragment: Recommend LessonBean 点击了 " + beans.get(0).getBookname());
                                }
                            });

                             //item1
                            item.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LogUtils.i("HomeFragment: Recommend LessonBean 点击了 " + beans.get(0).getBookname());
                                }
                            });

                            //item2
                            ImageView type2_img2 = (ImageView) layout2.findViewById(R.id.type2_img2);
                            TextView type2_title_tv2 =  (TextView) layout2.findViewById(R.id.type2_title_tv2);
                            TextView type2_people_tv2 =  (TextView) layout2.findViewById(R.id.type2_people_tv2);
                            TextView type2_price_tv2 =  (TextView) layout2.findViewById(R.id.type2_price_tv2);
                            Glide.with(mContext)
                                    .load(beans.get(1).getImages())
                                    .into(type2_img2);
                            type2_title_tv2.setText(beans.get(1).getBookname());
                            type2_people_tv2.setText(beans.get(1).getBuynum() + beans.get(1).getVirtual_buynum()+"人在学");
                            type2_price_tv2.setText("¥" + beans.get(1).getPrice());
                            layout2.findViewById(R.id.type2_item2).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LogUtils.i("HomeFragment: Recommend LessonBean 点击了 " + beans.get(1).getBookname());
                                }
                            });

                            //item3
                            ImageView type2_img3 = (ImageView) layout2.findViewById(R.id.type2_img3);
                            TextView type2_title_tv3 =  (TextView) layout2.findViewById(R.id.type2_title_tv3);
                            TextView type2_people_tv3 =  (TextView) layout2.findViewById(R.id.type2_people_tv3);
                            TextView type2_price_tv3 =  (TextView) layout2.findViewById(R.id.type2_price_tv3);
                            Glide.with(mContext)
                                    .load(beans.get(2).getImages())
                                    .into(type2_img3);
                            type2_title_tv3.setText(beans.get(2).getBookname());
                            type2_people_tv3.setText(beans.get(2).getBuynum() + beans.get(2).getVirtual_buynum()+"人在学");
                            type2_price_tv3.setText("¥" + beans.get(2).getPrice());
                            layout2.findViewById(R.id.type2_item3).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LogUtils.i("HomeFragment: Recommend LessonBean 点击了 " + beans.get(2).getBookname());
                                }
                            });

                            //item4
                            ImageView type2_img4 = (ImageView) layout2.findViewById(R.id.type2_img4);
                            TextView type2_title_tv4 =  (TextView) layout2.findViewById(R.id.type2_title_tv4);
                            TextView type2_people_tv4 =  (TextView) layout2.findViewById(R.id.type2_people_tv4);
                            TextView type2_price_tv4 =  (TextView) layout2.findViewById(R.id.type2_price_tv4);
                            Glide.with(mContext)
                                    .load(beans.get(3).getImages())
                                    .into(type2_img4);
                            type2_title_tv4.setText(beans.get(3).getBookname());
                            type2_people_tv4.setText(beans.get(3).getBuynum() + beans.get(3).getVirtual_buynum()+"人在学");
                            type2_price_tv4.setText("¥" + beans.get(3).getPrice());
                            layout2.findViewById(R.id.type2_item4).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LogUtils.i("HomeFragment: Recommend LessonBean 点击了 " + beans.get(3).getBookname());
                                }
                            });
                            break;

                    case 1:
                        LinearLayout layout3 = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.recommend_type3,mContainer3,false);
                        mContainer3.addView(layout3);
                        container3_title.setText(lesson.getRec_name());
                        container3_more.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LogUtils.i("HomeFragment: Recommend LessonBean 点击了 " + mRecommendLessons.get(2).getRec_name());
                            }
                        });

                        //item1
                        ImageView type3_img1 = (ImageView) layout3.findViewById(R.id.type3_img1);
                        TextView type3_title1 = (TextView) layout3.findViewById(R.id.type3_title1);
                        TextView type3_class_price1 = (TextView) layout3.findViewById(R.id.type3_class_price1);
                        TextView type3_class_quntity1 = (TextView) layout3.findViewById(R.id.type3_class_quntity1);
                        TextView type3_people1 = (TextView) layout3.findViewById(R.id.type3_people1);
                        Glide.with(mContext)
                                .load(beans.get(0).getImages())
                                .into(type3_img1);
                        type3_title1.setText(beans.get(0).getBookname());
                        type3_class_price1.setText(beans.get(0).getPrice());
                        type3_class_quntity1.setText("已更新"+beans.get(0).getCount()+"节课");
                        type3_people1.setText(beans.get(0).getBuynum() + beans.get(0).getVirtual_buynum() + beans.get(0).getVisit_number()+"");

                        layout3.findViewById(R.id.type3_item1).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                LogUtils.i("HomeFragment: Recommend LessonBean 点击了 " + beans.get(0).getBookname());
                            }
                        });

                        //item2
                        ImageView type3_img2 = (ImageView) layout3.findViewById(R.id.type3_img2);
                        TextView type3_title2 = (TextView) layout3.findViewById(R.id.type3_title2);
                        TextView type3_class_price2 = (TextView) layout3.findViewById(R.id.type3_class_price2);
                        TextView type3_class_quntity2 = (TextView) layout3.findViewById(R.id.type3_class_quntity2);
                        TextView type3_people2 = (TextView) layout3.findViewById(R.id.type3_people2);
                        Glide.with(mContext)
                                .load(beans.get(1).getImages())
                                .into(type3_img2);
                        type3_title2.setText(beans.get(1).getBookname());
                        type3_class_price2.setText(beans.get(1).getPrice());
                        type3_class_quntity2.setText("已更新"+beans.get(1).getCount()+"节课");
                        type3_people2.setText(beans.get(1).getBuynum() + beans.get(1).getVirtual_buynum() + beans.get(1).getVisit_number()+"");

                        layout3.findViewById(R.id.type3_item2).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                LogUtils.i("HomeFragment: Recommend LessonBean 点击了 " + beans.get(1).getBookname());
                            }
                        });

                        break;
                }

            }

        }


    }

    //--------------------------------------请求服务器数据-------------------------------------------

    // 1.获取轮播图数据
    private void getBannerData() {

        String url = HttpURL.BANNER_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("HomeFragment: result1 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String data = jsonObject.getString("data");
                            mBanners = new Gson().fromJson(data, new TypeToken<List<Banner>>(){}.getType());
                            LogUtils.i("HomeFragment: mBanners.size " + mBanners.size());

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
                LogUtils.e("HomeFragment: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(LOAD_DATA1_FAILE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Indexbanner" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("HomeFragment: token1 " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("HomeFragment json1 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 2.获取课程分类
    private void getCategoryData() {

        String url = HttpURL.CATEGORY_LIST_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("HomeFragment: result2 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String  data = jsonObject.getString("data");
                            mlessonCategory = new Gson().fromJson(data, new TypeToken<List<LessonCategory>>(){}.getType());
                            GlobalParameterApplication.lessonCategory = mlessonCategory;

                            mHandler.sendEmptyMessage(LOAD_DATA2_SUCCESS);

                            return;
                        }


                        mHandler.sendEmptyMessage(LOAD_DATA2_FAILE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA2_FAILE);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("HomeFragment: volleyError2 " + volleyError.toString());
                mHandler.sendEmptyMessage(LOAD_DATA2_FAILE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Lessonsoncategorylist" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("HomeFragment: token2 " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("HomeFragment json2 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 3.获取推荐课程
    private void getRecommendData() {

        String url = HttpURL.RECOMMEND_LESSON_URL;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("HomeFragment: result3 " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");

                        if ("200".equals(code)) {

                            String  data = jsonObject.getString("data");
                            mRecommendLessons = new Gson().fromJson(data, new TypeToken<List<RecommendLesson>>(){}.getType());

                            List<RecommendLesson.LessonBean> userBeanList = mRecommendLessons.get(0).getLesson();

                            mHandler.sendEmptyMessage(LOAD_DATA3_SUCCESS);
                            return;
                        }

                        mHandler.sendEmptyMessage(LOAD_DATA3_FAILE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA3_FAILE);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("HomeFragment: volleyError3 " + volleyError.toString());
                mHandler.sendEmptyMessage(LOAD_DATA3_FAILE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();
                JSONObject obj = new JSONObject();

                try {

                    String token = "Lessonsonrecommendlesson" + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                    LogUtils.i("HomeFragment: token3 " + token);
                    String sha_token = SHA.encryptToSHA(token);

                    obj.put("access_token", sha_token);
                    obj.put("device", CommonParameters.ANDROID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LogUtils.i("HomeFragment json3 " + obj.toString());

                map.put("dt", obj.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }
}
