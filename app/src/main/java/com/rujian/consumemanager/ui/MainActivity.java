package com.rujian.consumemanager.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rujian.consumemanager.Myapplication;
import com.rujian.consumemanager.R;
import com.rujian.consumemanager.bean.DateBean;
import com.rujian.consumemanager.bean.RecordBean;
import com.rujian.consumemanager.bean.charbean.MyData;
import com.rujian.consumemanager.bean.charbean.MyDataPosition;
import com.rujian.consumemanager.bean.charbean.RaceCommon;
import com.rujian.consumemanager.bean.charbean.XY;
import com.rujian.consumemanager.db.dao.RecordDao;
import com.rujian.consumemanager.view.raceView.TitleView;
import com.rujian.consumemanager.view.raceView.RaceAxisXView;
import com.rujian.consumemanager.view.raceView.RaceBarView;
import android.widget.RelativeLayout.LayoutParams;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseHeadActivity implements View.OnClickListener {
    private Myapplication myapplication;
    private ImageView iv_add, iv_edit, iv_count, iv_consume_history,iv_pre_view,iv_next_view;
    private TextView tv_nowday,tv_consume_description;
    private List<DateBean> dateBeanList = null;
    private RecordDao recordDao;
    private MydataChangeReceiver mydataChangeReceiver;

    //------------------------
    private LinearLayout axisXLayout = null;
    private LinearLayout threndLine_Layout = null;
    private LinearLayout title_layout = null;
    private RelativeLayout rl_history_view;

    private TitleView titleView;
    private RaceBarView raceBar;
    private RaceAxisXView axisX;

    private XY xy = new XY();
    private float oldX = 0;
    private float oldY = 0;

    private float maxHeight = 0;//柱形条最高
    private int nowYear;//现在显示的年
    private int nowMonth;//现在显示的月
    private int index = -1;//现在时间点位置

    private List<RecordBean> clickDateRecord;//选中的日期记录
    //------------------------

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0x126:
                    addView(); //添加图表视图
                    oldX = 0;
                    break;
                case 0x127:
                    Toast.makeText(MainActivity.this,"没有记录！",Toast.LENGTH_SHORT).show();
                    break;
                case 0x128:
                    if(!dateBeanList.isEmpty()){
                        index = dateBeanList.size()-1; //最新的记录位置
                        DateBean date = dateBeanList.get(index);
                        setTime(date.year,date.month); //设置当前显示的图表的时间
                        setData(nowYear,nowMonth); //设置图表数据
                    }else{
                        threndLine_Layout.removeAllViews();
                        axisXLayout.removeAllViews();
                        title_layout.removeAllViews();
                        tv_nowday.setText("无记录");
                        tv_consume_description.setTextColor(getResources().getColor(R.color.titlebar_bg));
                        tv_consume_description.setText("收支详细");
                    }
                    break;
                case 0x129: //显示描述信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("记录：\n");
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(2);
                    for(RecordBean bean:clickDateRecord){
                        buffer.append(bean._date+"\n"+bean._type+":"+nf.format(bean._consume)+" 元\n");
                        if(!TextUtils.isEmpty(bean._description)){
                            buffer.append("描述："+bean._description+"\n\n");
                        }else{
                            buffer.append("\n");
                        }
                    }
                    if(clickDateRecord.get(0)._type.equals("支出")){
                        tv_consume_description.setTextColor(getResources().getColor(R.color.red));
                    }else{
                        tv_consume_description.setTextColor(getResources().getColor(R.color.titlebar_bg));
                    }
                    tv_consume_description.setText(buffer.toString());
                    break;
                case 0x130:
                    tv_consume_description.setText("无记录");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_main);
        myapplication = (Myapplication) getApplication();
        myapplication.addActivity(this);

        iv_add = (ImageView) findViewById(R.id.iv_add);
        iv_edit = (ImageView) findViewById(R.id.iv_edit);
        iv_count = (ImageView) findViewById(R.id.iv_count);
        iv_consume_history = (ImageView) findViewById(R.id.iv_consume_history);
        iv_pre_view = (ImageView) findViewById(R.id.iv_pre_view);
        iv_next_view = (ImageView) findViewById(R.id.iv_next_view);
        tv_nowday = (TextView) findViewById(R.id.tv_nowday);
        tv_consume_description = (TextView) findViewById(R.id.tv_consume_description);

        setTitle("收支记录条形图");
        showRightImageView();
        hideBackButton();
        iv_add.setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        iv_count.setOnClickListener(this);
        iv_consume_history.setOnClickListener(this);
        iv_pre_view.setOnClickListener(this);
        iv_next_view.setOnClickListener(this);

        recordDao = new RecordDao(MainActivity.this);

        getDateList();//获取有记录时间列表
        setChartView();//设置图表参数

        //注册广播接收器
        mydataChangeReceiver = new MydataChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.rujian.consumemanager.mydataChangeReceiver.datechange");
        registerReceiver(mydataChangeReceiver, intentFilter);
    }

    /**
     * 获取有记录的时间列表
     */
    private void getDateList(){
        new Thread() {
            @Override
            public void run() {
                if (recordDao != null) {
                    dateBeanList = recordDao.selectRecordForYearAndMonth();
                    handler.sendEmptyMessage(0x128);
                }
            }
        }.start();
    }

    /**
     * 图表参数
     */
    private void setChartView(){
        //获取屏幕宽高
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        RaceCommon.screenWidth = mDisplayMetrics.widthPixels; //屏幕宽
        RaceCommon.screenHeight = mDisplayMetrics.heightPixels; //屏幕高
        //设置分组柱状图参数
        RaceCommon.raceWidth = RaceCommon.screenWidth *1/10; //分组宽
        RaceCommon.barWidth = (RaceCommon.raceWidth-10)/2; //条形宽
        RaceCommon.space = 5; //同组柱形之间的距离
        //设置字体大小
        if(RaceCommon.screenWidth > 800){
            RaceCommon.bigSize = 26;
            RaceCommon.smallSize = 24;
        }else if(RaceCommon.screenWidth < 600){
            RaceCommon.bigSize = 17;
            RaceCommon.smallSize = 15;
        }else{
            RaceCommon.bigSize = 18;
            RaceCommon.smallSize = 16;
        }

        //设置图区宽高、内容宽高
        RaceCommon.layoutWidth = RaceCommon.screenWidth;
        RaceCommon.layoutHeight = RaceCommon.screenHeight * 1/3;
        RaceCommon.viewWidth = RaceCommon.raceWidth*(RaceCommon.xScaleArray.length);
        RaceCommon.viewHeight = RaceCommon.screenHeight *1/3;

        //-------初始化各绘图组件 包括设置高宽、位置
        //绘图区宽高
        rl_history_view = (RelativeLayout)findViewById(R.id.rl_history_view);
        RelativeLayout.LayoutParams viewParams = (LayoutParams)rl_history_view.getLayoutParams();
        viewParams.height = RaceCommon.layoutHeight+100;
        viewParams.width = RaceCommon.layoutWidth;
        rl_history_view.setLayoutParams(viewParams);
        //标题
        title_layout = (LinearLayout) findViewById(R.id.titleView);
        //X轴
        axisXLayout = (LinearLayout) findViewById(R.id.axisX);
        RelativeLayout.LayoutParams xParams = (LayoutParams) axisXLayout.getLayoutParams();
        xParams.height = RaceCommon.layoutHeight;
        xParams.width = RaceCommon.layoutWidth;
        xParams.setMargins(xParams.leftMargin+RaceCommon.YWidth, xParams.topMargin, xParams.rightMargin, xParams.bottomMargin);
        axisXLayout.setLayoutParams(xParams);
        //图表宽高
        threndLine_Layout = (LinearLayout) findViewById(R.id.thrend_line);
        RelativeLayout.LayoutParams hParams = (LayoutParams) threndLine_Layout.getLayoutParams();
        hParams.height = RaceCommon.layoutHeight;
        hParams.width = RaceCommon.layoutWidth;
        hParams.setMargins(hParams.leftMargin + RaceCommon.YWidth, hParams.topMargin, hParams.rightMargin, hParams.bottomMargin + RaceCommon.XHeight);
        threndLine_Layout.setLayoutParams(hParams);
        //实例化View
        axisX = new RaceAxisXView(this);
        raceBar = new RaceBarView(this);
        titleView = new TitleView(this);
        //------------------------------------------
        //设置图例
        setKey();
        //设置
        setAxis();
        setTitle();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mydataChangeReceiver);
        super.onDestroy();
    }

    int pressBackNum = 0;
    @Override
    public void onBackPressed() {
        pressBackNum++;
        if (1 == pressBackNum) {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //两秒后归零
                    pressBackNum = 0;
                }
            }, 2000);
        } else if (2 == pressBackNum) {
            myapplication.finishAllActivity();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add: //添加记录
                Intent intent = new Intent(MainActivity.this,SaveRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_edit: //编辑记录
//                Intent intent2 = new Intent(MainActivity.this,SaveRecordActivity.class);
//                Bundle bundle = new Bundle();
//                RecordBean recordBean = new RecordBean();
//                bundle.putSerializable("RecordBean",recordBean);
//                intent2.putExtra("data",bundle);
//                startActivity(intent2);
                break;
            case R.id.iv_count: // 统计
                Intent intent3 = new Intent(MainActivity.this,CalculateActivity.class);
                startActivity(intent3);
                break;
            case R.id.iv_consume_history: //时间轴
                Intent intent4 = new Intent(MainActivity.this,RecordHistoryActivity.class);
                startActivity(intent4);
                break;
            case R.id.iv_pre_view: //上月
                if(index !=-1 && index!=0){
                    index--;
                    DateBean date = dateBeanList.get(index);
                    setTime(date.year,date.month);
                    setData(date.year,date.month);
                }else{
                    Toast.makeText(MainActivity.this,"无记录！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_next_view: //下月
                if(index !=-1 && index!= dateBeanList.size()-1){
                    index++;
                    DateBean date = dateBeanList.get(index);
                    setTime(date.year,date.month);
                    setData(date.year,date.month);
                }else{
                    Toast.makeText(MainActivity.this,"无记录！",Toast.LENGTH_SHORT).show();
                }
        }
    }

    /**
     * 设置显示当前时间
     */
    private void setTime(int year,int month){
        StringBuffer sb = new StringBuffer();
        sb.append(year + "年");
        sb.append(month + "月");
        nowYear = year;
        nowMonth = month;
        tv_nowday.setText(sb.toString());
    }

    /**
     * 设置数据
     */
    private void setData(final int year, final int month) {
        new Thread(){
            @Override
            public void run() {
                maxHeight = 0;
                List<RecordBean> recordBeanList = new ArrayList<>();
                float []consume = new float[31]; //支出
                float []income = new float[31]; //收入

                recordBeanList = recordDao.selectRecordByYearAndMonth(year,month);

                if(!recordBeanList.isEmpty()){
                    for(RecordBean bean:recordBeanList){
                        if(bean._type.equals("支出")){
                            consume[bean._day-1] = consume[bean._day-1]+bean._consume;
                            if(maxHeight < consume[bean._day-1]){
                                maxHeight = consume[bean._day-1]; //获取最大值
                            }
                        }else if(bean._type.equals("收入")){
                            income[bean._day-1] = income[bean._day-1]+bean._consume;
                            if(maxHeight < income[bean._day-1]){
                                maxHeight = income[bean._day-1]; //获取最大值
                            }
                        }
                    }
                    //设置Y轴的轴标
                    float []array = new float[RaceCommon.num];
//                    String c = String.valueOf(maxHeight);
//                    char[] data = c.toCharArray();
//                    int g = 1; //倍数
//                    for(int j=0;j<c.length()-1;j++){
//                        g = g*10;
//                    }
//                    int f =(Integer.parseInt(String.valueOf(data[0])) + 1)*g;//最大数的最高位加1

                    float height = (maxHeight+200)/(RaceCommon.num-1);//每格的高度
                    for(int i=0;i<RaceCommon.num;i++){
                        array[i] = height*i;
                    }
                    RaceCommon.yScaleArray = array;
                    MyData data1 = new MyData();
                    data1.setName("支出");
                    data1.setData(consume);
                    data1.setColor(0xdcf9f3bb);

                    MyData data2 = new MyData();
                    data2.setName("收入");
                    data2.setData(income);
                    data2.setColor(0xdc88fad4);
                    RaceCommon.DataSeries = new ArrayList<MyData>();
                    RaceCommon.DataSeries.add(data1);
                    RaceCommon.DataSeries.add(data2);
                    handler.sendEmptyMessage(0x126);
                }else{
                    handler.sendEmptyMessage(0x127);
                }
            }
        }.start();
    }

    /**
     * 设置图表标题
     */
    private void setTitle(){
        RaceCommon.title = "单位：元";
        RaceCommon.titleX = 20;
        RaceCommon.titleY = 80;
    }

    /**
     * 设置图表图例
     */
    private void setKey(){
        //设置图例参数
        RaceCommon.keyWidth = 20;
        RaceCommon.keyHeight = 20;
        RaceCommon.keyToLeft = 20;
        RaceCommon.keyToTop = 30;
        RaceCommon.keyToNext = 80;
        RaceCommon.keyTextPadding = 5;

    }

    /**
     * 设置X轴
     */
    private void setAxis(){
        //设置x轴参数
        RaceCommon.xScaleArray = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
        //设置x轴颜色
        RaceCommon.xScaleColor = Color.WHITE;
    }

    /**
     * 加入图表
     */
    private void addView(){
        int width=0;
        width=RaceCommon.viewWidth;

        //设定初始定位Y坐标
        xy.y = RaceCommon.viewHeight - RaceCommon.layoutHeight;

        raceBar.initValue(RaceCommon.viewHeight);//传入宽、高、是否在折线图上显示数据
        raceBar.scrollTo(0, xy.y);
        axisX.initValue(width, RaceCommon.viewHeight);//传入高度
        axisX.scrollTo(0, xy.y);
        axisXLayout.removeAllViews();
        axisXLayout.addView(axisX);

        threndLine_Layout.removeAllViews();
        threndLine_Layout.addView(raceBar);

        title_layout.removeAllViews();
        title_layout.addView(titleView);


        boolean isMoving = false;//是否滑动
        //监听滑动事件
        raceBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    oldX = event.getX();
                    oldY = event.getY();
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    parseXY(xy.x += oldX - event.getX(), xy.y += oldY - event.getY(), raceBar.getWidth(), raceBar.getHeight(), threndLine_Layout);
                    System.out.println("x=" + xy.x + "  y=" + xy.y);
                    raceBar.scrollTo(xy.x, xy.y);
//                    axisY_2.scrollTo(0, xy.y);
                    axisX.scrollTo(xy.x, RaceCommon.viewHeight - RaceCommon.layoutHeight);
                    oldX = event.getX();
                    oldY = event.getY();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if((event.getX()-oldX)<2||(event.getX()-oldX)>-2){
                        checkPointInRect(xy.x + oldX);//检测是否点中矩形区
                    }
                }
                return true;
            }
        });
    }

    /**
     * 检测是否点中矩形区
     * @param position
     */
    private void checkPointInRect(final float position){
        new Thread(){
            @Override
            public void run() {
                for(int j=0;j<RaceCommon.DataSeriesPosition.size();j++){
                    MyDataPosition d = RaceCommon.DataSeriesPosition.get(j);
                    for(int i=0;i<d.xPosition.size();i++){
                        float recPo = d.xPosition.get(i)._x;
                        if(recPo < position && position<(recPo+RaceCommon.barWidth)) {
                            Date date = new Date(nowYear - 1900, nowMonth - 1, d.xPosition.get(i)._day);
                            SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateInstance();
                            simpleDateFormat.applyPattern("yyyy-MM-dd");
                            String positionDate = simpleDateFormat.format(date);
                            RecordDao recordDao = new RecordDao(MainActivity.this);
                            clickDateRecord = recordDao.selectRecordByDateAndType(positionDate, d.name);
                            if (clickDateRecord != null && !clickDateRecord.isEmpty()) {
                                handler.sendEmptyMessage(0x129); //发送消息加载数据
                            }
                            break;
                        }
//                        }else{
//                            handler.sendEmptyMessage(0x130); //发送消息无记录
//                        }
                    }
                }
            }
        }.start();
    }

    /**
     * 坐标
     * @param x
     * @param y
     * @param width
     * @param height
     * @param parent
     */
    protected void parseXY(float x,float y,int width,int height,LinearLayout parent) {
        int parentWidth = parent.getWidth();
        int parentHeight = parent.getHeight();
        if(x<0)
            xy.x = 0;
        else if(x > width-parentWidth)
            xy.x = width-parentWidth;
        else
            xy.x = (int) x;

        if(y<0)
            xy.y = 0;
        else if(y > height-parentHeight)
            xy.y = height-parentHeight;
        else
            xy.y = (int) y;

        //初步防抖
        if(width<=parentWidth)
            xy.x = 0;
        if(height<=parentHeight)
            xy.y = 0;
    }

    class MydataChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.rujian.consumemanager.mydataChangeReceiver.datechange")){
                Log.i("change", "---------------数据改变了");
                getDateList();
            }
        }
    }
}
