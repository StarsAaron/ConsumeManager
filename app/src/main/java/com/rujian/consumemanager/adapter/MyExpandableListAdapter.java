package com.rujian.consumemanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rujian.consumemanager.R;
import com.rujian.consumemanager.bean.OneStatusBean;
import com.rujian.consumemanager.bean.RecordBean;
import com.rujian.consumemanager.ui.SaveRecordActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by zhrjian on 2015/11/17.
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context = null;
    private LayoutInflater inflater = null;
    private List<OneStatusBean> oneList;

    public MyExpandableListAdapter(Context context ,List<OneStatusBean> oneList) {
        this.context = context;
        this.oneList = oneList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return oneList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(oneList.get(groupPosition).recordList == null){
            return 0;
        }else{
            return oneList.get(groupPosition).recordList.size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return oneList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return oneList.get(groupPosition).recordList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        OneStatusHolder oneStatusHolder = null;
        View view = null;
        if(convertView != null){
            view = convertView;
            oneStatusHolder = (OneStatusHolder)view.getTag();
        }else{
            oneStatusHolder = new OneStatusHolder();
            view = inflater.inflate(R.layout.one_status_item,null);
            oneStatusHolder.tv_one_status_name = (TextView)view.findViewById(R.id.tv_one_status_name);
            view.setTag(oneStatusHolder);
        }
        oneStatusHolder.tv_one_status_name.setText(oneList.get(groupPosition).typeTitle);
        return view;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TwoStatusHolder twoStatusHolder = null;
        View view = null;
        RecordBean recordBean = (RecordBean)getChild(groupPosition, childPosition);
        if(convertView!=null){
            view = convertView;
            twoStatusHolder = (TwoStatusHolder)view.getTag();
        }else{
            twoStatusHolder = new TwoStatusHolder();
            view = inflater.inflate(R.layout.two_status_item,null);
            twoStatusHolder.tv_two_edit_time = (TextView)view.findViewById(R.id.tv_two_edit_time);
            twoStatusHolder.tv_two_money = (TextView)view.findViewById(R.id.tv_two_money);
            twoStatusHolder.tv_two_description = (TextView)view.findViewById(R.id.tv_two_description);
            twoStatusHolder.iv_two_edit = (ImageView)view.findViewById(R.id.iv_two_edit);
            view.setTag(twoStatusHolder);
        }
        twoStatusHolder.tv_two_edit_time.setText(recordBean._date);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        if(recordBean._type.equals("支出")){
            twoStatusHolder.tv_two_money.setText("-支出"+nf.format(recordBean._consume)+"元");
            twoStatusHolder.tv_two_money.setTextColor(Color.parseColor("#f95858"));
        }else if(recordBean._type.equals("收入")){
            twoStatusHolder.tv_two_money.setText("+收入"+nf.format(recordBean._consume)+"元");
            twoStatusHolder.tv_two_money.setTextColor(Color.parseColor("#2d7dfc"));
        }
        twoStatusHolder.tv_two_description.setText(recordBean._description);
        twoStatusHolder.iv_two_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,SaveRecordActivity.class);
                Bundle bundle = new Bundle();
                RecordBean recordBean = new RecordBean();
                recordBean = oneList.get(groupPosition).recordList.get(childPosition);
                bundle.putSerializable("RecordBean",recordBean);
                intent.putExtra("data",bundle);
                context.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class OneStatusHolder{
        TextView tv_one_status_name;//标题
    }
    static class TwoStatusHolder{
        TextView tv_two_edit_time; //编辑时间
        TextView tv_two_money; //金额
        TextView tv_two_description; //描述
        ImageView iv_two_edit;//编辑按钮
    }
}
