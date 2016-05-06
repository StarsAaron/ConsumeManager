package com.rujian.consumemanager.view.raceView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.rujian.consumemanager.bean.charbean.MyData;
import com.rujian.consumemanager.bean.charbean.MyDataPosition;
import com.rujian.consumemanager.bean.charbean.MyDateAndX;
import com.rujian.consumemanager.bean.charbean.RaceCommon;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 条线图绘制
 */
public class RaceBarView extends View{
	
	private float xPoint = 0;// 原点X坐标
	private float startPoint = 0; //画图起点X坐标
    private float yPoint = 0;// 原点Y坐标
    private float xLengh = 0;// X轴长度
    private float yLengh = 0;// Y轴长度
    private int widthBorder = 5;// 内边缘宽度，为了统计图不靠在屏幕的边缘上，向边缘缩进距离。最好大于30。
    private float[] yLableArray;// Y轴标签,用于计算
    private float[] begins;//Y轴刻度的y坐标
    private float each;//均分后刻度之间的长度
    
	public RaceBarView(Context context) {
		super(context);
	}

	public void initValue(int Height){
		xPoint = widthBorder;
        yPoint = Height ;
        xLengh = RaceCommon.viewWidth ;
        yLengh = Height ;
        yLableArray = RaceCommon.yScaleArray;
		startPoint = xPoint + (RaceCommon.raceWidth - (RaceCommon.barWidth + RaceCommon.space)*RaceCommon.DataSeries.size())/2;
		
        help2getPoint();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		drawGrid(canvas);
		drawRect(canvas);
	}

    /**
	 * 绘制y轴水平线参考线
	 * @param canvas
	 */
	private void drawGrid(Canvas canvas) {
		Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    	paint.setColor(Color.WHITE);
    	paint.setAlpha(100);
    	
		for(double lineHeight:begins){
			canvas.drawLine(xPoint, (float)lineHeight, xPoint+xLengh, (float)lineHeight, paint);
		}
	}

	/**
	 * 绘制条形
	 * @param canvas
	 */
	private void drawRect(Canvas canvas) {
		int count = 0;
		for(MyData data:RaceCommon.DataSeries){
//			startPoint += (RaceCommon.space+RaceCommon.barWidth);
			float point = startPoint + (RaceCommon.space+RaceCommon.barWidth)*count;
			Paint paint = new Paint();
	        paint.setStyle(Paint.Style.FILL);
	        paint.setAntiAlias(true);
	        paint.setColor(data.getColor());
            paint.setTextSize(RaceCommon.smallSize);// 设置字体

            List<MyDateAndX> dateAndXList = new ArrayList<>();
	        for(int i=0;i<data.getData().length;i++){
				float ydata = getYDataPoint(data.getData()[i]);
	        	float x = point+RaceCommon.raceWidth*i;
				if(ydata != yPoint){//如果为0不显示
                    MyDateAndX myDateAndX = new MyDateAndX();
                    myDateAndX._x = x;
                    myDateAndX._day = i+1;
                    dateAndXList.add(myDateAndX);
					canvas.drawRect(x, ydata, x + RaceCommon.barWidth, yPoint, paint);
					NumberFormat nf = NumberFormat.getInstance();
					nf.setMaximumFractionDigits(2);
					nf.setGroupingUsed(false);
					String re = nf.format(data.getData()[i]);
					canvas.drawText(re, x, ydata-10, paint);
				}
	        }

            MyDataPosition myDataPosition = new MyDataPosition();
            myDataPosition.name = data.getName();
            myDataPosition.xPosition = dateAndXList;
            RaceCommon.DataSeriesPosition.add(myDataPosition);
	        count++;
		}
	}

	/**
     * 均分Y轴
     */
    private void help2getPoint(){
		//均分7份
		int num = RaceCommon.num;
    	begins = new float[num];
    	each = yLengh / num;
    	for(int i=0;i<num;i++){
    		if(i==0){
    			begins[i] = yPoint;
    		}else{
    			begins[i] = begins[i-1] - each;
    		}
    	}
    }
    
    /**
     * 得到点的Y坐标
     * @param data 输入值
     * @return 对应Y坐标
     */
    private float getYDataPoint(double data){
		if(data <= 0.0){
			return yPoint;
		}
    	for(int i=0;i<yLableArray.length;i++){
    		if(data < yLableArray[i]){
				double f = begins[i] + each*(yLableArray[i] - data)/(yLableArray[i] - yLableArray[i-1]);
    			return (float)f;
    		}
    	}
    	return 0;
    }
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension((int)xLengh, (int)yLengh);  
	}
}
