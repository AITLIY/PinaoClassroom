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

    // 5.收藏课程
    public static final String COLLECT_LESSON_URL = BASE_URL + "collect/clesson";

    // 6.收藏讲师
    public static final String COLLECT_TEACHER_URL = BASE_URL + "collect/cteacher";

    // 7.我的足迹
    public static final String OAUTH_HISTORY_URL = BASE_URL + "oauth/history";




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

    // 9.修改手机
    public static final String OAUTH_MODIFYMBL_URL = BASE_URL + "oauth/modifymbl";

    // 8.修改密码
    public static final String OAUTH_MODIFYPSW_URL = BASE_URL + "oauth/modifypsw";



    // 1.会员等级列表和用户会员等级
    public static final String VIP_BUY_URL = BASE_URL + "vip/buy";

    // 2.会员等级列表和用户会员等级
    public static final String VIP_SHOW_URL = BASE_URL + "vip/show";

    // 3.卡密激活
    public static final String VIP_CARD_URL = BASE_URL + "vip/card";


}
