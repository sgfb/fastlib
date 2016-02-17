package com.fastlib.db;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fastlib.annotation.DatabaseInject;
import com.fastlib.bean.DatabaseTable;
import com.fastlib.utils.Reflect;
import com.google.gson.Gson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
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

	private FastDatabase(Context context,DatabaseConfig config){
		mContext=context;
		mConfig=config;
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
	 * 注解主键并且设置了主键值，数据库中如果有这条数据则会返回对应的对象，否则返回null
	 *
	 * @param obj
	 * @return
	 */
	public Object get(Object obj){
		Field[] fields;
		Field fieldKey=null;

		try {
			fields=obj.getClass().getDeclaredFields();
			for(int i=0;i<fields.length;i++){
				DatabaseInject inject=fields[i].getAnnotation(DatabaseInject.class);
				if(inject!=null&&inject.keyPrimary()){
					fieldKey=fields[i];
					break;
				}
			}
			if(fieldKey==null) {
				if(mConfig.isOutInformation)
					Log.w(TAG,"没有注解主键的对象无法使用get(Object obj)");
				return null;
			}
			Object key=fieldKey.get(obj);
			List<Object> list=get(obj.getClass(),fieldKey.getName(), Reflect.objToStr(key));
			if(list==null||list.size()==0){
				if(mConfig.isOutInformation)
				    Log.w(TAG, "向数据库中请求了一个不存在的数据");
			}
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
			if(columnInject!=null&&columnInject.keyPrimary()){
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

		tableName=cla.getClass().getName();
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
		String columnName;
		String columnValue;
		//是否有主键

		for(Field field:fields){
			field.setAccessible(true);
			DatabaseInject tableInject=field.getAnnotation(DatabaseInject.class);
			if(tableInject!=null&&tableInject.keyPrimary()){
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

	/**
	 * 删除主键值为keyValue的数据，需要注解主键
	 * @param cla
	 * @param keyValue
	 * @return
	 */
	public boolean delete(Class<?> cla,String keyValue){
		return false;
	}

	/**
	 * 输出某条数据，以where为过滤条件
	 * @param obj
	 * @param where
	 * @param whereValue
	 * @return
	 */
	public boolean delete(Object obj,String where,String whereValue){
		DatabaseInject tableInject=obj.getClass().getAnnotation(DatabaseInject.class);
		String tableName;
		Cursor cursor;
		SQLiteDatabase database;

		tableName=obj.getClass().getName();
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
		String tableName;
		ContentValues cv=new ContentValues();
		Field[] fields;
		StringBuilder sb=new StringBuilder();

		tableName=obj.getClass().getName();
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
	 * 保存或修改对象
	 *
	 * @param obj
	 * @return
	 */
	public boolean saveOrUpdate(Object obj){
		Field[] fields=obj.getClass().getDeclaredFields();
		SQLiteDatabase database = null;
		StringBuilder sb=new StringBuilder();
		ContentValues cv=new ContentValues();
		String tableName;
		boolean isUpdate=false;
		boolean hadPrimaryKey=false;

		tableName=obj.getClass().getName();
		sb.append("create table if not exists "+tableName+"(");

		for(Field field:fields){
			field.setAccessible(true);
			DatabaseInject fieldInject=field.getAnnotation(DatabaseInject.class);
			if(fieldInject!=null&&fieldInject.keyPrimary()){
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
			String columnName;
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
			if(fieldInject!=null&&fieldInject.keyPrimary()){
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

	/**
	 * 对一个存在的表和对象进行检查修改
	 * @param cla
	 */
	public void alterTable(Class<?> cla, List<String> valueToValue, List<String> newColumn){
		if(valueToValue==null&&newColumn==null)
			throw new RuntimeException("没有提供要如何修改数据表");

		SQLiteDatabase db=mContext.openOrCreateDatabase("test.db",Context.MODE_PRIVATE,null);
		String tableName=cla.getCanonicalName();
		String tempName="temp_table_"+Long.toString(System.currentTimeMillis());
		Iterator<String> iter;

		if(valueToValue==null){
			if(newColumn!=null){
				iter=newColumn.iterator();
				while(iter.hasNext()){
					String column=iter.next();
					db.execSQL("alter table '"+tableName+"' add "+column);
				}
			}
		}
		else{
			StringBuilder sb=new StringBuilder();
			iter=valueToValue.iterator();
			while(iter.hasNext())
				sb.append("'"+iter.next()+"'"+",");
			sb.deleteCharAt(sb.length()-1);
			if(valueToValue!=null){
				db.execSQL("alter table '"+tableName+"' rename to '"+tempName+"'");
				db.execSQL(generateCreateTableSql(cla));
				//注意不能复制主键值
				db.execSQL("insert into '" + tableName + "' (" + sb.toString() + ") select " + sb.toString() + " from " + tempName);
				db.execSQL("drop table '" + tempName+"'");
			}
		}
	}

	public String generateCreateTableSql(Class<?> cla){
		StringBuilder sb=new StringBuilder();
		DatabaseTable table=loadAttribute(cla);

		sb.append("create table if not exists '" + table.tableName + "'(");

		Iterator<String> iter=table.columnMap.keySet().iterator();
		while(iter.hasNext()){
			String key=iter.next();
			DatabaseTable.DatabaseColumn column=table.columnMap.get(key);

			if(column.isIgnore){
				continue;
			}
			sb.append(column.columnName)
					.append(" " + column.type);
			if(column.isPrimaryKey)
				sb.append(" primary key");
			if(column.autoincrement)
				sb.append(" autoincrement");
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		return sb.toString();
	}

	private DatabaseTable loadAttribute(Class<?> cla){
		DatabaseTable dt=new DatabaseTable();
		Field[] fields=cla.getDeclaredFields();
		dt.tableName=cla.getName();

		for(Field f:fields){
			DatabaseInject fieldInject=f.getAnnotation(DatabaseInject.class);
			DatabaseTable.DatabaseColumn column=new DatabaseTable.DatabaseColumn();
			String type=f.getType().getSimpleName();

			if(f.getClass().isArray())
				type=f.getType().getCanonicalName();
			column.columnName=f.getName();
			column.type=Reflect.toSQLType(type);
			if(fieldInject!=null){
				if(!TextUtils.isEmpty(fieldInject.columnName()))
					column.columnName=fieldInject.columnName();
				column.autoincrement=fieldInject.autoincrement();
				column.isPrimaryKey=fieldInject.keyPrimary();
				column.isIgnore=fieldInject.ignore();
			}
			dt.columnMap.put(f.getName(),column);
		}
		return dt;
	}

	private SQLiteDatabase prepare(final String sql){
		SQLiteDatabase database;
		SQLiteOpenHelper helper=new SQLiteOpenHelper(mContext,mConfig.getDatabaseName(),null,mConfig.getVersion()){

			@Override
			public void onCreate(SQLiteDatabase db) {

			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion,
					int newVersion) {
				updateDatabase(db);
			}
		};

		database=helper.getWritableDatabase();
		if(!TextUtils.isEmpty(sql))
		    database.execSQL(sql);
		return database;
	}

	/**
	 * 遍历所有table反射对象来作对比调整
	 * @param db
	 */
	private void updateDatabase(SQLiteDatabase db){
		Cursor cursor=db.rawQuery("select name from sqlite_master where type='table'", null);
		if(cursor!=null){
			cursor.moveToFirst();
			while(!cursor.isAfterLast()){
				String tableName=cursor.getString(0);
				Cursor tableCursor=db.rawQuery("pragma table_info("+"'"+tableName+"')", null);
				if(tableCursor!=null){
					tableCursor.moveToFirst();
					checkColumnChanged(db,tableName);
				}
				cursor.moveToNext();
			}
		}
	}

	/**
	 * 检查类是否被修改，如果被修改列也跟随修改
	 * @param tableName
	 * @throws ClassNotFoundException
	 */
	private void checkColumnChanged(SQLiteDatabase db,String tableName){
		Class<?> cla;
		Field[] fields;
		Map<String,Field> fieldMap=new HashMap<>();
		boolean needAlter=false;
		List<String> convertDatas=new ArrayList<>();

		try {
			//如果对象类不存在则删除这张表
			cla=Class.forName(tableName);
		} catch (ClassNotFoundException e) {
			db.execSQL("drop table "+tableName);
			return;
		}
		fields=cla.getDeclaredFields();
		for(Field field:fields) {
			//列名以注解为优先，默认字段名
			String columnName=field.getName();
			DatabaseInject inject=field.getAnnotation(DatabaseInject.class);
			if(inject!=null){
				if(inject.ignore())
					continue;
				if(!TextUtils.isEmpty(inject.columnName()))
					columnName=inject.columnName();
			}
			fieldMap.put(columnName, field);
		}
		DatabaseTable table=parse(db,tableName);
		Iterator<String> iter=table.columnMap.keySet().iterator();

		while(iter.hasNext()){
			String key=iter.next();
			DatabaseTable.DatabaseColumn column=table.columnMap.get(key);
			Field field=fieldMap.remove(key);
			DatabaseInject inject=field.getAnnotation(DatabaseInject.class);

			convertDatas.add(column.columnName);
			if(column.isPrimaryKey){
				if(inject==null||!inject.keyPrimary())
					needAlter=true;
			}
			else{
				if(inject!=null&&inject.keyPrimary())
					needAlter=true;
			}
			if(column.autoincrement){
				if (inject==null||!inject.autoincrement())
					needAlter=true;
			}
			else{
				if(inject!=null&&inject.autoincrement())
					needAlter=true;
			}
			String fieldType;
			if(field.getClass().isArray())
				fieldType=field.getClass().getComponentType().getSimpleName();
			else
			    fieldType=field.getType().getSimpleName();
			switch (column.type){
				case "integer":
					if(!Reflect.isInteger(fieldType)){
						needAlter=true;
						convertDatas.remove(column.columnName);
					}
					break;
				case "real":
					if(!Reflect.isReal(fieldType)){
						needAlter=true;
						if(!Reflect.isInteger(fieldType))
							convertDatas.remove(column.columnName);
					}
					break;
				case "varchar":
					if(!Reflect.isVarchar(fieldType)){
						needAlter=true;
					}
					break;
				default:
					if(!field.getType().getName().equals(column.type)){
						needAlter=true;
						convertDatas.remove(column.columnName);
					}
					break;
			}
		}

		if(needAlter){
			//TODO 将整合好的数据进行处理，修改数据库表
		}
	}

	private DatabaseTable parse(SQLiteDatabase db,String tableName){
		Cursor cursor=db.rawQuery("select name,sql from sqlite_master where name='"+tableName+"'",null);
		if(cursor!=null){
			cursor.moveToFirst();
			final String name=cursor.getString(cursor.getColumnIndex("name"));
			String sql=cursor.getString(cursor.getColumnIndex("sql"));
			DatabaseTable dt=new DatabaseTable(name);
			sql=sql.substring(sql.indexOf('('));
			String[] ss=sql.split(",");

			for(String s:ss){
				DatabaseTable.DatabaseColumn column=new DatabaseTable.DatabaseColumn();
				column.columnName=s.substring(0, s.indexOf(' '));
				column.type=s.substring(s.indexOf(' '), s.indexOf(' ',2));
				column.isPrimaryKey=s.contains("primary");
				column.autoincrement=s.contains("autoincrement");
				dt.columnMap.put(column.columnName,column);
			}
			return dt;
		}
		return null;
	}

	/**
	 * 判断表是否存在
	 * @param tableName
	 * @return
	 */
	private boolean tableExists(String tableName){
		SQLiteDatabase db=mContext.openOrCreateDatabase(mConfig.getDatabaseName(), Context.MODE_PRIVATE, null);
		Cursor cursor=db.rawQuery("select name from sqlite_master where type='table' and name="+"'"+tableName+"'",null);
		if(cursor==null)
			return false;
		else
		    return true;
	}

	/**
	 * 判断表中是否有数据
	 * @param tableName
	 * @return
	 */
	private boolean tableHadData(String tableName){
		if(!tableExists(tableName))
			return false;
		SQLiteDatabase db=mContext.openOrCreateDatabase(mConfig.getDatabaseName(), Context.MODE_PRIVATE, null);
		Cursor cursor=db.rawQuery("select rootpage from sqlite_master where type='table' and name='"+tableName+"'",null);
		if(cursor!=null){
			cursor.moveToFirst();
			int page=cursor.getInt(cursor.getColumnIndex("rootpage"));
			if(page>0)
				return true;
		}
		return false;
	}

	public void switchDatabase(String databaseName){
		mConfig.switchDatabase(databaseName);
	}

	public DatabaseConfig getConfig(){
		return mConfig;
	}

	/**
	 * 数据库配置
	 */
	public class DatabaseConfig{
		private boolean isOutInformation;
		private int mVersion;
		private String mCurrentDatabase;

		/**
		 * 默认的数据库配置为
		 * 版本＝1
		 * 日志输出＝true
		 * 数据库名＝default_1.db
		 */
		private DatabaseConfig(){
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
			File file=mContext.getDatabasePath(mCurrentDatabase);
			mVersion=version;
			switchDatabase(mCurrentDatabase);
			if(file!=null&&file.exists())
				file.renameTo(new File(mCurrentDatabase));
		}
		public int getVersion(){
			return mVersion;
		}
	}
}