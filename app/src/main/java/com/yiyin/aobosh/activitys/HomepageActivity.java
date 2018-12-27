package com.yiyin.aobosh.activitys;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.activitys.login.LoginActivity;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.fragments.AllClassFragment;
import com.yiyin.aobosh.fragments.HomeFragment;
import com.yiyin.aobosh.fragments.MineFragment;
import com.yiyin.aobosh.fragments.MyClassFragment;

public class HomepageActivity extends AppCompatActivity{

    LinearLayout ll_homepage,ll_all_class,ll_my_class,ll_mine;
    ImageView iv_homepage,iv_all_class,iv_my_class,iv_mine;
    TextView tv_homepage,tv_all_class,tv_my_class,tv_mine;
    FrameLayout content_fl;
    HomeFragment mHomeFragment;
    AllClassFragment mAllClassFragment;
    MyClassFragment mMyClassFragment;
    MineFragment mMineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        // 隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.app_title_bar), true);

        init();
    }

    private void init() {
        initView();
        initFrament();
    }

    private void initView() {

        content_fl = findViewById(R.id.content_fl);

        tv_homepage = findViewById(R.id.tv_homepage);
        iv_homepage = findViewById(R.id.iv_homepage);
        ll_homepage = findViewById(R.id.ll_homepage);

        tv_all_class = findViewById(R.id.tv_all_class);
        iv_all_class = findViewById(R.id.iv_all_class);
        ll_all_class = findViewById(R.id.ll_all_class);

        tv_my_class = findViewById(R.id.tv_my_class);
        iv_my_class = findViewById(R.id.iv_my_class);
        ll_my_class = findViewById(R.id.ll_my_class);

        tv_mine = findViewById(R.id.tv_mine);
        iv_mine = findViewById(R.id.iv_mine);
        ll_mine = findViewById(R.id.ll_mine);

        BootombarListener listener = new BootombarListener();
        ll_homepage.setOnClickListener(listener);
        ll_my_class.setOnClickListener(listener);
        ll_all_class.setOnClickListener(listener);
        ll_mine.setOnClickListener(listener);
        mTabItemId = ll_homepage.getId();
        ll_homepage.setSelected(true);//默认选中

    }

    private void initFrament() {
        mHomeFragment = new HomeFragment();
        replaceContentPage(mHomeFragment);//默认加载
        mAllClassFragment = new AllClassFragment();
        mMyClassFragment = new MyClassFragment();
        mMineFragment = new MineFragment();
    }

    private int mTabItemId;  //当前选中按钮的id
    class BootombarListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            if (mTabItemId == view.getId()) {
                return;
            }

            switch (view.getId()) {
                case R.id.ll_homepage:
                    replaceContentPage(mHomeFragment);
                    break;
                case R.id.ll_all_class:
                    replaceContentPage(mAllClassFragment);
                    break;
                case R.id.ll_my_class:
                    replaceContentPage(mMyClassFragment);
                    break;
                case R.id.ll_mine:

                    if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                        startActivity(new Intent(HomepageActivity.this, LoginActivity.class));
                        return;
                    } else {
                        replaceContentPage(mMineFragment);
                    }
                    break;
            }

            changeTabItemStyle(view);
        }
    }

    // 改变Fragment
    public void replaceContentPage(Fragment fragment) {

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.content_fl, fragment)
                .commit();
    }

    // 根据是否被选中改变展示风格
    private void changeTabItemStyle(View view) {
        mTabItemId = view.getId();
        ll_homepage.setSelected(view.getId() == R.id.ll_homepage);
        ll_all_class.setSelected(view.getId() == R.id.ll_all_class);
        ll_my_class.setSelected(view.getId() == R.id.ll_my_class);
        ll_mine.setSelected(view.getId() == R.id.ll_mine);
    }

    // 回到主页
    public void BackToTheHomepage (){
        mTabItemId = ll_homepage.getId();
        replaceContentPage(mHomeFragment);
        changeTabItemStyle(ll_homepage);
    }

}
