package com.yiyin.aobosh.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ALIY on 2019/1/12 0012.
 */

public class WxPayBean {

    /**
     * appid : wx3f889385b49ca1b8
     * partnerid : 1521303781
     * prepayid : wx120013445601160ade468d574046367840
     * package : Sign=WXPay
     * noncestr : 2qk7v4JMyPRGidDG
     * timestamp : 1547223224
     * sign : A92A956ED551F0E38E61C8276B387193
     */

    private String appid;
    private String partnerid;
    private String prepayid;
    @SerializedName("package")
    private String packageX;
    private String noncestr;
    private int timestamp;
    private String sign;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getPackageX() {
        return packageX;
    }

    public void setPackageX(String packageX) {
        this.packageX = packageX;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
