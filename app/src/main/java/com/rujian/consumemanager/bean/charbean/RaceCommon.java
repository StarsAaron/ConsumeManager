package com.rujian.consumemanager.bean.charbean;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class RaceCommon {
	/**
	 * 一组数据的宽度
	 */
	public static int raceWidth = 0;
	
	/**
	 * 单个柱形宽度
	 */
	public static int barWidth = 0;
	
	/**
	 * 同组柱形之间的距离
	 */
	public static int space = 5;

	public static int screenWidth = 0;
	public static int screenHeight = 0;

	/**
	 * 绘图区域宽度
	 */
	public static int layoutWidth = 0;

	/**
	 * 绘图区域高度
	 */
	public static int layoutHeight = 0;

	/**
	 * 图宽
	 */
	public static int viewWidth = 0;

	/**
	 * 图高
	 */
	public static int viewHeight = 0;

	/**
	 * Y轴区域宽度 (到左边的距离)
	 */
	public static int YWidth = 0;

	/**
	 * Y轴标题
	 */
	public static String YName = "";

	/**
	 * Y轴标题X坐标
	 */
	public static int YName2Left = 0;

	/**
	 * Y轴标题Y坐标
	 */
	public static int YName2Top = 0;

	/**
	 * X轴距下方高度（为刻度文本预留高度）
	 */
	public static int XHeight = 20;

	/**
	 * 标题
	 */
	public static String title = "";

	/**
	 * 标题横坐标
	 */
	public static int  titleX = 20;

	/**
	 * 标题纵坐标
	 */
	public static int  titleY = 40;

	/**
	 * 副标题
	 */
	public static String secondTitle = "";

	/**
	 * 副标题横坐标
	 */
	public static int  StitleX = 20;

	/**
	 * 副标题纵坐标
	 */
	public static int  StitleY = 40;

	/**
	 * 标题颜色
	 */
	public static int titleColor = Color.WHITE;

	/**
	 * 图例宽度
	 */
	public static int keyWidth = 10;

	/**
	 * 图例高度
	 */
	public static int keyHeight = 10;

	/**
	 * 图例横坐标（第一个）
	 */
	public static int keyToLeft = 150;

	/**
	 * 图例纵坐标（第一个）
	 */
	public static int keyToTop = 60;

	/**
	 * 图例之间的距离
	 */
	public static int keyToNext = 30;

	/**
	 * 图例文字与图例距离
	 */
	public static int keyTextPadding = 5;

	/**
	 * X轴刻度名称
	 */
	public static String[] xScaleArray = new String[]{"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};

	/**
	 * X轴颜色
	 */
	public static int xScaleColor = Color.rgb(25, 156, 240);

	/**
	 * Y轴颜色
	 */
	public static int yScaleColor = Color.rgb(25, 156, 240);

	/**
	 * Y轴刻度名称
	 */
	public static float[] yScaleArray = new float[]{0,25,50,100,200,300,500};

	/**
	 * Y轴等级分区名称
	 */
	public static String[] levelName = new String[]{"优","良","轻度","中度","重度","严重"};

	/**
	 * Y轴等级分区颜色
	 */
	public static int[] yScaleColors = new int[]{0xff00ff00,0xffffff00,0xffffa500,0xffff4500,0xffdc143c,0xffa52a2a};

	/**
	 * 绘图数据
	 */
	public static List<MyData> DataSeries = new ArrayList<MyData>();
    /**
     * 绘图数据坐标
     */
    public static List<MyDataPosition> DataSeriesPosition = new ArrayList<MyDataPosition>();

	/**
	 * 标题等字体大小
	 */
	public static int bigSize = 18;

	/**
	 * 副标题等字体大小
	 */
	public static int smallSize = 16;

	/**
	 * Y轴划分的格数
	 */
	public static int num = 7;
}
