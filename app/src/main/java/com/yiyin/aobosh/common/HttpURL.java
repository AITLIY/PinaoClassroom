package com.yiyin.aobosh.common;

/**
 * 访问服务器的网址
 * Created by ALIY on 2018/12/9 0009.
 */

public class HttpURL {

    // BASE_URL
    public static final String BASE_URL = "http://app.aobosh.cn/api/";

    // 1.轮播图
    public static final String BANNER_URL = BASE_URL + "index/banner";

    // 2.课程分类
    public static final String CATEGORY_LIST_URL = BASE_URL + "lessonson/categorylist";

    // 3.首页推荐板块内容（推荐课程，免费课程，技巧提升）
    public static final String RECOMMEND_LESSON_URL = BASE_URL + "lessonson/recommendlesson";

    // 4.全部课程  筛选（排序和查询）
    public static final String LESSONSON_SEARCH_URL = BASE_URL + "lessonson/search";



}
