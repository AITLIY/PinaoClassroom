package com.yiyin.aobosh.commons;

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

    // 5.收藏的课程
    public static final String COLLECT_LESSON_URL = BASE_URL + "collect/clesson";

    // 6.收藏的讲师
    public static final String COLLECT_TEACHER_URL = BASE_URL + "collect/cteacher";

    // 7.我的足迹
    public static final String OAUTH_HISTORY_URL = BASE_URL + "oauth/history";

    // 8.我的课程
    public static final String MYLESSON_MYLESSON_URL = BASE_URL + "mylesson/mylesson";

    // 9.课程播放页
    public static final String LESSONSON_FINDSON_URL = BASE_URL + "lessonson/findson";

    // 10.章节列表
    public static final String LESSONSON_SONLIST_URL = BASE_URL + "lessonson/sonlist";

    // 11.课程或讲师收藏
    public static final String LESSONSON_COLLECT_URL = BASE_URL + "lessonson/collect";

    // 12.课程详情
    public static final String LESSONSON_DESC_URL = BASE_URL + "lessonson/desc";

    // 13.评价列表
    public static final String LESSONSON_EVALUATE_URL = BASE_URL + "lessonson/evaluate";

    // 14.提交课程订单评价
    public static final String EVALUATE_SUB_URL = BASE_URL + "evaluate/sub";

    // 15.讲师详情
    public static final String TEACHER_TEACHERDETAIL_URL = BASE_URL + "teacher/teacherdetail";

    // 16.讲师课程列表
    public static final String TEACHER_TEACHERLESSON_URL = BASE_URL + "teacher/teacherlesson";

    // 16.讲师课程列表
    public static final String LESSONSON_PLAY_URL = BASE_URL + "lessonson/play";




    // 1.自动登录
    public static final String INDEX_OAUTH_URL = BASE_URL + "index/oauth";

    // 2.APP手机登录
    public static final String OAUTH_LOGIN_URL = BASE_URL + "oauth/login";

    // 3.发送注册验证码
    public static final String SMS_SENDSMSCAPTCHA_URL = BASE_URL + "sms/sendSmsCaptcha";

    // 4.校对注册验/忘记密码的验证码
    public static final String SMS_CHECKSMSCAPTCHA_URL = BASE_URL + "sms/checkSmsCaptcha";

    // 5.用户注册
    public static final String OAUTH_REGISTER_URL = BASE_URL + "oauth/register";

    // 6.发送忘记密码/修改手机的验证码
    public static final String SMS_SENDSMSMOBILE_URL = BASE_URL + "sms/sendSmsMobile";

    // 7.忘记密码
    public static final String OAUTH_FORGETPWD_URL = BASE_URL + "oauth/forgetpwd";

    // 8.修改手机
    public static final String OAUTH_MODIFYMBL_URL = BASE_URL + "oauth/modifymbl";

    // 9.修改密码
    public static final String OAUTH_MODIFYPSW_URL = BASE_URL + "oauth/modifypsw";

    // 10.个人资料修改
    public static final String OAUTH_MODIFYINFO_URL = BASE_URL + "oauth/modifyinfo";

    // 11.微信授权
    public static final String OAUTH_GETSESSION_URL = BASE_URL + "oauth/getSession";

    // 12.微信登录
    public static final String OAUTH_WXLOGIN_URL = BASE_URL + "oauth/wxlogin";




    // 1.购买会员订单支付状态
    public static final String VIP_BUY_URL = BASE_URL + "vip/buy";

    // 2.会员等级列表和用户会员等级
    public static final String VIP_SHOW_URL = BASE_URL + "vip/show";

    // 3.VIP订单
    public static final String VIP_VIPORDER_URL = BASE_URL + "vip/viporder";

    // 4.卡密激活
    public static final String VIP_CARD_URL = BASE_URL + "vip/card";

    // 5.我的优惠券
    public static final String COUPON_COUPON_URL = BASE_URL + "coupon/coupon";

    // 6.课程优惠码转换
    public static final String COUPON_TRANSFOR_URL = BASE_URL + "coupon/transfor";

    // 7.购买会员时生成的订单信息
    public static final String VIP_BUYVIP_URL = BASE_URL + "vip/buyvip";

    // 8.课程订单信息
    public static final String LESSONSON_CREATEORDER_URL = BASE_URL + "lessonson/createorder";

    // 9.我的课程列表点击后订单信息
    public static final String LESSONSON_FINDORDER_URL = BASE_URL + "lessonson/findorder";

    // 10.取消订单
    public static final String LESSONSON_CANSLEORDER_URL = BASE_URL + "lessonson/cansleorder";





}
