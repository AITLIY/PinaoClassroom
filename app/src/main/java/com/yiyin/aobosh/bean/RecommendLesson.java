package com.yiyin.aobosh.bean;

import java.util.List;

/**
 * 推荐课程
 * Created by ALIY on 2018/12/10 0010.
 */

public class RecommendLesson {


    /**
     * id :
     * uniacid : 4
     * rec_name : 免费课程 推荐板块名称
     * show_style : 3 显示样式 1.单课程模式 2.课程+专题模式 3.专题模式
     * displayorder : 100
     * is_show : 1
     * addtime : 1511236315
     * lesson : [{"id":143,"uniacid":4,"pid":51,"cid":62,"bookname":"钢琴天天练 第一册","price":"0.00","isdiscount":0,"vipdiscount":0,"integral":0,"images":"http://www.a8du.com/attachment/images/4/2018/03/ivzOH59hH7kl912fVZBev75e1nmVB9.jpg","displayorder":123,"virtual_buynum":123," buynum":123,"visit_number":123,"count":123}]
     */

    private int id;             //课程id，章节的parentid
    private int uniacid;
    private String rec_name;    //推荐板块名称
    private int show_style;     //显示样式 1.单课程模式 2.课程+专题模式 3.专题模式
    private int displayorder;   //推荐板块优先级，数字越大排序越靠前
    private int is_show;        //是否显示
    private int addtime;
    private java.util.List<LessonBean> lesson;

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

    public String getRec_name() {
        return rec_name;
    }

    public void setRec_name(String rec_name) {
        this.rec_name = rec_name;
    }

    public int getShow_style() {
        return show_style;
    }

    public void setShow_style(int show_style) {
        this.show_style = show_style;
    }

    public int getDisplayorder() {
        return displayorder;
    }

    public void setDisplayorder(int displayorder) {
        this.displayorder = displayorder;
    }

    public int getIs_show() {
        return is_show;
    }

    public void setIs_show(int is_show) {
        this.is_show = is_show;
    }

    public int getAddtime() {
        return addtime;
    }

    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }

    public List<LessonBean> getLesson() {
        return lesson;
    }

    public void setLesson(List<LessonBean> lesson) {
        this.lesson = lesson;
    }

    public static class LessonBean {

        private int id;
        private int uniacid;
        private int pid;
        private int cid;
        private String bookname;  //课程名称
        private String price;     //课程价格
        private int isdiscount;
        private int vipdiscount;
        private int integral;
        private String images;      //课程封面
        private int displayorder;   //课程排序
        private int virtual_buynum; //虚拟购买人数
        private int buynum;         //购买人数
        private int visit_number;   //访问人数
        private int count;          //已更新课程

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

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
