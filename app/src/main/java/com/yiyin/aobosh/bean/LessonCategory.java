package com.yiyin.aobosh.bean;

import java.util.List;

/**
 * 课程分类
 * Created by ALIY on 2018/12/10 0010.
 */

public class LessonCategory {

    /**
     * id : 155
     * uniacid : 4
     * name : 南京艺术学院钢琴考级曲集
     * parentid : 0
     * ico : http://www.a8du.com/attachment/images/4/2018/11/l6dyNK20hy4X4r844DN2440X3Xd3hH.png
     * link :
     * displayorder : 21
     * is_show : 1
     * addtime : 1543196473
     * sonlist : [{"id":156,"uniacid":4,"name":"第一级","parentid":155,"ico":"","link":"","displayorder":10,"is_show":1,"addtime":1543223072}]
     */

    private int id;             //cate_id
    private int uniacid;
    private String name;        //二级分类名称
    private int parentid;
    private String ico;         //分类图标
    private int ico2;         //分类图标
    private String link;
    private int displayorder;   //数字越大排序越靠前
    private int is_show;        //是否显示
    private int addtime;
    private List<SonlistBean> sonlist;

    public int getIco2() {
        return ico2;
    }

    public void setIco2(int ico2) {
        this.ico2 = ico2;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public List<SonlistBean> getSonlist() {
        return sonlist;
    }

    public void setSonlist(List<SonlistBean> sonlist) {
        this.sonlist = sonlist;
    }

    public static class SonlistBean {
        /**
         * id : 156
         * uniacid : 4
         * name : 第一级
         * parentid : 155
         * ico :
         * link :
         * displayorder : 10
         * is_show : 1
         * addtime : 1543223072
         */

        private int id;
        private int uniacid;
        private String name;
        private int parentid;
        private String ico;
        private String link;
        private int displayorder;
        private int is_show;
        private int addtime;
        private int type;
        private int position;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getParentid() {
            return parentid;
        }

        public void setParentid(int parentid) {
            this.parentid = parentid;
        }

        public String getIco() {
            return ico;
        }

        public void setIco(String ico) {
            this.ico = ico;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
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
    }
}
