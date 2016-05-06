package com.rujian.consumemanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rujian.consumemanager.Myapplication;
import com.rujian.consumemanager.R;
import com.rujian.consumemanager.bean.RecordBean;
import com.rujian.consumemanager.db.dao.RecordDao;
import com.rujian.consumemanager.ui.fragment.CalendarFragment;
import com.rujian.consumemanager.util.RecordUtil;
import com.rujian.consumemanager.view.MyDialog;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by zhrjian on 2015/11/16.
 */
public class SaveRecordActivity extends BaseHeadActivity implements View.OnClickListener,CalendarFragment.ChooseDateListener {
    private Myapplication myapplication;
    private Button btn_calendar,btn_today,btn_save,btn_delete,btn_record_voice;
    private EditText et_time,et_money,et_description;
    private RadioButton rb_consume,rb_income;
    private RadioGroup rbg_choose;
    private LinearLayout ll_save_date;
    private CalendarFragment calendarFragment;
    private FrameLayout fl_savepage;
    private String chooseConsume = "";
    private boolean isFromEdit = false;//是否来自编辑页面
    private RecordBean recordBean = null;//来自编辑请求的RecordBean
    //---录音图标
    private FrameLayout fl_diaplay_voice_icon;
    private ImageView iv_voice_size;
    private TextView tv_record_time;//录音时间显示
    //录音播放条
    private ImageView iv_voiceplay_btn;//播放按钮
    private ProgressBar prb_voiceplay_bar;
    private TextView tv_voiceplay_time;
    private ImageView iv_delete_voice;
    private LinearLayout ll_voiceplay_bar;

