package com.fastlib.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import java.util.Random;

/**
 * 应用信息
 */
public class AppInformation
{

	private AppInformation()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("Class AppUtils cannot be instantiated");

	}

	/**
	 * [获取应用程序名称]
	 */
	public static String getAppName(Context context)
	{
		try
		{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * [获取应用程序版本名称信息]
	 * 
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context)
	{
		try
		{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * [获取应用程序版本号]
	 * 
	 * @param context
	 * @return 当前应用的版本
	 */
	public static int getVersionCode(Context context)
	{
		try
		{
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionCode;

		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 随机值
	 * @param length 长度
	 * @param max 每一位的限值0~9
	 * @return
	 */
	private String randomStr(int length,int... max){
		StringBuilder sb=new StringBuilder();
		Random random=new Random();
		
		for(int i=0;i<length;i++){
			int m=max.length<i+1?10:max[i];
			if(m>10)
				m=0;
			if(m<0)
				m=0;
			sb.append(random.nextInt(m));
		}
		return sb.toString();
	}

}