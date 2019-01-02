package com.yiyin.aobosh.bean;

/**
 * Created by Administrator on 2019/1/2.
 */

public class EvaluateBean {

    /**
     * lessonid : 152
     * bookname : 汤普森简易钢琴教程 第一册
     * nickname : MMT宋婷婷
     * grade : 1
     * content : 我要好好学到里面的内容。
     * reply : null
     * addtime : 1536973945
     * avatar : http://thirdwx.qlogo.cn/mmopen/MmW2XFibStNLnYNN5hicsPEpagaamPEnC93DNjsDq4PL178IV8m4vAg8yJqiaCoxjiaJBn3fozIcv6YZth34MhQibJMzUwAcEsulP/132
     */

    private int lessonid;
    private String bookname;
    private String nickname;
    private int grade;
    private String content;
    private Object reply;
    private int addtime;
    private String avatar;

    public int getLessonid() {
        return lessonid;
    }

    public void setLessonid(int lessonid) {
        this.lessonid = lessonid;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getReply() {
        return reply;
    }

    public void setReply(Object reply) {
        this.reply = reply;
    }

    public int getAddtime() {
        return addtime;
    }

    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
