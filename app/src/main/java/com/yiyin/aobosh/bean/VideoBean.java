package com.yiyin.aobosh.bean;

import java.util.List;

/**
 * Created by ALIY on 2018/12/30 0030.
 */

public class VideoBean {

    /**
     * list : [{"id":682,"uniacid":4,"parentid":143,"title":"第一组·走路","savetype":0,"sectiontype":1,"videourl":"http://yykt2.aobosh.cn/å¤©å¤©ç»\u0083/ç¬¬ä¸\u0080å\u0086\u008cï¼\u0088æ\u0095\u0099å­¦ï¼\u00891ç¬¬ä¸\u0080ç»\u0084Â·èµ°è·¯ï¼\u0088æ\u0095\u0099å­¦ï¼\u0089.mp4","videotime":"","content":"","displayorder":200,"is_free":1,"status":1,"addtime":1516082725,"auto_show":0,"show_time":0,"test_time":0,"suffix":2,"qupu":null,"qupu_price":null}]
     * poster : http://www.a8du.com/attachment/images/4/2018/03/e9ar9P8V9E9VTHl0a90HNOLfhHuwZP.jpg
     * total : 2
     */

    private String poster;           // 视频播放封面图
    private int total;               // 评价数量
    private List<ListBean> list;
    private int iscollect;

    public int getIscollect() {
        return iscollect;
    }

    public void setIscollect(int iscollect) {
        this.iscollect = iscollect;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 682
         * uniacid : 4
         * parentid : 143
         * title : 第一组·走路
         * savetype : 0
         * sectiontype : 1
         * videourl : http://yykt2.aobosh.cn/å¤©å¤©ç»/ç¬¬ä¸åï¼æå­¦ï¼1ç¬¬ä¸ç»Â·èµ°è·¯ï¼æå­¦ï¼.mp4
         * videotime :
         * content :
         * displayorder : 200
         * is_free : 1
         * status : 1
         * addtime : 1516082725
         * auto_show : 0
         * show_time : 0
         * test_time : 0
         * suffix : 2
         * qupu : null
         * qupu_price : null
         */

        private int id;
        private int uniacid;
        private int parentid;
        private String title;
        private int savetype;
        private int sectiontype;
        private String videourl;
        private String videotime;
        private String content;
        private int displayorder;
        private int is_free;
        private int status;
        private int addtime;
        private int auto_show;
        private int show_time;
        private int test_time;
        private int suffix;
        private Object qupu;
        private Object qupu_price;

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

        public int getParentid() {
            return parentid;
        }

        public void setParentid(int parentid) {
            this.parentid = parentid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getSavetype() {
            return savetype;
        }

        public void setSavetype(int savetype) {
            this.savetype = savetype;
        }

        public int getSectiontype() {
            return sectiontype;
        }

        public void setSectiontype(int sectiontype) {
            this.sectiontype = sectiontype;
        }

        public String getVideourl() {
            return videourl;
        }

        public void setVideourl(String videourl) {
            this.videourl = videourl;
        }

        public String getVideotime() {
            return videotime;
        }

        public void setVideotime(String videotime) {
            this.videotime = videotime;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getDisplayorder() {
            return displayorder;
        }

        public void setDisplayorder(int displayorder) {
            this.displayorder = displayorder;
        }

        public int getIs_free() {
            return is_free;
        }

        public void setIs_free(int is_free) {
            this.is_free = is_free;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getAddtime() {
            return addtime;
        }

        public void setAddtime(int addtime) {
            this.addtime = addtime;
        }

        public int getAuto_show() {
            return auto_show;
        }

        public void setAuto_show(int auto_show) {
            this.auto_show = auto_show;
        }

        public int getShow_time() {
            return show_time;
        }

        public void setShow_time(int show_time) {
            this.show_time = show_time;
        }

        public int getTest_time() {
            return test_time;
        }

        public void setTest_time(int test_time) {
            this.test_time = test_time;
        }

        public int getSuffix() {
            return suffix;
        }

        public void setSuffix(int suffix) {
            this.suffix = suffix;
        }

        public Object getQupu() {
            return qupu;
        }

        public void setQupu(Object qupu) {
            this.qupu = qupu;
        }

        public Object getQupu_price() {
            return qupu_price;
        }

        public void setQupu_price(Object qupu_price) {
            this.qupu_price = qupu_price;
        }
    }
}
