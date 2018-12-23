package com.yiyin.aobosh.activitys.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.yiyin.aobosh.R;

public class ForgetPasswordActivity2 extends Activity  implements View.OnClickListener {

    private Context mContext;
    private EditText new_password1_ed,new_password2_ed;
    private LinearLayout forget_password_commit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password2);

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

        new_password1_ed =  (EditText) findViewById(R.id.new_password1_ed);
        new_password2_ed =  (EditText) findViewById(R.id.new_password2_ed);
        forget_password_commit =  (LinearLayout) findViewById(R.id.forget_password_commit);
    }

    private void initListner() {

        forget_password_commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.forget_password_commit:

                startActivity(new Intent(mContext, PasswordSuccessActivity.class));
                break;
        }
    }
}
