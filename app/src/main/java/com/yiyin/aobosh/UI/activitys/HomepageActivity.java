package com.yiyin.aobosh.UI.activitys;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.lidroid.xutils.util.LogUtils;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.UI.activitys.login.LoginActivity;
import com.yiyin.aobosh.application.GlobalParameterApplication;
import com.yiyin.aobosh.UI.fragments.homes.AllClassFragment;
import com.yiyin.aobosh.UI.fragments.homes.HomeFragment;
import com.yiyin.aobosh.UI.fragments.homes.MineFragment;
import com.yiyin.aobosh.UI.fragments.homes.MyLessonFragment;
import com.yiyin.aobosh.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomepageActivity extends AppCompatActivity{

    private LinearLayout ll_homepage,ll_all_class,ll_my_class,ll_mine;

    private HomeFragment mHomeFragment;
    private AllClassFragment mAllClassFragment;
    private MyLessonFragment mMyLessonFragment;
    private MineFragment mMineFragment;
    private List<Fragment> mFragments;

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

        ll_homepage = findViewById(R.id.ll_homepage);
        ll_all_class = findViewById(R.id.ll_all_class);
        ll_my_class = findViewById(R.id.ll_my_class);
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
        mAllClassFragment = new AllClassFragment();
        mMyLessonFragment = new MyLessonFragment();
        mMineFragment = new MineFragment();

        mFragments = new ArrayList<>();
        currentFragment = mHomeFragment;
        changeFragment(mHomeFragment);//默认加载
    }

    private int mTabItemId;  //当前选中按钮的id
    private Fragment currentFragment;  //当前选中按钮的id
    class BootombarListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            if (mTabItemId == view.getId()) {
                return;
            }

            switch (view.getId()) {
                case R.id.ll_homepage:
                    changeFragment(mHomeFragment);
                    break;
                case R.id.ll_all_class:
                    changeFragment(mAllClassFragment);
                    break;
                case R.id.ll_my_class:

                    if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                        startActivity(new Intent(HomepageActivity.this, LoginActivity.class));
                        return;
                    } else {
                        changeFragment(mMyLessonFragment);
                    }
                    break;
                case R.id.ll_mine:

                    if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                        startActivity(new Intent(HomepageActivity.this, LoginActivity.class));
                        return;
                    } else {
                        changeFragment(mMineFragment);
                    }
                    break;
            }
            changeTabItemStyle(view);
        }
    }

    public void changeFragment(Fragment fragment){

        FragmentManager manager = getSupportFragmentManager();

        if (!mFragments.contains(fragment)) {
            mFragments.add(fragment);
            manager.beginTransaction()
                    .add(R.id.content_fl, fragment)
                    .commit();
            LogUtils.i("HomepageActivity: mFragments.add fragment ");
        }

        manager.beginTransaction()
                .hide(currentFragment)
                .show(fragment)
                .commit();

        currentFragment = fragment;
    }

    // 改变Fragment
    public void replaceContentPage(Fragment fragment) {

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.content_fl, fragment)
                .addToBackStack(null)
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
        changeFragment(mHomeFragment);
        changeTabItemStyle(ll_homepage);
    }

    // 去我的课程
    public void goToMyLesson (){
        mTabItemId = ll_my_class.getId();
        changeFragment(mMyLessonFragment);
        changeTabItemStyle(ll_my_class);
    }

    private boolean isExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode==KeyEvent.KEYCODE_BACK) {
            Timer tExit;
            if (!isExit) {
                isExit = true;
                ToastUtil.show(this, "再次点击返回按钮退出");
                tExit = new Timer();
                tExit.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
