package com.yiyin.aobosh.bean;

/**
 * Created by ALIY on 2018/12/23 0023.
 */

public class UserInfo {


    /**
     * uid : 76266
     * mobile : 18117052244
     * nickname : android01
     * avatar : http://www.a8du.com/attachment/avatar.jpg
     * token : b684a85136418a9bf5004172b6100f215c4eb809
     */

    private int uid;
    private String mobile;
    private String nickname;
    private String avatar;
    private String token;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