    private String voice_save_path = "";//声音保存路径
    private String voive_directory = "";//声音保存目录
    private RecordUtil mRecordUtil = null;
    private static final int MIN_TIME = 2;// 最短录音时间
    private static final int RECORD_NO = 0; // 不在录音
    private static final int RECORD_ING = 1; // 正在录音
    private static final int RECORD_ED = 2; // 完成录音
    private int mRecord_State = 0; // 录音的状态
    private float mRecord_Time;// 录音的时间
    private double mRecord_Volume;// 麦克风获取的音量值
    private boolean mPlayState; // 播放状态
    private int mPlayCurrentPosition;// 当前播放的时间

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
//                    // 显示录音时间
//                    tv_recordtime.setText((int) mRecord_Time + "″");
//                    // 根据录音声音大小显示效果
//                    ViewGroup.LayoutParams params = voice_recording_volume.getLayoutParams();
//                    if (mRecord_Volume < 200.0) {
//                        params.height = mMINVolume;
//                    } else if (mRecord_Volume > 200.0 && mRecord_Volume < 400) {
//                        params.height = mMINVolume * 2;
//                    } else if (mRecord_Volume > 400.0 && mRecord_Volume < 800) {
//                        params.height = mMINVolume * 3;
//                    } else if (mRecord_Volume > 800.0 && mRecord_Volume < 1600) {
//                        params.height = mMINVolume * 4;
//                    } else if (mRecord_Volume > 1600.0 && mRecord_Volume < 3200) {
//                        params.height = mMINVolume * 5;
//                    } else if (mRecord_Volume > 3200.0 && mRecord_Volume < 5000) {
//                        params.height = mMINVolume * 6;
//                    } else if (mRecord_Volume > 5000.0 && mRecord_Volume < 7000) {
//                        params.height = mMINVolume * 7;
//                    } else if (mRecord_Volume > 7000.0 && mRecord_Volume < 10000.0) {
//                        params.height = mMINVolume * 8;
//                    } else if (mRecord_Volume > 10000.0 && mRecord_Volume < 14000.0) {
//                        params.height = mMINVolume * 9;
//                    } else if (mRecord_Volume > 14000.0 && mRecord_Volume < 17000.0) {
//                        params.height = mMINVolume * 10;
//                    } else if (mRecord_Volume > 17000.0 && mRecord_Volume < 20000.0) {
//                        params.height = mMINVolume * 11;
//                    } else if (mRecord_Volume > 20000.0 && mRecord_Volume < 24000.0) {
//                        params.height = mMINVolume * 12;
//                    } else if (mRecord_Volume > 24000.0 && mRecord_Volume < 28000.0) {
//                        params.height = mMINVolume * 13;
//                    } else if (mRecord_Volume > 28000.0) {
//                        params.height = mMAXVolume;
//                    }
//                    voice_recording_volume.setLayoutParams(params);
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_saverecord);
        myapplication = (Myapplication)getApplication();
        myapplication.addActivity(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        registView();
        initView();
        if(bundle != null){
            isFromEdit = true;
            recordBean = (RecordBean)bundle.getSerializable("RecordBean");
            initEditDate(recordBean);
            btn_delete.setVisibility(View.VISIBLE);
        }
    }

    //来自编辑请求，进行空间数据初始化
    private void initEditDate(RecordBean recordBean) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setGroupingUsed(false);//不加，分割
        et_time.setText(recordBean._date);
        et_money.setText(nf.format(recordBean._consume));
        et_description.setText(recordBean._description);
        if(recordBean._type.equals("支出")){
            rb_consume.setChecked(true);
        }else if(recordBean._type.equals("收入")){
            rb_income.setChecked(true);
        }
    }

    private void registView() {
        setTitle("财务记录");
        showBackButton();
        setLeftImageViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTitleBarColor(getResources().getColor(R.color.green));
        btn_calendar = (Button)findViewById(R.id.btn_calendar);
        btn_today = (Button)findViewById(R.id.btn_today);
        btn_save = (Button)findViewById(R.id.btn_save);
        btn_delete = (Button)findViewById(R.id.btn_delete);
        btn_record_voice = (Button)findViewById(R.id.btn_record_voice);
        et_time = (EditText)findViewById(R.id.et_time);
        et_money = (EditText)findViewById(R.id.et_money);
        et_description = (EditText)findViewById(R.id.et_description);
        rb_consume = (RadioButton)findViewById(R.id.rb_consume);
        rb_income = (RadioButton)findViewById(R.id.rb_income);
        rbg_choose = (RadioGroup)findViewById(R.id.rbg_choose);
        ll_save_date = (LinearLayout)findViewById(R.id.ll_save_date);
        fl_savepage = (FrameLayout)findViewById(R.id.fl_savepage);

        tv_record_time = (TextView)findViewById(R.id.tv_record_time);
        iv_voice_size = (ImageView) findViewById(R.id.iv_voice_size);
        fl_diaplay_voice_icon = (FrameLayout) findViewById(R.id.fl_diaplay_voice_icon);

        iv_voiceplay_btn = (ImageView)findViewById(R.id.iv_voiceplay_btn);
        prb_voiceplay_bar = (ProgressBar)findViewById(R.id.prb_voiceplay_bar);
        tv_voiceplay_time = (TextView)findViewById(R.id.tv_voiceplay_time);
        iv_delete_voice = (ImageView)findViewById(R.id.iv_delete_voice);
        ll_voiceplay_bar = (LinearLayout)findViewById(R.id.ll_voiceplay_bar);
    }

    private void initView() {
        btn_calendar.setOnClickListener(this);
        btn_today.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_delete.setOnClickListener(this);

        recordBean = new RecordBean();//放在前面

        //初始化选择状态
        int chooseID = rbg_choose.getCheckedRadioButtonId();
        if(chooseID == R.id.rb_consume){
            chooseConsume = "支出";
        }else{
            chooseConsume = "收入";
        }
        rbg_choose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //checkedId 是选择的 RadioButton 的 ID
                switch(checkedId){
                    case R.id.rb_consume:
                        chooseConsume = "支出";
                    break;
                    case R.id.rb_income:
                        chooseConsume = "收入";
                    break;
                }
            }
        });

        //按着录音点击事件处理
        btn_record_voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){ //按下
//                    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        showRecordBar(View.VISIBLE);//显示录音图标
//                        // 修改录音状态
//                        mRecord_State = RECORD_ING;
//                        voive_directory = File.separator + "consumemanager_voice_record"
//                                + File.separator + UUID.randomUUID().toString()+".amr";
//                        voice_save_path = Environment.getExternalStorageDirectory() + voive_directory;
//                        // 实例化录音工具类
//                        mRecordUtil = new RecordUtil(voice_save_path);
//                        try {
//                            // 开始录音
//                            mRecordUtil.start();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        new Thread(new Runnable() {
//                            public void run() {
//                                // 初始化录音时间
//                                mRecord_Time = 0;
//                                while (mRecord_State == RECORD_ING) {
//                                    try {
//                                        // 每隔200毫秒就获取声音音量并更新界面显示
//                                        Thread.sleep(200);
//                                        mRecord_Time += 0.2;
//                                        if (mRecord_State == RECORD_ING) {
//                                            mRecord_Volume = mRecordUtil.getAmplitude();
//                                            handler.sendEmptyMessage(0);
//                                        }
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        }).start();
//                    }else{
//                        Toast.makeText(SaveRecordActivity.this,"手机内存没挂载",Toast.LENGTH_SHORT).show();
//                    }

