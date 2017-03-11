package com.fastlib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.format.DateFormat;

public class TimeUtil {

	private TimeUtil(){}

            /**
	 * 返回N天，N月，N年前
	 * @param timeMillis
	 * @return
	 */
	public static String formatTimeLag(long timeMillis){
		final int hour=1000*60*60;
		final int minute=1000*60;
		long currTime=System.currentTimeMillis();
		Calendar currCalendar=Calendar.getInstance(); //当前时间日历
		Calendar customCalendar=Calendar.getInstance(); //自定义时间日历

		customCalendar.setTime(new Date(timeMillis));
		//从年，月，天遍历下去
		int currYear=currCalendar.get(Calendar.YEAR);
		int customYear=customCalendar.get(Calendar.YEAR);
		if(currYear!=customYear)
			return (currYear-customYear)+"年前";
		int currMonth=currCalendar.get(Calendar.MONTH);
		int customMonth=customCalendar.get(Calendar.MONTH);
		if(currMonth!=customMonth)
			return (currMonth-customMonth)+"月前";
		int currDay=currCalendar.get(Calendar.DAY_OF_MONTH);
		int customDay=customCalendar.get(Calendar.DAY_OF_MONTH);
		if(currDay!=customDay)
			return (currDay-customDay)+"天前";
		//小时，分，秒
		if(currTime-timeMillis>hour)
			return ((currTime-timeMillis)/hour)+"小时前";
		if(currTime-timeMillis>minute)
			return ((currTime-timeMillis)/minute)+"分钟前";
		return ((currTime-timeMillis)/1000)+"秒前";
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
