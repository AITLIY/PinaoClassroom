package com.yiyin.aobosh.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/12/25.
 */

public class VipShow {

    private List<LevelListBean> level_list;             //  会员等级列表
    private List<MemberVipListBean> memberVip_list;     //  用户的会员等级

    public List<LevelListBean> getLevel_list() {
        return level_list;
    }

    public void setLevel_list(List<LevelListBean> level_list) {
        this.level_list = level_list;
    }

    public List<MemberVipListBean> getMemberVip_list() {
        return memberVip_list;
    }

    public void setMemberVip_list(List<MemberVipListBean> memberVip_list) {
        this.memberVip_list = memberVip_list;
    }

    public static class LevelListBean {
        /**
         * id : 1
         * uniacid : 4
         * level_name : 包年会员
         * level_validity : 365
         * level_price : 598.00
         * discount : 100
         * sort : 1
         * is_show : 1
         * addtime : 1507884788
         * integral : 0
         * renew : 1
         */

        private int id;
        private int uniacid;            // 公众号id
        private String level_name;      // 会员名称
        private int level_validity;     // 会员期限
        private String level_price;     // 会员价格
        private int discount;           // 购买折扣 0表示没有折扣
        private int sort;               // 排序
        private int is_show;            //  显示状态
        private int addtime;            // 添加时间
        private int integral;           // 赠送积分
        private int renew;

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

        public String getLevel_name() {
            return level_name;
        }

        public void setLevel_name(String level_name) {
            this.level_name = level_name;
        }

        public int getLevel_validity() {
            return level_validity;
        }

        public void setLevel_validity(int level_validity) {
            this.level_validity = level_validity;
        }

        public String getLevel_price() {
            return level_price;
        }

        public void setLevel_price(String level_price) {
            this.level_price = level_price;
        }

        public int getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
            this.discount = discount;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
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

        public int getIntegral() {
            return integral;
        }

        public void setIntegral(int integral) {
            this.integral = integral;
        }

        public int getRenew() {
            return renew;
        }

        public void setRenew(int renew) {
            this.renew = renew;
        }
    }

    public static class MemberVipListBean {
        /**
         * id : 274
         * uniacid : 4
         * uid : 1520
         * level_id : 2
         * validity : 2019-07-04 15:30
         * discount : 0
         * addtime : 1536305433
         * update_time : 1536541533
         * gifttime : 1538970008
         * level : {"id":2,"uniacid":4,"level_name":"包月会员","level_validity":30,"level_price":"50.00","discount":0,"sort":0,"is_show":1,"addtime":1527907739,"integral":0}
         */

        private int id;
        private int uniacid;
        private int uid;            // 用户uid
        private int level_id;       // vip等级id
        private String validity;    // 有效期
        private int discount;       // 是否有折扣
        private int addtime;        // 添加时间
        private int update_time;    // 更新时间
        private int gifttime;
        private LevelBean level;

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

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getLevel_id() {
            return level_id;
        }

        public void setLevel_id(int level_id) {
            this.level_id = level_id;
        }

        public String getValidity() {
            return validity;
        }

        public void setValidity(String validity) {
            this.validity = validity;
        }

        public int getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
            this.discount = discount;
        }

        public int getAddtime() {
            return addtime;
        }

        public void setAddtime(int addtime) {
            this.addtime = addtime;
        }

        public int getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(int update_time) {
            this.update_time = update_time;
        }

        public int getGifttime() {
            return gifttime;
        }

        public void setGifttime(int gifttime) {
            this.gifttime = gifttime;
        }

        public LevelBean getLevel() {
            return level;
        }

        public void setLevel(LevelBean level) {
            this.level = level;
        }

        public static class LevelBean {
            /**
             * id : 2
             * uniacid : 4
             * level_name : 包月会员
             * level_validity : 30
             * level_price : 50.00
             * discount : 0
             * sort : 0
             * is_show : 1
             * addtime : 1527907739
             * integral : 0
             */

            private int id;
            private int uniacid;
            private String level_name;
            private int level_validity;
            private String level_price;
            private int discount;
            private int sort;
            private int is_show;
            private int addtime;
            private int integral;

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

            public String getLevel_name() {
                return level_name;
            }

            public void setLevel_name(String level_name) {
                this.level_name = level_name;
            }

            public int getLevel_validity() {
                return level_validity;
            }

            public void setLevel_validity(int level_validity) {
                this.level_validity = level_validity;
            }

            public String getLevel_price() {
                return level_price;
            }

            public void setLevel_price(String level_price) {
                this.level_price = level_price;
            }

            public int getDiscount() {
                return discount;
            }

            public void setDiscount(int discount) {
                this.discount = discount;
            }

            public int getSort() {
                return sort;
            }

            public void setSort(int sort) {
                this.sort = sort;
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

            public int getIntegral() {
                return integral;
            }

            public void setIntegral(int integral) {
                this.integral = integral;
            }
        }
    }
}
