package com.yiyin.aobosh.activitys.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.yiyin.aobosh.R;

public class ForgetPasswordActivity1 extends Activity implements View.OnClickListener {
   
    private Context mContext;
    private EditText phone_ed,captcha_ed;
    private Button login_get_captcha;
    private LinearLayout forget_password_next;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password1);

        mContext = this;
        init();
    }

    private void init() {

        initView();
        initListner();
    }


    private void initView() {

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        phone_ed =  (EditText) findViewById(R.id.phone_ed);
        captcha_ed =  (EditText) findViewById(R.id.captcha_ed);
        login_get_captcha =  (Button) findViewById(R.id.login_get_captcha);
        forget_password_next =  (LinearLayout) findViewById(R.id.forget_password_next);


    }

    private void initListner() {

        login_get_captcha.setOnClickListener(this);
        forget_password_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.forget_password:

                break;

            case R.id.forget_password_next:

                startActivity(new Intent(mContext, ForgetPasswordActivity2.class));
                break;

        }
    }
}
