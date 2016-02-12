package com.fastlib.db;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.fastlib.annotation.DatabaseInject;
import com.fastlib.test.Reflect;
import com.google.gson.Gson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

/**
 * orm数据库.提供一些与数据库交互的基本操作
 * 这个数据库应该在application中被生成
 * @author sgfb
 */
public class FastDatabase{
	public final String TAG=FastDatabase.class.getSimpleName();
	public final String DEFAULT_DATABASE_NAME="default";
	
	private static final Object slock=new Object();
	private static FastDatabase sOwer;
	private static DatabaseConfig mConfig;
	
	private Context mContext;
	
	private FastDatabase(Context context){
		mContext=context; 
		mConfig=new DatabaseConfig();
	}

	public static void build(Context context){
		if(sOwer==null){
			synchronized(slock){
				sOwer=new FastDatabase(context);
			}
		}
	}
	
	public static FastDatabase getInstance(){
		return sOwer;
	}
	
	/**
	 * 只有注解了主键并设置了主键值，数据库中如果有这条数据则会返回对应的对象，否则返回null
	 * 
	 * @param obj
	 * @return
	 */
	public Object get(Object obj){
		Field[] fields;
		Field fieldKey=null;
		String primaryKey;

		try {
			fields=obj.getClass().getDeclaredFields();
			for(int i=0;i<fields.length;i++){
				DatabaseInject inject=fields[i].getAnnotation(DatabaseInject.class);
				if(inject.key_primary()){
					fieldKey=fields[i];
					break;
				}
			}
			if(fieldKey==null)
				return null;
			Object key=fieldKey.get(obj);
			List<Object> list=get(obj.getClass(),fieldKey.getName(), Reflect.objToStr(key));
			if(list==null||list.size()==0)
				Log.w(TAG,"向数据库中请求了一个不存在的数据");
			else
				return list.get(0);
		} catch (IllegalAccessException e) {
			return null;
		}
		return obj;
	}

	/**
	 * 返回某主键值为keyValue对象。使用这个方法对象类中必须有主键
	 *
	 * @param cla
	 * @param keyValue 主键值
	 * @return
	 */
	public List<Object> get(Class<?> cla,String keyValue){
		Field[] fields=cla.getDeclaredFields();
		String key=null;
		for(Field f:fields){
			DatabaseInject columnInject=f.getAnnotation(DatabaseInject.class);
			if(columnInject!=null&&columnInject.key_primary()){
				key=f.getName();
				break;
			}
		}
		if(TextUtils.isEmpty(key))
			return null;
		List<Object> list=get(cla,key,keyValue);
		return list;
	}

