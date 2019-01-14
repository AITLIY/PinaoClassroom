package com.yiyin.aobosh.UI.activitys.yiyinClassroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.yiyin.aobosh.R;
import com.yiyin.aobosh.application.GlobalParameterApplication;

public class TeacherWebActivity extends Activity {

    private WebView webView;
    private TextView teache_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_web);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent!=null) {
            String url =  intent.getStringExtra("url");
            init(url);
        }

    }


    private void init(String url){
        webView = findViewById(R.id.webView);
        teache_title = findViewById(R.id.teache_title);
        //WebView加载web资源
        webView.loadUrl(url);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                teache_title.setText(view.getTitle());
                return true;
            }
        });
    }
}
