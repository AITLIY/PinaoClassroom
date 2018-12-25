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

    // 5.APP手机登录
    public static final String OAUTH_LOGIN_URL = BASE_URL + "oauth/login";

    // 6.发送注册验证码
    public static final String SMS_SENDSMSCAPTCHA_URL = BASE_URL + "sms/sendSmsCaptcha";

    // 7.校对注册验/忘记密码的验证码
    public static final String SMS_CHECKSMSCAPTCHA_URL = BASE_URL + "sms/checkSmsCaptcha";

    // 8.用户注册
    public static final String OAUTH_REGISTER_URL = BASE_URL + "oauth/register";

    // 9.修改密码
    public static final String OAUTH_MODIFYPSW_URL = BASE_URL + "oauth/modifypsw";

    // 10.发送修改手机/忘记密码的验证码
    public static final String SMS_SENDSMSMOBILE_URL = BASE_URL + "sms/sendSmsMobile";

    // 11.修改手机
    public static final String OAUTH_MODIFYMBL_URL = BASE_URL + "oauth/modifymbl";

    // 12.忘记密码
    public static final String OAUTH_FORGET_URL = BASE_URL + "oauth/forget";


    // 1.会员等级列表和用户会员等级
    public static final String VIP_SHOW_URL = BASE_URL + "vip/show";
}
