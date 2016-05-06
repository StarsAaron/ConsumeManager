package com.rujian.consumemanager.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.rujian.consumemanager.R;
import com.rujian.consumemanager.view.GestureLockView;

public class ResetGestureLockActivity extends Activity {

    private GestureLockView reset_GestureLockView;
    private TextView tv_reset_title;
    private Animation animation;
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;
    private String gesturePassword = "";//手势密码
    private int setCount = 0;//设置次数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetgesturelock);
        
        reset_GestureLockView = (GestureLockView) findViewById(R.id.reset_GestureLockView);
        tv_reset_title = (TextView) findViewById(R.id.tv_reset_title);

        //文字动画
        animation = new TranslateAnimation(-10, 10, 0, 0);
        animation.setDuration(50);
        animation.setRepeatCount(10);
        animation.setRepeatMode(Animation.REVERSE);

        sharedPreferences = getSharedPreferences("gesturelock", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        reset_GestureLockView.setOnGestureFinishListener(new GestureLockView.OnGestureFinishListener() {
            @Override
            public void OnGestureFinish(String key) {
                    setCount++;
                    if (1 == setCount) {
                        gesturePassword = key;
                        reset_GestureLockView.setResult(true);
                        tv_reset_title.setTextColor(Color.parseColor("#ffffff"));
                        tv_reset_title.setText("再次输入手势密码：");
                    }
                    if (2 == setCount) {
                        checkPassword(key);
                    }
            }
        });
    }

    /**
     * 检查密码
     * @param pass
     */
    private void checkPassword(String pass){
        if (gesturePassword.equals(pass)) {
            tv_reset_title.setTextColor(Color.BLUE);
            reset_GestureLockView.setResult(true);
            tv_reset_title.setText("设置成功!");
            setCount = 0;
            if(editor!=null){
                editor.putString("password",gesturePassword);
                editor.commit();
            }
            finish();
        }else{
            reset_GestureLockView.setResult(false);
            tv_reset_title.setTextColor(Color.RED);
            tv_reset_title.setText("密码不一致！");
            tv_reset_title.startAnimation(animation);
            setCount = 0;//清零，重新设置
        }
    }
}
