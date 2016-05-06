package com.rujian.consumemanager.view.raceView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.rujian.consumemanager.bean.charbean.RaceCommon;

/**
 * 柱形图X轴绘制
 */
@SuppressLint("DrawAllocation")
public class RaceAxisXView extends View {
	private float xPoint = 0;// 原点X坐标
	private float startPoint = 0;// 原点X坐标
    private float yPoint = 0;// 原点Y坐标
    private float xLengh = 240;// X轴长度
    private float yLengh = 320;// Y轴长度
    private int widthBorder = 2;// 内边缘宽度，为了统计图不靠在屏幕的边缘上，向边缘缩进距离
    private String[] xLableArray;// X轴标签
    private int count = 1;//每多少个刻度显示一次文本
    
    public RaceAxisXView(Context context) {
        super(context);
    }

    /**
     * 实例化值
     */
    public void initValue(int Width, int Height) {
        xPoint = widthBorder;
        yPoint = Height - 20;
        xLengh = Width - widthBorder * 2 ;
        yLengh = Height ;
        xLableArray = RaceCommon.xScaleArray;

//        if (xLableArray.length <= 10) {
//            count = 2;
//        } else {
//            count = 4;
//        }
        
        startPoint = xPoint + (RaceCommon.raceWidth - (RaceCommon.barWidth + RaceCommon.space)*RaceCommon.DataSeries.size())/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 设置画笔
        Paint linepaint = new Paint();
        linepaint.setStyle(Paint.Style.FILL);// 设置画笔样式
        linepaint.setAntiAlias(true);// 去锯齿
        linepaint.setColor(RaceCommon.xScaleColor);// 设置颜色
        linepaint.setTextSize(RaceCommon.smallSize);// 设置字体
        linepaint.setStrokeWidth(3);
        // 画X轴轴线
        canvas.drawLine(xPoint, yPoint, xPoint + xLengh, yPoint, linepaint);
        for (int i = 0; xLableArray != null && i < xLableArray.length; i++) {
            // 画X轴刻度
        	if(i%count == 0 ){
        		canvas.drawLine(startPoint+RaceCommon.raceWidth*i, yPoint, startPoint+RaceCommon.raceWidth*i, yPoint + 8, linepaint);
        		canvas.drawText(xLableArray[i]+"", startPoint+RaceCommon.raceWidth*i, yPoint+20, linepaint);
        	}
        	else 
        		canvas.drawLine(startPoint+RaceCommon.raceWidth*i, yPoint, startPoint+RaceCommon.raceWidth*i, yPoint + 4, linepaint);
            
        }
    }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension((int)xLengh, (int)yLengh);  
	}

}
