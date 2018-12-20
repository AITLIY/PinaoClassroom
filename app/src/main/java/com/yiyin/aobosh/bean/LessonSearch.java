package com.yiyin.aobosh.bean;

/**
 * Created by ALIY on 2018/12/18 0018.
 */

public class LessonSearch {


    /**
     * id : 153
     * uniacid : 4
     * pid : 68
     * cid : 102
     * bookname : 拜厄钢琴基本教程
     * price : 29.90
     * isdiscount : 0
     * vipdiscount : 0
     * integral : 0
     * images : http://www.a8du.com/attachment/images/4/2018/04/nB87rqe5YPnbPh8FeYZYkEynQnB8BR.jpg
     * poster :
     * count :
     */

    private int id;             // 课程id，章节的parentid
    private int uniacid;
    private int pid;
    private int cid;
    private String bookname;    // 课程名称
    private String price;       // 价格
    private int isdiscount;
    private int vipdiscount;
    private int integral;
    private String images;
    private int displayorder;   //课程排序
    private int virtual_buynum; //虚拟购买人数
    private int buynum;         //购买人数
    private int visit_number;   //访问人数
    private String poster;
    private String count;       // 课程数量

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUniacid() {
        return uniacid;
    }

    public void setUniacid(int uniacid) {
        this.uniacid = uniacid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getIsdiscount() {
        return isdiscount;
    }

    public void setIsdiscount(int isdiscount) {
        this.isdiscount = isdiscount;
    }

    public int getVipdiscount() {
        return vipdiscount;
    }

    public void setVipdiscount(int vipdiscount) {
        this.vipdiscount = vipdiscount;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getDisplayorder() {
        return displayorder;
    }

    public void setDisplayorder(int displayorder) {
        this.displayorder = displayorder;
    }

    public int getVirtual_buynum() {
        return virtual_buynum;
    }

    public void setVirtual_buynum(int virtual_buynum) {
        this.virtual_buynum = virtual_buynum;
    }

    public int getBuynum() {
        return buynum;
    }

    public void setBuynum(int buynum) {
        this.buynum = buynum;
    }

    public int getVisit_number() {
        return visit_number;
    }

    public void setVisit_number(int visit_number) {
        this.visit_number = visit_number;
    }
}
