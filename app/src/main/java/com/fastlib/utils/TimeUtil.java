package com.fastlib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.format.DateFormat;

public class TimeUtil {

	private TimeUtil() {

	}

	/**
	 * 转换为1990年01月01日的日期格式
	 * 
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @return
	 */
	public static String formatDate(int year, int monthOfYear, int dayOfMonth) {
		String result = year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日";
		return result;
	}

	public static String[] getYMDDate(String date) {
		for (int i = 0; i < date.split("\\D").length; i++) {
			System.out.println(date.split("\\D")[i].toString());
		}
		return date.split("\\D");
	}

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
		/*
		 * 1秒=1000毫秒
		 * 1分=1000*60毫秒
		 * 1小时=1000*60*60毫秒
		 * 1天=1000*60*60*24毫秒
		 * 2天=1000*60*60*24*2毫秒
		 * 3天=1000*60*60*24*3毫秒
		 */
		
		long timeLag=currentTimeMillis-timeMillis;
		if(timeLag>1000L*60L*60L*24L*3L){//超过三天，显示具体日期
			String formatStr="yyyy-MM-dd";
			return (String) DateFormat.format(formatStr, timeMillis);
		}else if(timeLag>1000L*60L*60L*24L*2L){//显示两天前
			return "两天前";
		}else if(timeLag>1000L*60L*60L*24L){//显示一天前
			return "一天前";
		}else if(timeLag>1000L*60L*60L){//显示几个小时前
			String formatStr="hh:mm:ss";
			return (String) DateFormat.format(formatStr, timeMillis);
		}else if(timeLag>1000L*60L){//显示几分钟前
			String formatStr="hh:mm:ss";
			return (String) DateFormat.format(formatStr, timeMillis);
		}else{//显示几秒前
			return String.valueOf(timeLag/1000L)+"秒前";
		}
		
	}
	
	
	/**
	 * 日期转字符串
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String dateToString(Date date,String format){
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	public static String dateToString(Date date){
		return dateToString(date,"yyyy-mm-dd");
	}
	
	public static Date StringToDate(String dateStr){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-dd");
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getCurrentTime(){
		SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
		return sdf.format(new Date(System.currentTimeMillis()));
	}
	
	public static String millisToString(long timeMillis){
		SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
		return sdf.format(new Date(timeMillis));
	}
	
	/**
	 * 格式化返回毫秒转换具体日期
	 * @param timeMillis
	 * @return
	 */
	public static String formatMillisTime(long timeMillis){
		String suffix="";
		int day=24*60*60*1000;
		long timeDiff=System.currentTimeMillis()-timeMillis;
		Date date=new Date(timeMillis);
		Date dateNow=new Date(System.currentTimeMillis());
		int dayDiff=dateNow.getDate()-date.getDate();
		
		if(dateNow.getMonth()!=date.getMonth()||dateNow.getYear()!=date.getYear())
			return new SimpleDateFormat("MM月dd日 HH:mm").format(new Date(timeMillis));
		
		if(dayDiff==1)
			suffix="昨天";
		else if(dayDiff==2)
			suffix="前天";
		else if(dayDiff>=3)
			return new SimpleDateFormat("MM月dd日 HH:mm").format(new Date(timeMillis));
		return suffix+millisToString(timeMillis);
	}
}
