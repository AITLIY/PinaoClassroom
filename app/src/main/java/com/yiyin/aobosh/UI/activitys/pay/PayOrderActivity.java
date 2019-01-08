package com.yiyin.aobosh.UI.activitys.pay;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.githang.statusbar.StatusBarCompat;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.bean.UserInfo;
import com.yiyin.aobosh.utils.ToastUtil;

public class PayOrderActivity extends Activity {

    private Context mContext;
    private RequestQueue requestQueue;
    private UserInfo mUserInfo;

    private LinearLayout pay_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_order);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        init();
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pay_ll = findViewById(R.id.pay_ll);

        pay_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ToastUtil.show(mContext,"支付失败");
            }
        });
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserInfo = GlobalParameterApplication.getInstance().getUserInfo();

//        Intent intent = getIntent();
//        if (intent != null) {
//            lessonID = intent.getIntExtra("lessonid",0);
//            LogUtils.i("PlaceOrderActivity: PlaceOrderActivity " + lessonID);
//        }
//        getFindOrder(mUserInfo.getUid(), lessonID);

    }
}
