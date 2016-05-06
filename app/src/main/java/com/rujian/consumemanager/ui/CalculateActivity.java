package com.rujian.consumemanager.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rujian.consumemanager.Myapplication;
import com.rujian.consumemanager.R;
import com.rujian.consumemanager.db.dao.RecordDao;
import com.rujian.consumemanager.ui.fragment.CalendarFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhrjian on 2015/11/16.
 */
public class CalculateActivity extends BaseHeadActivity implements View.OnClickListener, CalendarFragment.ChooseDateListener {
    private Myapplication myapplication;
    private Button btn_calcu;
    private EditText et_fromdate, et_todate;
    private CalendarFragment calendarFragment;
    private ImageView iv_fromdate_calcendar, iv_todate_calcendar;
    private FrameLayout fl_calcu_page;
    private int edit_id = 0;//正在打开日历的EditText
    private String fromdate;
    private String todate;
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_calculate);
        myapplication = (Myapplication) getApplication();
        myapplication.addActivity(this);
        registView();
        initView();
    }

    private void registView() {
        setTitle("统计数据");
        showBackButton();
        setTitleBarColor(getResources().getColor(R.color.orange));
        setLeftImageViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_calcu = (Button) findViewById(R.id.btn_calcu);
        et_fromdate = (EditText) findViewById(R.id.et_fromdate);
        et_todate = (EditText) findViewById(R.id.et_todate);
        iv_fromdate_calcendar = (ImageView) findViewById(R.id.iv_fromdate_calcendar);
        iv_todate_calcendar = (ImageView) findViewById(R.id.iv_todate_calcendar);
        fl_calcu_page = (FrameLayout) findViewById(R.id.fl_calcu_page);
        tv_result = (TextView) findViewById(R.id.tv_result);
    }

    private void initView() {
        btn_calcu.setOnClickListener(this);
        iv_fromdate_calcendar.setOnClickListener(this);
        iv_todate_calcendar.setOnClickListener(this);
    }

    @Override
    public void onSelectDate(int year, int month, int dayOfMonth) {
        Date date = new Date(year-1900,month,dayOfMonth);
//        String date = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd");
        switch (edit_id) {
            case R.id.iv_fromdate_calcendar:
                et_fromdate.setText(simpleDateFormat.format(date));
                break;
            case R.id.iv_todate_calcendar:
                et_todate.setText(simpleDateFormat.format(date));
                break;
        }
    }

    @Override
    public void onClickOK() {
        getFragmentManager().beginTransaction()
                .remove(calendarFragment)
                .commit();
        calendarFragment = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_calcu:
                if(checkDateArea()){
                    RecordDao recordDao = new RecordDao(CalculateActivity.this);
                    HashMap<String,Float> result = recordDao.selectRecordBetween(et_fromdate.getText().toString().trim(), et_todate.getText().toString().trim());
                    if(result != null){
                        StringBuffer buffer = new StringBuffer();
                        buffer.append("时间从 "+fromdate+" 到 "+todate+"的收支统计数据如下：\n");
                        if(result.containsKey("支出")){
                            double consume = result.get("支出");
                            buffer.append("总支出："+consume+" 元\n");
                        }
                        if(result.containsKey("收入")){
                            double income = result.get("收入");
                            buffer.append("总收入："+income+" 元\n");
                        }
                        tv_result.setText(buffer.toString());
                    }else{
                        Toast.makeText(CalculateActivity.this, "无记录！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.iv_fromdate_calcendar:
                setToday(et_fromdate);
                calendarFragment = new CalendarFragment();
                getFragmentManager().beginTransaction().add(R.id.fl_calcu_page, calendarFragment).commit();
                edit_id = R.id.iv_fromdate_calcendar;//保存当前使用日历的编辑框id
                break;
            case R.id.iv_todate_calcendar:
                setToday(et_todate);
                calendarFragment = new CalendarFragment();
                getFragmentManager().beginTransaction().add(R.id.fl_calcu_page, calendarFragment).commit();
                edit_id = R.id.iv_todate_calcendar;//保存当前使用日历的编辑框id
                break;

        }
    }

    private void setToday(EditText et_id){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat)DateFormat.getDateInstance();
        simpleDateFormat.applyPattern("yyyy-MM-dd");
        et_id.setText(simpleDateFormat.format(date));
    }

    @Override
    public void onBackPressed() {
        if(calendarFragment!=null){ //按回车键，如果calendarFragment打开了，就只关闭calendarFragment
            getFragmentManager().beginTransaction()
                    .remove(calendarFragment)
                    .commit();
            calendarFragment = null;
        }else{  //否则关闭当前Activity
            super.onBackPressed();
        }
    }

    /**
     * 检查时间范围
     *
     * @return
     */
    private boolean checkDateArea() {
        fromdate = et_fromdate.getText().toString().trim();
        todate = et_todate.getText().toString().trim();
        //日期格式yyyy-mm-dd判断（可怕判断闰年）
        String match = "((((19|20)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))" +
                "|(((19|20)\\d{2})-(0?[469]|11)-(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))" +
                "|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))-0?2-(0?[1-9]|[12]\\d)))$";
        Pattern pattern = Pattern.compile(match);
        if (TextUtils.isEmpty(fromdate)) {
            et_fromdate.setError("请填写开始时间");
            Toast.makeText(CalculateActivity.this, "请填写开始时间", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!(pattern.matcher(fromdate).matches())) {
//            et_fromdate.setError("格式不正确");
            Toast.makeText(CalculateActivity.this, "开始时间格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(todate)) {
//            et_todate.setError("请填写结束时间");
            Toast.makeText(CalculateActivity.this, "请填写结束时间", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!(pattern.matcher(todate).matches())) {
//            et_todate.setError("格式不正确");
            Toast.makeText(CalculateActivity.this, "结束时间格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fromdate.compareTo(todate) > 0) {
            Toast.makeText(CalculateActivity.this, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