	/**
	 * 获取对象集合.有where来指定过滤
	 *
	 * @param cla
	 * @param where
	 * @param whereValue
	 * @return
	 */
	public List<Object> get(Class<?> cla,String where,String whereValue){
		DatabaseInject tableInject=cla.getAnnotation(DatabaseInject.class);
		String tableName;
		SQLiteDatabase database=prepare(null);
		Cursor cursor=null;
		List<Object> list=new ArrayList<Object>();
		
		if(tableInject!=null&&!TextUtils.isEmpty(tableInject.tableName()))
			tableName=tableInject.tableName();
		else
			tableName=cla.getSimpleName();
		if(!tableExists(tableName)){
			Log.w(TAG,mConfig.getDatabaseName()+" 不存在表 "+tableName);
			return null;
		}
		if(TextUtils.isEmpty(where))
			cursor=database.rawQuery("select *from "+tableName,null);
		else
			cursor=database.rawQuery("select *from "+tableName+" where "+where+"=?",new String[]{whereValue});
		if(cursor==null)
			return null;
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			try {
				Object obj=cla.newInstance();
				Field[] fields=cla.getDeclaredFields();
				for(Field field:fields){
					field.setAccessible(true);
					String type=field.getType().getSimpleName();
					int columnIndex=cursor.getColumnIndex(field.getName());
					
					if(type.contains("this"))
						continue;
					
					if(field.getType().isArray()){
						Gson gson=new Gson();
						String json=cursor.getString(columnIndex);
						Object[] array;
						
						array=(Object[]) gson.fromJson(json,field.getType());
						field.set(obj, array);
						continue;
					}
					
					switch(type){
					case "int":
						field.setInt(obj, cursor.getInt(columnIndex));
						break;
					case "long":
						field.setLong(obj, cursor.getLong(columnIndex));
						break;
					case "short":
						field.setShort(obj, cursor.getShort(columnIndex));
						break;
					case "boolean":
						int value=cursor.getInt(columnIndex);
						break;
					case "float":
						field.setFloat(obj, cursor.getFloat(columnIndex));
						break;
					case "double":
						field.setDouble(obj, cursor.getDouble(columnIndex));
						break;
					case "String":
						field.set(obj, cursor.getString(columnIndex));
						break;
					default:
						Gson gson=new Gson();
						String json=cursor.getString(columnIndex);
						Object preObj=gson.fromJson(json,field.getType());
						field.set(obj,preObj);
						break;
					}
				}
				list.add(obj);
				cursor.moveToNext();
			} catch (Exception e) {
				return null;
			}
		}
		return list;
	}
	
	/**
	 * 如果obj中没有注解主键，请使用delete(Object obj,String where,String whereValue)
	 * 
	 * @param obj
	 * @return
	 */
	public boolean delete(Object obj){
		Field[] fields=obj.getClass().getDeclaredFields();
		Field primaryField=null;
		String columnName=null;
		String columnValue=null;
		//是否有主键
		
		for(Field field:fields){
			field.setAccessible(true);
			DatabaseInject tableInject=field.getAnnotation(DatabaseInject.class);
			if(tableInject!=null&&tableInject.key_primary()){
				primaryField=field;
				break;
			}
		}
		
		if(primaryField!=null){
			DatabaseInject columnInject=primaryField.getAnnotation(DatabaseInject.class);
			
			if(columnInject!=null&&!TextUtils.isEmpty(columnInject.columnName()))
				columnName=columnInject.columnName();
			else
				columnName=primaryField.getName();
			try{
				switch(primaryField.getType().getSimpleName()){
				case "short":
					columnValue=Short.toString(primaryField.getShort(obj));
					break;
				case "int":
					columnValue=Integer.toString(primaryField.getInt(obj));
					break;
				case "String":
					columnValue=(String)primaryField.get(obj);
					break;
				case "long":
					columnValue=Long.toString(primaryField.getLong(obj));
					break;
				case "float":
					columnValue=Float.toString(primaryField.getFloat(obj));
					break;
				case "double":
					columnValue=Double.toString(primaryField.getDouble(obj));
					break;
				default:
					Log.w(TAG,"不支持 short,int,long,String,float,double 之外的类型做为主键");
					return false;
				}
			}catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			}
		}
		else{
			Log.w(TAG, "错误的使用了delete(Object obj),obj没有注解主键");
			return false;
		}
		return delete(obj,columnName,columnValue);
	}
	
	public boolean delete(Object obj,String where,String whereValue){
		DatabaseInject tableInject=obj.getClass().getAnnotation(DatabaseInject.class);
		String tableName;
		Cursor cursor;
		SQLiteDatabase database;
		
		if(tableInject!=null&&!TextUtils.isEmpty(tableInject.tableName()))
			tableName=tableInject.tableName();
		else
			tableName=obj.getClass().getSimpleName();
		if(!tableExists(tableName)){
			Log.w(TAG,"数据库 "+mConfig.getDatabaseName()+"中不存在表 "+tableName);
			return false;
		}
		database=prepare(null);
		cursor=database.rawQuery("select *from " + tableName + " where " + where + "=" + whereValue, null);
		cursor.moveToFirst();
		if(cursor.isAfterLast()){
			Log.w(TAG,"表中不存在 "+where+"值为"+whereValue+"的数据");
			cursor.close();
			return false;
		}
		else{
			cursor.close();
			try{
				database.beginTransaction();
				database.execSQL("delete from "+tableName+" where "+where+"="+whereValue);
				database.setTransactionSuccessful();
				Log.i(TAG,mConfig.getDatabaseName()+"----d--->"+where);
			}catch(SQLiteException e){
				return false;
			}finally{
				database.endTransaction();
			}
			return true;
	    }
	}
	
	/**
	 * 对无主键或者非主键查询的对象保存
	 * 
	 * @param obj
	 * @param where
	 * @param whereValue
	 * @return
	 */
	public boolean update(Object obj,String where,String whereValue){
		SQLiteDatabase database=prepare(null);
		DatabaseInject inject=obj.getClass().getAnnotation(DatabaseInject.class);
		String tableName=null;
		ContentValues cv=new ContentValues();
		Field[] fields=null;
		StringBuilder sb=new StringBuilder();
		
		if(inject!=null&&!TextUtils.isEmpty(inject.tableName()))
			tableName=inject.tableName();
		else
			tableName=obj.getClass().getSimpleName();
		if(!tableExists(tableName))
			return false;
		Cursor cursor=database.rawQuery("select *from "+tableName+" where "+where+"="+whereValue,null);
		if(cursor.getCount()<=0)
			return false;
		fields=obj.getClass().getDeclaredFields();
		
		for(Field field:fields){
			field.setAccessible(true);
			String type=field.getType().getSimpleName();
			DatabaseInject fieldInject=field.getAnnotation(DatabaseInject.class);
			String columnName=null;
			
			if(fieldInject!=null&&!TextUtils.isEmpty(fieldInject.columnName()))
				columnName=inject.columnName();
			else
				columnName=field.getName();
			try{
				switch(type){
				case "boolean":
					sb.append(columnName+" boolean ,");
					cv.put(columnName, field.getBoolean(obj));
					break;
				case "short":
					sb.append(columnName+" integer ,");
					cv.put(columnName, field.getShort(obj));
					break;
				case "int":
					sb.append(columnName+" integer ,");
					cv.put(columnName,field.getInt(obj));
					break;
				case "long":
					sb.append(columnName+" integer ,");
					cv.put(columnName,field.getLong(obj));
					break;
				case "float":
					sb.append(columnName+" real ,");
					cv.put(columnName, field.getFloat(obj));
					break;
				case "double":
					sb.append(columnName+" real ,");
					cv.put(columnName, field.getDouble(obj));
					break;
				case "char":
					sb.append(columnName+" varchar ,");
					char c=field.getChar(obj);
					if(c==0)
						cv.putNull(columnName);
					else
					    cv.put(columnName, String.valueOf(c));
					break;
				case "String":
					sb.append(columnName+" varchar ,");
					String s=(String)field.get(obj);
					if(s==null)
						cv.putNull(columnName);
					else
					    cv.put(columnName,s);
					break;
				default:
					Object pre=field.get(obj);
					Gson gson=new Gson();
					String json=gson.toJson(pre);

					sb.append(columnName+" varchar,");
					if(pre==null)
						cv.putNull(columnName);
					else
						cv.put(columnName,json);
					break;
				}
			}catch(IllegalAccessException | IllegalArgumentException e){
				return false;
			}
		}
		
		try{
			database.beginTransaction();
			database.update(tableName, cv, null, null);
			database.setTransactionSuccessful();
		}catch(SQLiteException e){
			return false;
		}finally{
			database.endTransaction();
		}
		return true;
	}
	
	/**
	 * 保存或修改对象。如果对象没有主键将不会被修改，只会被保存
	 * 
	 * @param obj
	 * @return
	 */
	public boolean saveOrUpdate(Object obj){
		Field[] fields=obj.getClass().getDeclaredFields();
		SQLiteDatabase database = null;
		StringBuilder sb=new StringBuilder();
		ContentValues cv=new ContentValues();
		String tableName=null;
		boolean isUpdate=false;
		boolean hadPrimaryKey=false;
		DatabaseInject inject=obj.getClass().getAnnotation(DatabaseInject.class);
		
		if(inject==null||TextUtils.isEmpty(inject.tableName()))
			tableName=obj.getClass().getSimpleName();
		else
			tableName=inject.tableName();
		sb.append("create table if not exists "+tableName+"(");
		
		for(Field field:fields){
			field.setAccessible(true);
			DatabaseInject fieldInject=field.getAnnotation(DatabaseInject.class);
			if(fieldInject!=null&&fieldInject.key_primary()){
				String primaryKey=null;
				String primaryValue=null;
				Cursor cursor=null;
				
				if(!TextUtils.isEmpty(fieldInject.columnName()))
					primaryKey=fieldInject.columnName();
				else
					primaryKey=field.getName();
				try{
					switch(field.getType().getSimpleName()){
					case "short":
						primaryValue=Short.toString(field.getShort(obj));
						break;
					case "int":
						primaryValue=Integer.toString(field.getInt(obj));
						break;
					case "long":
						primaryValue=Long.toString(field.getLong(obj));
						break;
					case "String":
						primaryValue="'"+field.get(obj)+"'";
						break;
					case "float":
						primaryValue=Float.toString(field.getFloat(obj));
						break;
					case "double":
						primaryValue=Double.toString(field.getDouble(obj));
						break;
					default:
						throw new IllegalArgumentException("不支持 short,int,long,String,float,double 之外的类型做为主键");
					}
				}catch(IllegalAccessException | IllegalArgumentException e){
					e.printStackTrace();
					return false;
				}
				if(primaryKey!=null){
					database=prepare(null);
					try{
						String sql="select *from "+tableName+" where "+primaryKey+"="+primaryValue;

						cursor=database.rawQuery(sql,null);
						if(cursor.getCount()>0)
							isUpdate=true;
						cursor.close();
					}catch(SQLiteException e){
						//may be no such table
						isUpdate=false;
					}
				}
				hadPrimaryKey=true;
				break;
			}
		}
		
		if(!hadPrimaryKey)
			Log.i(TAG,"你的对象中没有主键，不能使用saveOrUpdate来自动更新");
		for(Field field:fields){
			field.setAccessible(true);
			String type=field.getType().getSimpleName();
			String columnName=null;
			String keyToken="";
			DatabaseInject fieldInject=field.getAnnotation(DatabaseInject.class);
			
			if(fieldInject!=null&&fieldInject.ignore())
				continue;
			if(fieldInject==null||TextUtils.isEmpty(fieldInject.columnName()))
				columnName=field.getName()+" ";
			else
				columnName=fieldInject.columnName();
			if(columnName.contains("this"))
				continue;
			if(fieldInject!=null&&fieldInject.key_primary()){
			    keyToken="primary key";
				if(fieldInject.autoincrement()){
					if(!isUpdate&&tableExists(tableName))
						continue;
				    keyToken+=" autoincrement";
				}
			}
			try{
				if(field.getType().isArray()){
					Object arrayObj=field.get(obj);
					int arrayLen=Array.getLength(arrayObj);
					Object[] array=new Object[arrayLen];
					Gson gson=new Gson();
					
					if(!keyToken.equals(""))
						throw new UnsupportedOperationException("不支持数组成为任何键");
					for(int i=0;i<arrayLen;i++){
						array[i]=Array.get(arrayObj,i);
					}
					sb.append(columnName+" varchar ,");
					if(arrayLen==0)
						cv.putNull(columnName);
					else
					    cv.put(columnName,gson.toJson(array));
					continue;
				}
				
				switch(type){
				case "boolean":
					sb.append(columnName+" boolean "+keyToken+",");
					cv.put(columnName, field.getBoolean(obj));
					break;
				case "short":
					sb.append(columnName+" integer "+keyToken+",");
					cv.put(columnName, field.getShort(obj));
					break;
				case "int":
					sb.append(columnName+" integer "+keyToken+",");
					cv.put(columnName,field.getInt(obj));
					break;
				case "long":
					sb.append(columnName+" integer "+keyToken+",");
					cv.put(columnName,field.getLong(obj));
					break;
				case "float":
					sb.append(columnName+" real "+keyToken+",");
					cv.put(columnName, field.getFloat(obj));
					break;
				case "double":
					sb.append(columnName+" real "+keyToken+",");
					cv.put(columnName, field.getDouble(obj));
					break;
				case "char":
					sb.append(columnName+" varchar "+keyToken+",");
					char c=field.getChar(obj);
					if(c==0)
						cv.putNull(columnName);
					else
					    cv.put(columnName, String.valueOf(c));
					break;
				case "String":
					sb.append(columnName+" varchar "+keyToken+",");
					String s=(String)field.get(obj);
					if(s==null)
						cv.putNull(columnName);
					else
					    cv.put(columnName,s);
					break;
				default:
					Object pre=field.get(obj);
					Gson gson=new Gson();
					String json=gson.toJson(pre);
					
					if(!keyToken.equals(""))
						throw new UnsupportedOperationException("不支持引用作为任何键");
					sb.append(columnName+" varchar,");
					if(pre==null)
						cv.putNull(columnName);
					else
						cv.put(columnName,json);
					break;
				}
			}catch(IllegalAccessException | IllegalArgumentException e){
				return false;
			}
		}
		final String sql=sb.substring(0,sb.toString().length()-1)+")";
		
		try{
		if(isUpdate){
		    database=prepare(null);
		    database.beginTransaction();
		    database.update(tableName,cv, null, null);
		    database.setTransactionSuccessful();
		}
		else{
			database=prepare(sql);
			database.beginTransaction();
			database.insert(tableName,null, cv);
			database.setTransactionSuccessful();
		}
		}catch(SQLiteException e){
			return false;
		}finally{
			database.endTransaction();
		}
		
		if(mConfig.getIsOutInformation()) {
			if(isUpdate)
				Log.d(TAG,mConfig.getDatabaseName()+"<--update--"+tableName);
			else
			    Log.d(TAG, mConfig.getDatabaseName() + "<------" + tableName);
		}
		return true;
	}
	
	private SQLiteDatabase prepare(final String sql){
		SQLiteDatabase database=null;
		SQLiteOpenHelper helper=new SQLiteOpenHelper(mContext,mConfig.getDatabaseName(),null,mConfig.getVersion()){

			@Override
			public void onCreate(SQLiteDatabase db) {
				
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion,
					int newVersion) {
				
			}
		};
		
		database=helper.getWritableDatabase();
		if(!TextUtils.isEmpty(sql))
		    database.execSQL(sql);
		return database;
	}
	
	private boolean tableExists(String tableName){
		SQLiteDatabase database=prepare(null);
		Cursor cursor=null;
		try{
			cursor=database.rawQuery("select *from "+tableName,null);
			cursor.close();
		}catch(SQLiteException e){
			return false;
		}
		return true;
	}
	
	public void switchDatabase(String databaseName){
		mConfig.switchDatabase(databaseName);
	}

	/**
	 * 数据库配置
	 */
	public class DatabaseConfig{
		private boolean isOutInformation;
		private int mVersion;
		private String mCurrentDatabase;
		
		public DatabaseConfig(){
			isOutInformation=true;
			mVersion=1;
			mCurrentDatabase=DEFAULT_DATABASE_NAME+"_"+mVersion+".db";
		}
		
		public void setOutInfomation(boolean isOut){
			isOutInformation=isOut;
		}
		
		/**
		 * 切换数据库，如果不存在会在之后的操作中被创建
		 */
		public void switchDatabase(String databaseName){
			mCurrentDatabase=databaseName+"_"+mVersion+".db";
		}
		
		public String getDatabaseName(){
			return mCurrentDatabase;
		}
		
		public boolean getIsOutInformation(){
			return isOutInformation;
		}
		
		public void setVersion(int version){
			if(version<=mVersion)
				throw new IllegalArgumentException("设置的版本小于等于当前版本");
			mVersion=version;
		}
		
		public int getVersion(){
			return mVersion;
		}
	}
}
