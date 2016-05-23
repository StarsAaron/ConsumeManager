package com.rujian.consumemanager.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.rujian.consumemanager.R;
import com.rujian.consumemanager.view.GestureLockView;

public class GestureLockActivity extends Activity {

    private GestureLockView gv;
    private TextView textView;
    private Animation animation;
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;
    private String sharedPreferencesPassword = "";//SharedPreferences中的密码
    private String gesturePassword = "";//手势密码
    private boolean isSettingPasswordModel = false;//设置模式
    private int setCount = 0;//设置次数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesturelock);


        gv = (GestureLockView) findViewById(R.id.gv);
        textView = (TextView) findViewById(R.id.textview);

        animation = new TranslateAnimation(-10, 10, 0, 0);
        animation.setDuration(50);
        animation.setRepeatCount(10);
        animation.setRepeatMode(Animation.REVERSE);

        sharedPreferences = getSharedPreferences("gesturelock", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        gv.setOnGestureFinishListener(new GestureLockView.OnGestureFinishListener() {
            @Override
            public void OnGestureFinish(String key) {
                if (isSettingPasswordModel) {
                    setCount++;
                    if (1 == setCount) {
                        gesturePassword = key;
                        Log.e("key", key);
                        gv.setResult(true);
                        textView.setTextColor(Color.parseColor("#ffffff"));
                        textView.setText("再次输入手势密码：");
                    }
                    if (2 == setCount) {
                        checkPassword(key);
                    }
                } else {
                    checkPassword(key);
                }
            }
        });
        if (checkRecord()) {//检查有没有设置手势密码
            textView.setTextColor(Color.parseColor("#ffffff"));
            textView.setText("请输入手势密码：");
        } else {
            textView.setTextColor(Color.parseColor("#ffffff"));
            textView.setText("设置手势密码：");
        }
    }

    /**
     * 检查密码
     * @param pass
     */
    private void checkPassword(String pass){
        if (gesturePassword.equals(pass)) {
            textView.setTextColor(Color.BLUE);
            if(isSettingPasswordModel) {
                gv.setResult(true);
                textView.setText("设置成功!");
            }else{
                gv.setResult(true);
                textView.setText("密码正确!");
            }
            setCount = 0;
            if(editor!=null){
                editor.putString("password",gesturePassword);
                editor.commit();
            }
            Intent intent = new Intent(GestureLockActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            gv.setResult(false);
            textView.setTextColor(Color.RED);
            if(isSettingPasswordModel) {
                textView.setText("密码不一致!");
            }else{
                textView.setText("密码错误!");
            }
            textView.startAnimation(animation);
            setCount = 0;//清零，重新设置
        }
    }

    /**
     * 检查是否有记录
     * @return
     */
    private boolean checkRecord() {
        sharedPreferencesPassword = sharedPreferences.getString("password", "");
        if (TextUtils.isEmpty(sharedPreferencesPassword)) {
            isSettingPasswordModel = true;
            return false;
        } else {
            gesturePassword = sharedPreferencesPassword;
            isSettingPasswordModel = false;
            return true;
        }
    }
}
