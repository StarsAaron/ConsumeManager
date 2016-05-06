package com.rujian.consumemanager.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rujian.consumemanager.bean.DateBean;
import com.rujian.consumemanager.bean.RecordBean;
import com.rujian.consumemanager.db.RecordDBOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecordDao {
    private Context context;
    private RecordDBOpenHelper recordDBOpenHelper = null;

    public RecordDao(Context context) {
        this.context = context;
        recordDBOpenHelper = new RecordDBOpenHelper(context,"recordDB.db",null,1);
    }

    /**
     * 添加记录
     * @param recordrBean
     * @return
     */
    public boolean addRecord(RecordBean recordrBean){
        SQLiteDatabase db = recordDBOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("r_year", recordrBean._year);
        values.put("r_month", recordrBean._month);
        values.put("r_day", recordrBean._day);
        values.put("r_date",recordrBean._date);
        values.put("r_type", recordrBean._type);
        values.put("r_consume", recordrBean._consume);
        values.put("r_description", recordrBean._description);
        long i = db.insert("Record", null, values);
        db.close();
        if(i!=-1){
            Intent intent = new Intent();
            intent.setAction("com.rujian.consumemanager.mydataChangeReceiver.datechange");
            context.sendBroadcast(intent);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 查找记录
     * @return
     */
    public List<RecordBean> selectAll(){
        List<RecordBean> list = new ArrayList<>();
        SQLiteDatabase db = recordDBOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("Record", null, null, null, null, null, null);
        while(cursor.moveToNext()){
            RecordBean recordBean = new RecordBean();
            recordBean._id = cursor.getInt(0);
            recordBean._year = cursor.getInt(1);
            recordBean._month = cursor.getInt(2);
            recordBean._day = cursor.getInt(3);
            recordBean._date = cursor.getString(4);
            recordBean._type = cursor.getString(5);
            recordBean._consume = cursor.getFloat(6);
            recordBean._description = cursor.getString(7);
            list.add(recordBean);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 查找数据库中存在的年份
     * @return
     */
    public List<Integer> selectYear(){
        List<Integer> list = new ArrayList<>();
        SQLiteDatabase db = recordDBOpenHelper.getReadableDatabase();
        String str = "Select distinct r_year from Record order by r_year asc";
        Cursor cursor = db.rawQuery(str, null);
//        Cursor cursor = db.query(true,"Record", new String[]{"r_year"},null, null, null, null,null,null);
        while(cursor.moveToNext()){
            int year = cursor.getInt(0);
            if(!list.contains(year)){
                list.add(year);
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 按年份查找记录
     * @return
     */
    public List<RecordBean> selectRecordByYear(int year){
        List<RecordBean> list = new ArrayList<>();
        SQLiteDatabase db = recordDBOpenHelper.getReadableDatabase();
        String str = "Select * from Record where r_year=? order by r_month,r_day asc";
        Cursor cursor = db.rawQuery(str,new String[]{String.valueOf(year)});
//        Cursor cursor = db.query("Record", null, "WHERE r_year=?", new String[]{String.valueOf(year)}, null, null, null);
        while(cursor.moveToNext()){
            RecordBean recordBean = new RecordBean();
            recordBean._id = cursor.getInt(0);
            recordBean._year = cursor.getInt(1);
            recordBean._month = cursor.getInt(2);
            recordBean._day = cursor.getInt(3);
            recordBean._date = cursor.getString(4);
            recordBean._type = cursor.getString(5);
            recordBean._consume = cursor.getFloat(6);
            recordBean._description = cursor.getString(7);
            list.add(recordBean);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 按年份和月份查找记录
     * @return
     */
    public List<RecordBean> selectRecordByYearAndMonth(int year,int month){
        List<RecordBean> list = new ArrayList<>();
        SQLiteDatabase db = recordDBOpenHelper.getReadableDatabase();
        String str = "Select * from Record where r_year=? And r_month=? order by r_day asc";
        Cursor cursor = db.rawQuery(str,new String[]{String.valueOf(year),String.valueOf(month)});
//        Cursor cursor = db.query("Record", null, "WHERE r_year=?", new String[]{String.valueOf(year)}, null, null, null);
        while(cursor.moveToNext()){
            RecordBean recordBean = new RecordBean();
            recordBean._id = cursor.getInt(0);
            recordBean._year = cursor.getInt(1);
            recordBean._month = cursor.getInt(2);
            recordBean._day = cursor.getInt(3);
            recordBean._date = cursor.getString(4);
            recordBean._type = cursor.getString(5);
            recordBean._consume = cursor.getFloat(6);
            recordBean._description = cursor.getString(7);
            list.add(recordBean);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 查找存在记录的年月
     * @return
     */
    public List<DateBean> selectRecordForYearAndMonth(){
        List<DateBean> list = new ArrayList<>();
        SQLiteDatabase db = recordDBOpenHelper.getReadableDatabase();
        String str = "Select distinct r_year,r_month from Record order by r_year,r_month asc";
        Cursor cursor = db.rawQuery(str,null);
        while(cursor.moveToNext()){
            DateBean dateBean = new DateBean();
            dateBean.year = cursor.getInt(0);
            dateBean.month = cursor.getInt(1);
            list.add(dateBean);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 查找时间范围记录
     * @return
     */
    public HashMap<String,Float> selectRecordBetween(String startTime,String endTime){
        HashMap<String,Float> map = new HashMap<>();
        SQLiteDatabase db = recordDBOpenHelper.getReadableDatabase();
        String str = "Select r_type,sum(r_consume) from Record where r_date BETWEEN ? And ? group by r_type";
        Cursor cursor = db.rawQuery(str,new String[]{startTime,endTime});
        while(cursor.moveToNext()){
            map.put(cursor.getString(0), cursor.getFloat(1));
        }
        cursor.close();
        db.close();
        return map;
    }

    /**
     * 按日期和类型查找记录
     * @return
     */
    public List<RecordBean> selectRecordByDateAndType(String date,String typename){
        List<RecordBean> list = new ArrayList<>();
        SQLiteDatabase db = recordDBOpenHelper.getReadableDatabase();
        String str = "Select * from Record where r_date=? And r_type=?";
        Cursor cursor = db.rawQuery(str,new String[]{date,typename});
        while(cursor.moveToNext()){
            RecordBean recordBean = new RecordBean();
            recordBean._id = cursor.getInt(0);
            recordBean._year = cursor.getInt(1);
            recordBean._month = cursor.getInt(2);
            recordBean._day = cursor.getInt(3);
            recordBean._date = cursor.getString(4);
            recordBean._type = cursor.getString(5);
            recordBean._consume = cursor.getFloat(6);
            recordBean._description = cursor.getString(7);
            list.add(recordBean);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 更新记录
     * @return
     */
    public boolean updateRecord(RecordBean recordrBean){
        SQLiteDatabase db = recordDBOpenHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("r_year", recordrBean._year);
        values.put("r_month", recordrBean._month);
        values.put("r_day", recordrBean._day);
        values.put("r_date", recordrBean._date);
        values.put("r_type", recordrBean._type);
        values.put("r_consume", recordrBean._consume);
        values.put("r_description", recordrBean._description);
        int i = db.update("Record", values, "_id=?", new String[]{String.valueOf(recordrBean._id)});
        if(i != 0){
            db.close();
            Intent intent = new Intent();
            intent.setAction("com.rujian.consumemanager.mydataChangeReceiver.datechange");
            context.sendBroadcast(intent);
            return true;
        } else {
            db.close();
            return false;
        }
    }

    /**
     * 删除某条记录
     */
    public boolean delete(int id){
        SQLiteDatabase db = recordDBOpenHelper.getWritableDatabase();
        if(db.delete("Record", "_id=?", new String[]{String.valueOf(id)})!=-1){
            db.close();
            Intent intent = new Intent();
            intent.setAction("com.rujian.consumemanager.mydataChangeReceiver.datechange");
            context.sendBroadcast(intent);
            return true;
        }else{
            db.close();
            return false;
        }

    }

    /**
     * 删除所有记录
     */
    public boolean deleteAll(){
        SQLiteDatabase db = recordDBOpenHelper.getWritableDatabase();
        if(db.delete("Record", "1", null)!=-1){
            db.close();
            Intent intent = new Intent();
            intent.setAction("com.rujian.consumemanager.mydataChangeReceiver.datechange");
            context.sendBroadcast(intent);
            return true;
        }else{
            db.close();
            return false;
        }

    }

}
