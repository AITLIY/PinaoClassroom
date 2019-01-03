package com.yiyin.aobosh.bean;

/**
 * Created by ALIY on 2019/1/3 0003.
 */

public class LessonDetail {

    /**
     * id : 191
     * uniacid : 4
     * pid : 135
     * cid : 136
     * bookname : 孩子们的哈农
     * price : 9.90
     * isdiscount : 0
     * vipdiscount : 0
     * integral : 0
     * images : images/4/2018/05/Z252H1Lfx5Fl476IdF3l6l214f4567.jpg
     * poster : ["images\/4\/2018\/05\/E22a2uE9av61n2AYxryGuzgDygnV20.jpg","images\/4\/2018\/05\/Ubaq5BgLfD33clKl5GFKQCGOXnghzk.jpg","images\/4\/2018\/05\/k0E111Kn182xe721122NXkx11077x0.jpg"]
     * descript :
     * difficulty :
     * stock : 0
     * buynum : 3
     * virtual_buynum : 0
     * score : 1.00
     * teacherid : 2
     * commission : a:3:{s:11:"commission1";d:0;s:11:"commission2";d:0;s:11:"commission3";d:0;}
     * displayorder : 0
     * status : 1
     * recommendid :
     * vipview : ["1","2"]
     * teacher_income : 0
     * link : null
     * validity : 0
     * addtime : 1519884452
     * deduct_integral : 0
     * share : {"title":"","images":"","descript":""}
     * support_coupon : 1
     * integral_rate : 0.00
     * visit_number : 153
     * update_time : 2018-03-07 16:48:35
     * ico_name :
     * lesson_type : 0
     * appoint_info : null
     * qupu : null
     * rec_images :
     * rec_name :
     * rec_link :
     * teacher : 叶子
     * qq :
     * qqgroup :
     * qqgroupLink :
     * weixin_qrcode :
     * teacherphoto : images/4/2017/10/u2rvy0Cv2QZYyqO1vO0709Z2c9O2c1.jpg
     * teacherdes :
     */

    private int id;
    private int uniacid;
    private int pid;
    private int cid;
    private String bookname;        // 课程名称
    private String price;
    private int isdiscount;
    private int vipdiscount;
    private int integral;
    private String images;
    private String poster;
    private String descript;        // 课程介绍
    private String difficulty;      // 课程难度（入门篇、进阶篇、高级篇）
    private int stock;
    private int buynum;
    private int virtual_buynum;
    private String score;
    private int teacherid;
    private String commission;
    private int displayorder;
    private int status;
    private String recommendid;
    private String vipview;
    private int teacher_income;
    private Object link;
    private int validity;
    private int addtime;
    private int deduct_integral;
    private String share;
    private int support_coupon;
    private String integral_rate;
    private int visit_number;
    private String update_time;
    private String ico_name;
    private int lesson_type;
    private String appoint_info;
    private Object qupu;
    private String rec_images;
    private String rec_name;
    private String rec_link;
    private String teacher;
    private String qq;
    private String qqgroup;
    private String qqgroupLink;
    private String weixin_qrcode;
    private String teacherphoto;
    private String teacherdes;   // 教师简介

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

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getBuynum() {
        return buynum;
    }

    public void setBuynum(int buynum) {
        this.buynum = buynum;
    }

    public int getVirtual_buynum() {
        return virtual_buynum;
    }

    public void setVirtual_buynum(int virtual_buynum) {
        this.virtual_buynum = virtual_buynum;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getTeacherid() {
        return teacherid;
    }

    public void setTeacherid(int teacherid) {
        this.teacherid = teacherid;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public int getDisplayorder() {
        return displayorder;
    }

    public void setDisplayorder(int displayorder) {
        this.displayorder = displayorder;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRecommendid() {
        return recommendid;
    }

    public void setRecommendid(String recommendid) {
        this.recommendid = recommendid;
    }

    public String getVipview() {
        return vipview;
    }

    public void setVipview(String vipview) {
        this.vipview = vipview;
    }

    public int getTeacher_income() {
        return teacher_income;
    }

    public void setTeacher_income(int teacher_income) {
        this.teacher_income = teacher_income;
    }

    public Object getLink() {
        return link;
    }

    public void setLink(Object link) {
        this.link = link;
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public int getAddtime() {
        return addtime;
    }

    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }

    public int getDeduct_integral() {
        return deduct_integral;
    }

    public void setDeduct_integral(int deduct_integral) {
        this.deduct_integral = deduct_integral;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public int getSupport_coupon() {
        return support_coupon;
    }

    public void setSupport_coupon(int support_coupon) {
        this.support_coupon = support_coupon;
    }

    public String getIntegral_rate() {
        return integral_rate;
    }

    public void setIntegral_rate(String integral_rate) {
        this.integral_rate = integral_rate;
    }

    public int getVisit_number() {
        return visit_number;
    }

    public void setVisit_number(int visit_number) {
        this.visit_number = visit_number;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getIco_name() {
        return ico_name;
    }

    public void setIco_name(String ico_name) {
        this.ico_name = ico_name;
    }

    public int getLesson_type() {
        return lesson_type;
    }

    public void setLesson_type(int lesson_type) {
        this.lesson_type = lesson_type;
    }

    public String getAppoint_info() {
        return appoint_info;
    }

    public void setAppoint_info(String appoint_info) {
        this.appoint_info = appoint_info;
    }

    public Object getQupu() {
        return qupu;
    }

    public void setQupu(Object qupu) {
        this.qupu = qupu;
    }

    public String getRec_images() {
        return rec_images;
    }

    public void setRec_images(String rec_images) {
        this.rec_images = rec_images;
    }

    public String getRec_name() {
        return rec_name;
    }

    public void setRec_name(String rec_name) {
        this.rec_name = rec_name;
    }

    public String getRec_link() {
        return rec_link;
    }

    public void setRec_link(String rec_link) {
        this.rec_link = rec_link;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getQqgroup() {
        return qqgroup;
    }

    public void setQqgroup(String qqgroup) {
        this.qqgroup = qqgroup;
    }

    public String getQqgroupLink() {
        return qqgroupLink;
    }

    public void setQqgroupLink(String qqgroupLink) {
        this.qqgroupLink = qqgroupLink;
    }

    public String getWeixin_qrcode() {
        return weixin_qrcode;
    }

    public void setWeixin_qrcode(String weixin_qrcode) {
        this.weixin_qrcode = weixin_qrcode;
    }

    public String getTeacherphoto() {
        return teacherphoto;
    }

    public void setTeacherphoto(String teacherphoto) {
        this.teacherphoto = teacherphoto;
    }

    public String getTeacherdes() {
        return teacherdes;
    }

    public void setTeacherdes(String teacherdes) {
        this.teacherdes = teacherdes;
    }
}
