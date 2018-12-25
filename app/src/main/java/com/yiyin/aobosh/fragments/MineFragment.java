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

        LogUtils.i("ChangeMobileActivity: mobile " + mUserInfo.getMobile() + "uid" + mUserInfo.getUid());
        init();
        return mView;
    }

    private void init() {

        initView();
        initListner();
    }

    private void initView() {

        userIcon =  (CircleImageView) mView.findViewById(R.id.user_icon);
        vip_ll =  (LinearLayout) mView.findViewById(R.id.vip_ll);
        cardpw_ll =  (LinearLayout) mView.findViewById(R.id.cardpw_ll);
        footprint_ll =  (LinearLayout) mView.findViewById(R.id.footprint_ll);
        star_lesson_ll =  (LinearLayout) mView.findViewById(R.id.star_lesson_ll);
        star_teacher_ll =  (LinearLayout) mView.findViewById(R.id.star_teacher_ll);
        discounts_ll =  (LinearLayout) mView.findViewById(R.id.discounts_ll);
        phone_ll =  (LinearLayout) mView.findViewById(R.id.phone_ll);
        password_ll =  (LinearLayout) mView.findViewById(R.id.password_ll);
        exit_ll =  (LinearLayout) mView.findViewById(R.id.exit_ll);
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

                if (mUserInfo != null) {
                    //TODO

                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;

            case R.id.vip_ll:

                startActivity(new Intent(getActivity(), VipServiceActivity.class));
                break;

            case R.id.cardpw_ll:

                break;

            case R.id.footprint_ll:

                break;

            case R.id.star_lesson_ll:

                break;

            case R.id.star_teacher_ll:

                break;

            case R.id.discounts_ll:

                break;

            case R.id.phone_ll:

                startActivity(new Intent(getActivity(), ChangeMobileActivity.class));
                break;

            case R.id.password_ll:

                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                break;

            case R.id.exit_ll:

                Sputils.clear(mContext);    // 清空本地存储
                ((HomepageActivity) getActivity()).BackToTheHomepage(); //回到主页
                break;

        }
    }
}