//                }
//                if(event.getAction() == MotionEvent.ACTION_UP){ //抬起
//                    try {
//                        // 停止录音
//                        mRecordUtil.stop();
//                        // 初始录音音量
//                        mRecord_Volume = 0;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    // 如果录音时间小于最短时间
//                    if (mRecord_Time <= MIN_TIME) {
//                        // 显示提醒
//                        Toast.makeText(SaveRecordActivity.this, "录音时间过短", Toast.LENGTH_SHORT).show();
//                        // 修改录音状态
//                        mRecord_State = RECORD_NO;
//                        // 修改录音时间
//                        mRecord_Time = 0;
//                        // 修改显示界面
//                        tv_recordtime.setText("0″");
//                        // 修改录音声音界面
//                        ViewGroup.LayoutParams params = voice_recording_volume.getLayoutParams();
//                        params.height = 0;
//                        voice_recording_volume.setLayoutParams(params);
//                    } else {
//                        // 录音成功,则显示录音成功后的界面
//                        mDisplayVoiceProgressBar.setMax((int) mRecord_Time);
//                        mDisplayVoiceProgressBar.setProgress(0);
//                        mDisplayVoiceTime.setText((int) mRecord_Time + "″");
//                    }
                }
                return true;
            }
        });
    }

    //设置是否显示播放条
    private void showPlayBar(int isVis){
        ll_voiceplay_bar.setVisibility(isVis);
    }

    //设置是否显示录音图标
    private void showRecordBar(int isVis){
        fl_diaplay_voice_icon.setVisibility(isVis);
        tv_record_time.setVisibility(isVis);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_calendar:
                calendarFragment = new CalendarFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.fl_savepage, calendarFragment)
                        .commit();
                break;
            case R.id.btn_today:
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = (SimpleDateFormat)DateFormat.getDateInstance();
                simpleDateFormat.applyPattern("yyyy-MM-dd");
                et_time.setText(simpleDateFormat.format(date));
                break;
            case R.id.btn_save:
                String time = et_time.getText().toString().trim();
                String money = et_money.getText().toString().trim();
                String description = et_description.getText().toString().trim();
                if(saveBefore(time,money,description)){
                    String[] time2 = time.split("-");
                    recordBean._year = Integer.valueOf(time2[0]);
                    recordBean._month = Integer.valueOf(time2[1]);
                    recordBean._day = Integer.valueOf(time2[2]);
                    recordBean._date = time;
                    recordBean._consume = Float.parseFloat(money);
                    recordBean._description = description;
                    recordBean._type = chooseConsume;
                    RecordDao recordDao = new RecordDao(SaveRecordActivity.this);
                    if(isFromEdit){//是否来自编辑请求
                        if(recordDao.updateRecord(recordBean)){
                            Toast.makeText(SaveRecordActivity.this,"修改成功！",Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(SaveRecordActivity.this,"修改失败！",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        if(recordDao.addRecord(recordBean)){
                            Toast.makeText(SaveRecordActivity.this,"保存成功！",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(SaveRecordActivity.this,"保存失败！",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.btn_delete:
                MyDialog myDialog = new MyDialog(SaveRecordActivity.this, "删除", "确定要删除吗？", true, new MyDialog.AfterClickBtn() {
                    @Override
                    public void clickOk() {
                        RecordDao recordDao = new RecordDao(SaveRecordActivity.this);
                        if(recordDao.delete(recordBean._id)){
                            Toast.makeText(SaveRecordActivity.this,"删除成功！",Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(SaveRecordActivity.this,"删除失败！",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void clickConcel() {
                    }
                });
                myDialog.showDialog();
                break;
        }
    }

    /**
     * 保存记录前的检查
     */
    private boolean saveBefore(String time,String money,String description) {
        String money_regu = "^\\d+\\.\\d{1,2}|\\d+$";
        if(TextUtils.isEmpty(time)){
            Toast.makeText(SaveRecordActivity.this,"时间不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(money)){
            Toast.makeText(SaveRecordActivity.this,"金额不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!money.matches(money_regu)){
            Toast.makeText(SaveRecordActivity.this,"金额格式不正确！",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(calendarFragment!=null){
            getFragmentManager().beginTransaction()
                    .remove(calendarFragment)
                    .commit();
            calendarFragment = null;
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onSelectDate(int year,int month,int dayOfMonth) {
        Date date = new Date(year-1900,month,dayOfMonth);
//        String date = String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(dayOfMonth);
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat)DateFormat.getDateInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd");
        et_time.setText(simpleDateFormat.format(date));
    }

    @Override
    public void onClickOK() {
        getFragmentManager().beginTransaction()
                .remove(calendarFragment)
                .commit();
        calendarFragment = null;
    }
}
