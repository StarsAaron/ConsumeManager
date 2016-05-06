package com.rujian.consumemanager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rujian.consumemanager.Myapplication;
import com.rujian.consumemanager.R;

/**
 * Created by zhrjian on 2015/11/16.
 */
public class BaseHeadActivity extends Activity {
    private RelativeLayout rl_container,rl_titlebar;
    private ImageView iv_return,iv_menu;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_basehead);

        Myapplication myapplication = (Myapplication)getApplication();
        myapplication.addActivity(this);

        iv_return = (ImageView)findViewById(R.id.iv_return);
        iv_menu = (ImageView)findViewById(R.id.iv_menu);
        tv_title = (TextView)findViewById(R.id.tv_title);
        rl_container = (RelativeLayout)findViewById(R.id.rl_container);
        rl_titlebar = (RelativeLayout)findViewById(R.id.rl_titlebar);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseHeadActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void setContentView(int layoutResID) {
        View v = getLayoutInflater().inflate(layoutResID,rl_container,false);
        setContentView(v);
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, view.getLayoutParams());
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        rl_container.addView(view, params);
    }

    //显示标题栏
    protected void showTitleBar(){
        rl_titlebar.setVisibility(View.VISIBLE);
    }

    //隐藏标题栏
    protected void hideTitleBar(){
        rl_titlebar.setVisibility(View.GONE);
    }

    //设置标题
    protected void setTitle(String title){
        tv_title.setText(title);
    }
    //隐藏左按钮
    protected void hideBackButton(){
        iv_return.setVisibility(View.GONE);
    }
    //显示左按钮
    protected void showBackButton(){
        iv_return.setVisibility(View.VISIBLE);
    }
    //添加左按钮点击事件
    protected void setLeftImageViewOnClickListener(View.OnClickListener listener){
        iv_return.setOnClickListener(listener);
    }

    //设置标题字体颜色
    protected void setTitleTextColor(int color){
        tv_title.setTextColor(color);
    }

    //设置标题栏颜色
    protected void setTitleBarColor(int color){
        rl_titlebar.setBackgroundColor(color);
    }

    //显示右边ImageView
    protected void showRightImageView(){
        iv_menu.setVisibility(View.VISIBLE);
    }
    //隐藏右按钮
    protected void hideRightImageView(){
        iv_menu.setVisibility(View.GONE);
    }

    //设置右边ImageView点击监听
    protected void setRightImageViewOnClickListener(View.OnClickListener listener){
        iv_menu.setOnClickListener(listener);
    }
}
