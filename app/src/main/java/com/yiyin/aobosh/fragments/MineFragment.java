package com.yiyin.aobosh.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.activitys.HomepageActivity;
import com.yiyin.aobosh.activitys.login.LoginActivity;
import com.yiyin.aobosh.activitys.mine.ChangeMobileActivity;
import com.yiyin.aobosh.activitys.mine.ChangePasswordActivity;
import com.yiyin.aobosh.activitys.mine.CollectLessonActivity;
import com.yiyin.aobosh.activitys.mine.CollectTeacherActivity;
import com.yiyin.aobosh.activitys.mine.OauthHistoryActivity;
import com.yiyin.aobosh.activitys.mine.VipCardActivity;
import com.yiyin.aobosh.activitys.mine.VipServiceActivity;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.utils.Sputils;
import com.yiyin.aobosh.view.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private Context mContext;
    private UserInfo mUserInfo;

    private CircleImageView userIcon;
    private LinearLayout vip_ll,cardpw_ll,footprint_ll,star_lesson_ll,
            star_teacher_ll,discounts_ll,phone_ll,password_ll,exit_ll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mine, container, false);

        mContext = getContext();
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();

        LogUtils.i("MineFragment: mobile " + mUserInfo.getMobile() + ", uid " + mUserInfo.getUid());
        init();
        return mView;
    }

    private void init() {

        initView();
        initListner();
    }

    private void initView() {

        userIcon = mView.findViewById(R.id.user_icon);
        vip_ll = mView.findViewById(R.id.vip_ll);
        cardpw_ll = mView.findViewById(R.id.cardpw_ll);
        footprint_ll = mView.findViewById(R.id.footprint_ll);
        star_lesson_ll = mView.findViewById(R.id.star_lesson_ll);
        star_teacher_ll = mView.findViewById(R.id.star_teacher_ll);
        discounts_ll = mView.findViewById(R.id.discounts_ll);
        phone_ll = mView.findViewById(R.id.phone_ll);
        password_ll = mView.findViewById(R.id.password_ll);
        exit_ll = mView.findViewById(R.id.exit_ll);
    }

    private void initListner() {

        userIcon.setOnClickListener(this);
        vip_ll.setOnClickListener(this);
        cardpw_ll.setOnClickListener(this);
        footprint_ll.setOnClickListener(this);
        star_lesson_ll.setOnClickListener(this);
        star_teacher_ll.setOnClickListener(this);
        discounts_ll.setOnClickListener(this);
        phone_ll.setOnClickListener(this);
        password_ll.setOnClickListener(this);
        exit_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.user_icon:

                if (GlobalParameterApplication.getInstance().getLoginStatus()) {
                    //TODO

                } else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
                break;

            case R.id.vip_ll:

                startActivity(new Intent(mContext, VipServiceActivity.class));
                break;

            case R.id.cardpw_ll:

                startActivity(new Intent(mContext, VipCardActivity.class));
                break;

            case R.id.footprint_ll:

                startActivity(new Intent(mContext, OauthHistoryActivity.class));
                break;

            case R.id.star_lesson_ll:

                startActivity(new Intent(mContext, CollectLessonActivity.class));
                break;

            case R.id.star_teacher_ll:

                startActivity(new Intent(mContext, CollectTeacherActivity.class));
                break;

            case R.id.discounts_ll:

                break;

            case R.id.phone_ll:

                startActivity(new Intent(mContext, ChangeMobileActivity.class));
                break;

            case R.id.password_ll:

                startActivity(new Intent(mContext, ChangePasswordActivity.class));
                break;

            case R.id.exit_ll:

                GlobalParameterApplication.getInstance().setLoginStatus(false);
                GlobalParameterApplication.getInstance().clearUserInfo();   // 清空本地存储的用户信息
                ((HomepageActivity) getActivity()).BackToTheHomepage();     // 回到主页
                break;

        }
    }
}
