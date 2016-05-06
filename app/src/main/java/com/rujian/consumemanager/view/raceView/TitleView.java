package com.rujian.consumemanager.view.raceView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.rujian.consumemanager.R;
import com.rujian.consumemanager.bean.charbean.MyData;
import com.rujian.consumemanager.bean.charbean.RaceCommon;

/**
 * 标题及图例等
 * @author ZLL
 */
@SuppressLint("DrawAllocation")
public class TitleView extends View {
    public TitleView(Context context) {
        super(context);
    }

    /**
     * 实例化值
     *
     */
    public void initValue() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 设置画笔
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);// 设置画笔样式
        paint.setAntiAlias(true);// 去锯齿
        paint.setColor(Color.BLACK);// 设置颜色
        paint.setTextSize(RaceCommon.smallSize);// 设置字体
        paint.setStrokeWidth(1);

        
        drawTitle(canvas);
        drawRect(canvas);
        drawYName(canvas);
    }
    
    private void drawTitle(Canvas canvas){
    	// 设置画笔
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);// 设置画笔样式
        paint.setAntiAlias(true);// 去锯齿
        paint.setColor(RaceCommon.titleColor);// 设置颜色
        paint.setTextSize(RaceCommon.bigSize);// 设置字体
        
        canvas.drawText(RaceCommon.title, RaceCommon.titleX, RaceCommon.titleY, paint);
        
        paint.setTextSize(RaceCommon.bigSize);
        canvas.drawText(RaceCommon.secondTitle, RaceCommon.StitleX, RaceCommon.StitleY , paint);
    }    
    
    private void drawRect(Canvas canvas){
    	int count = 0;
    	
    	int width = RaceCommon.keyWidth;
    	int height = RaceCommon.keyHeight;
    	int toLeft = RaceCommon.keyToLeft;
    	int toTop = RaceCommon.keyToTop;
    	int toNext = RaceCommon.keyToNext;
    	int textPadding = RaceCommon.keyTextPadding;
    	
    	for(MyData data:RaceCommon.DataSeries){
    		Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);// 设置画笔样式
            paint.setAntiAlias(true);// 去锯齿
            paint.setColor(data.getColor());// 设置颜色
            paint.setTextSize(RaceCommon.smallSize);// 设置字体

            if(toLeft+toNext*count+width > RaceCommon.screenWidth){
            	toTop += height*3/2;
            	count = 0;
            }
	        canvas.drawRect(toLeft+toNext*count, toTop, toLeft+toNext*count+width, toTop+height, paint);
	        canvas.drawText(data.getName(), toLeft+toNext*count+width+textPadding, toTop+height, paint);
            
            count++;
    	}
    }
    
    private void drawYName(Canvas canvas){
    	
    	Paint paint = new Paint();
    	paint.setStyle(Paint.Style.FILL_AND_STROKE);
    	paint.setAntiAlias(true);
    	paint.setTextSize(RaceCommon.bigSize);
    	paint.setColor(RaceCommon.titleColor);
    	canvas.rotate(-90, RaceCommon.YName2Left, RaceCommon.YName2Top);
    	canvas.drawText(RaceCommon.YName, RaceCommon.YName2Left, RaceCommon.YName2Top, paint);
    }
}
