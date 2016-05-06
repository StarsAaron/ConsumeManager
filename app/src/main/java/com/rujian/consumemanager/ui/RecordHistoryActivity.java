package com.rujian.consumemanager.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.rujian.consumemanager.Myapplication;
import com.rujian.consumemanager.R;
import com.rujian.consumemanager.adapter.MyExpandableListAdapter;
import com.rujian.consumemanager.bean.OneStatusBean;
import com.rujian.consumemanager.db.dao.RecordDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhrjian on 2015/11/16.
 */
public class RecordHistoryActivity extends BaseHeadActivity{
    private static final int LOAD_FINISH = 0x123;
    private Myapplication myapplication = null;
    private ExpandableListView expL_history = null;
    private List<OneStatusBean> oneList = null;
    private MyExpandableListAdapter myExpandableListAdapter = null;
    private MydataChangeReceiver mydataChangeReceiver = null;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case LOAD_FINISH: //加载数据完成
                    if(oneList!=null && !oneList.isEmpty() && expL_history!=null){
                        myExpandableListAdapter = new MyExpandableListAdapter(RecordHistoryActivity.this,oneList);
                        expL_history.setAdapter(myExpandableListAdapter);
                        int groupCount = expL_history.getCount();
                        for(int i=0;i<groupCount;i++){
                            expL_history.expandGroup(i);//全部展开
                        }
//                        expL_history.expandGroup(groupCount - 1); //展开最后一项
                    }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_record_history);
        myapplication = (Myapplication) getApplication();
        myapplication.addActivity(this);//加入Activity列表
        registView();
        initView();
    }

    private void registView() {
        setTitle("时间轴");
        showBackButton();
        setLeftImageViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTitleBarColor(getResources().getColor(R.color.red2));
//        hideTitleBar();
        expL_history = (ExpandableListView)findViewById(R.id.expL_history);
    }

    private void initView() {
        expL_history.setGroupIndicator(null);
        expL_history.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (expL_history.isGroupExpanded(groupPosition)) {
                    expL_history.collapseGroup(groupPosition);
                } else {
                    expL_history.expandGroup(groupPosition);
                }
                return true;
            }
        });
        loadData();
        //注册广播接收器
        mydataChangeReceiver = new MydataChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.rujian.consumemanager.mydataChangeReceiver.datechange");
        registerReceiver(mydataChangeReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mydataChangeReceiver);
        super.onDestroy();
    }

    //加载数据
    private void loadData(){
        new Thread(){
            @Override
            public void run() {
                oneList = new ArrayList<OneStatusBean>();
                RecordDao recordDao = new RecordDao(RecordHistoryActivity.this);
                List<Integer> year = recordDao.selectYear();
                for(int i=0;i<year.size();i++){
                    OneStatusBean oneStatusBean = new OneStatusBean();
                    oneStatusBean.typeTitle = String.valueOf(year.get(i));
                    oneStatusBean.recordList = recordDao.selectRecordByYear(year.get(i));
                    oneList.add(oneStatusBean);
                }
                handler.sendEmptyMessage(LOAD_FINISH);
            }
        }.start();
    }

    class MydataChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.rujian.consumemanager.mydataChangeReceiver.datechange")){
                Log.i("change","---------------数据改变了");
                loadData();
            }
        }
    }
}
