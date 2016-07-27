package com.fastlib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.format.DateFormat;

public class TimeUtil {

	private TimeUtil(){}

	/**
	 * 格式化时间差
	 * 
	 * @param timeMillis
	 * @return
	 */
	public static String formatTimeLag(long timeMillis) {
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis - timeMillis < 0) {//发布时间比系统当前时间更晚
			return "兄弟，你穿越了";
		}
		
		long timeLag=currentTimeMillis-timeMillis;
		if(timeLag>1000L*60L*60L*24L*3L){//超过三天，显示具体日期
			String formatStr="yyyy-MM-dd";
			return (String) DateFormat.format(formatStr, timeMillis);
		}else if(timeLag>1000L*60L*60L*24L*2L){//显示两天前
			return "两天前";
		}else if(timeLag>1000L*60L*60L*24L){//显示一天前
			return "一天前";
		}else if(timeLag>1000L*60L*60L){//显示几个小时前
			int hour= (int) (timeLag/1000/60/60);
			return hour+"小时前";
		}else if(timeLag>1000L*60L){//显示几分钟前
			int minute=(int)(timeLag/1000/60);
			return minute+"分钟前";
		}else{//显示几秒前
			return String.valueOf(timeLag/1000L)+"秒前";
		}
	}
	
	
	/**
	 * 日期转字符串
	 * @param date
	 * @param format
	 * @return
	 */
	public static String dateToString(Date date,String format){
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	public static String dateToString(Date date){
		return dateToString(date,"yyyy-MM-dd hh:mm:ss");
	}
	
	public static Date StringToDate(String dateStr,String format){
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
