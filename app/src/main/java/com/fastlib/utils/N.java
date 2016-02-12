package com.fastlib.utils;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.Toast;

import com.fastlib.R;

/**
 * Notification统一管理类
 */
public class N
{
	public static final int TYPE_TOAST=1;
	public static final int TYPE_SNACKBAR=2;
	public static final int TYPE_NOTIFICATION=3;

	private N()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	public static void showShort(Context context,CharSequence message){
		showToastShort(context, message);
	}

	public static void showLong(Context context,int message){
		showToastLong(context, message);
	}

	/**
	 * 短时间显示Toast
	 *
	 * @param context
	 * @param message
	 */
	public static void showToastShort(Context context,CharSequence message){
		Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
	}

	/**
	 * 短时间显示Toast
	 *
	 * @param context
	 * @param message
	 */
	public static void showToastShort(Context context,int message){
		Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
	}

	/**
	 * 长时间显示Toast
	 *
	 * @param context
	 * @param message
	 */
	public static void showToastLong(Context context,CharSequence message){
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * 长时间显示Toast
	 *
	 * @param context
	 * @param message
	 */
	public static void showToastLong(Context context,int message){
		Toast.makeText(context,message,Toast.LENGTH_LONG).show();
	}

	/**
	 * 自定义时间显示Toast
	 *
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void showToast(Context context,CharSequence message,int duration){
		Toast.makeText(context,message,duration).show();
	}

	/**
	 * 自定义时间显示Toast
	 *
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void showToast(Context context,int message,int duration){
		Toast.makeText(context,message,duration).show();
	}

	/**
	 * 短时间显示Snackbar
	 *
	 * @param view
	 * @param message
	 */
	public static void showSnackbarShort(View view,CharSequence message){
		Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
	}

	/**
	 * 短时间显示Snackbar
	 *
	 * @param view
	 * @param message
	 */
	public static void showSnackbarShort(View view,int message){
		Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
	}

	/**
	 * 长时间显示Snackbar
	 *
	 * @param view
	 * @param message
	 */
	public static void showSnackbarLong(View view,CharSequence message){
		Snackbar.make(view,message,Snackbar.LENGTH_LONG).show();
	}

	/**
	 * 长时间显示Snackbar
	 *
	 * @param view
	 * @param message
	 */
	public static void showSnackbarLong(View view,int message){
		Snackbar.make(view,message,Snackbar.LENGTH_LONG).show();
	}

	/**
	 * 使用launcherIcon，默认的铃声和震动显示message
	 *
	 * @param context
	 * @param message
	 */
	public static void showNotify(Context context,int id,CharSequence message){
		showNotify(context,id,R.drawable.ic_launcher,message,message);
	}

	/**
	 * 指定icon，标题和内容的Notification
	 *
	 * @param context
	 * @param icon
	 * @param title
	 * @param message
	 */
	public static void showNotify(Context context,int id,@DrawableRes int icon,CharSequence title,CharSequence message){
		NotificationManagerCompat manager=NotificationManagerCompat.from(context);
		Notification notification=new NotificationCompat.Builder(context)
				.setDefaults(Notification.DEFAULT_ALL)
				.setAutoCancel(true)
				.setTicker(title)
				.setSmallIcon(icon)
				.setContentText(message)
				.build();
		manager.notify(id,notification);
	}
}