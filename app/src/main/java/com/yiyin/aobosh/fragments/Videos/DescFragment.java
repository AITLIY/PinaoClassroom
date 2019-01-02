package com.yiyin.aobosh.fragments.Videos;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.activitys.yiYinClassroom.TeacherActivity;
import com.yiyin.aobosh.bean.UserInfo;

/**
 * A simple {@link Fragment} subclass.
 */
public class DescFragment extends Fragment {

    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;
    private UserInfo mUserInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_desc, container, false);
        mContext = getContext();

        init();
        return mView;
    }

    private void init() {

        mView.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(mContext, TeacherActivity.class));
            }
        });
    }

}
