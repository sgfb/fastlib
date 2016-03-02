package com.fastlib.db;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

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
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import org.w3c.dom.Text;

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
	public <T> T get(Object obj){
		Field[] fields;
		Field fieldKey=null;

		try {
			fields=Reflect.getAllField(obj.getClass());
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
			fieldKey.setAccessible(true);
			Object key=fieldKey.get(obj);
			List<T> list= (List<T>) get(obj.getClass(),fieldKey.getName(), Reflect.objToStr(key));
			if(list==null||list.size()==0){
				if(mConfig.isOutInformation)
				    Log.w(TAG, "向数据库中请求了一个不存在的数据");
			}
			else
				return list.get(0);
		} catch (IllegalAccessException e){
			return null;
		}
		return null;
	}

	/**
	 * 返回某主键值为keyValue对象。使用这个方法对象类中必须有主键
	 *
	 * @param cla
	 * @param keyValue 主键值
	 * @return
	 */
	public <T> List<T> get(Class<T> cla,String keyValue){
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
		return get(cla, key, keyValue);
	}

	/**
	 * 获取对象集合.有where来指定过滤
	 *
	 * @param cla
	 * @param where
	 * @param whereValue
	 * @return
	 */
	public <T> List<T> get(Class<T> cla,String where,String whereValue){
		String tableName;
		SQLiteDatabase database=prepare(null);
		Cursor cursor;
		List<Object> list=new ArrayList<>();

		tableName=cla.getName();
		if(!tableExists(tableName)){
			Log.w(TAG,mConfig.getDatabaseName()+" 不存在表 "+tableName);
			return null;
		}
		if(TextUtils.isEmpty(where))
			cursor=database.rawQuery("select *from '"+tableName+"'",null);
		else {
			if(whereValue==null)
				cursor=database.rawQuery("select *from '"+tableName+"' where "+where+" is null",null);
			else
			    cursor = database.rawQuery("select *from '" + tableName + "' where " + where + "=?", new String[]{whereValue});
		}
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
		cursor.close();
		return List.class.cast(list);
	}

	public <T> List<T> getAll(Class<T> cla){
		return get(cla, null, null);
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
		return delete(obj.getClass(), columnName, columnValue);
	}

	/**
	 * 删除主键值为keyValue的数据，需要注解主键
	 * @param cla
	 * @param keyValue
	 * @return
	 */
	public boolean delete(Class<?> cla,String keyValue){
		//TODO
		return false;
	}

	/**
	 * 输出某条数据，以where为过滤条件
	 * @param cla
	 * @param where
	 * @param whereValue
	 * @return
	 */
	public boolean delete(Class<?> cla,String where,String whereValue){
		String tableName;
		Cursor cursor;
		SQLiteDatabase database;
		//查到对应行的总数
		int count=0;

		tableName=cla.getName();
		if(!tableExists(tableName)){
			Log.w(TAG,"数据库 "+mConfig.getDatabaseName()+"中不存在表 "+tableName);
			return false;
		}
		database=prepare(null);
		if(whereValue==null)
			cursor=database.rawQuery("select *from '"+tableName+"' where+"+where+" is null",null);
		else
		    cursor=database.rawQuery("select *from '" + tableName + "' where " + where+"=?",new String[]{whereValue});
		cursor.moveToFirst();
		if(cursor.isAfterLast()){
			Log.w(TAG,"表中不存在 "+where+"值为"+whereValue+"的数据");
			cursor.close();
			return false;
		}
		else{
			count=cursor.getCount();
			cursor.close();
			try{
				database.beginTransaction();
				database.execSQL("delete from '"+tableName+"' where "+where+"="+whereValue);
				database.setTransactionSuccessful();
				Log.i(TAG,mConfig.getDatabaseName()+"--d--"+Integer.toString(count)+"->"+where);
			}catch(SQLiteException e){
				return false;
			}finally{
				database.endTransaction();
			}
			return true;
	    }
	}

	/**
	 * 可以对无主键或者非主键查询的对象保存
	 *
	 * @param obj
	 * @param where
	 * @param whereValue
	 * @return
	 */
	public boolean update(@NonNull Object obj,String where,String whereValue){
		SQLiteDatabase database=prepare(null);
		String tableName;
		ContentValues cv=new ContentValues();
		Field[] fields;
		StringBuilder sb=new StringBuilder();

		tableName=obj.getClass().getName();
		//如果表不存在或者表中没有这条数据，则返回false
		if(!tableExists(tableName))
			return false;
		if(!tableHadData(tableName))
			return false;
		if(!TextUtils.isEmpty(where)){
			Cursor cursor;
			if(whereValue==null)
				cursor=database.rawQuery("select *from '"+tableName+"' where "+where+" is null",null);
			else
			    cursor=database.rawQuery("select *from '"+tableName+"' where "+where+"='"+whereValue+"'",null);
			if(cursor==null||cursor.isAfterLast())
				return false;
		}
		fields=obj.getClass().getDeclaredFields();

		for(Field field:fields){
			field.setAccessible(true);
			String type=field.getType().getSimpleName();
			DatabaseInject fieldInject=field.getAnnotation(DatabaseInject.class);
			String columnName;

			if(fieldInject!=null&&!TextUtils.isEmpty(fieldInject.columnName()))
				columnName=fieldInject.columnName();
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
			if(whereValue==null)
				database.update("'"+tableName+"'",cv,null,null);
			else
			    database.update("'" + tableName + "'",cv,where,new String[]{whereValue});
			database.setTransactionSuccessful();
			if(mConfig.isOutInformation)
				Log.d(TAG,mConfig.getDatabaseName()+"<--u-- "+tableName);
		}catch(SQLiteException e){
			return false;
		}finally{
			database.endTransaction();
		}
		return true;
	}

	private boolean save(Object obj){
		Field[] fields=Reflect.getAllField(obj.getClass());
		ContentValues cv=new ContentValues();
		SQLiteDatabase db=prepare(null);

		for(Field field:fields){
			field.setAccessible(true);
			String type=field.getType().getSimpleName();
			DatabaseInject fieldInject=field.getAnnotation(DatabaseInject.class);
			String columnName;

			if(fieldInject!=null&&fieldInject.ignore())
				continue;
			if(fieldInject!=null&&!TextUtils.isEmpty(fieldInject.columnName()))
				columnName=fieldInject.columnName();
			else
				columnName=field.getName();
			try{
				switch(type){
					case "boolean":
						cv.put(columnName, field.getBoolean(obj));
						break;
					case "short":
						cv.put(columnName, field.getShort(obj));
						break;
					case "int":
						cv.put(columnName,field.getInt(obj));
						break;
					case "long":
						cv.put(columnName,field.getLong(obj));
						break;
					case "float":
						cv.put(columnName, field.getFloat(obj));
						break;
					case "double":
						cv.put(columnName, field.getDouble(obj));
						break;
					case "char":
						char c=field.getChar(obj);
						if(c==0)
							cv.putNull(columnName);
						else
							cv.put(columnName, String.valueOf(c));
						break;
					case "String":
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
			db.beginTransaction();
			db.insert("'" + obj.getClass().getCanonicalName() + "'", null, cv);
			db.setTransactionSuccessful();
		}catch(SQLiteException e){
			return false;
		}
		finally {
			db.endTransaction();
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
		String tableName;
		boolean isUpdate=false;
		boolean success=true;

		DatabaseTable table=loadAttribute(obj.getClass());
		tableName=table.tableName;
		//如果表存在并且有主键，尝试获取这个对象，如果不是null则更新
		if(tableExists(tableName)){
			if(table.keyColumn!=null){
				Object data=get(obj);
				if(data!=null){
					isUpdate=true;
					try {
						Field field=obj.getClass().getDeclaredField(table.keyFieldName);
						field.setAccessible(true);
						update(obj,table.keyColumn.columnName, Reflect.objToStr(field.get(obj)));
					} catch (NoSuchFieldException e) {
						return false;
					} catch (IllegalAccessException e) {
						return false;
					}
				}
			}
		}

		if(!isUpdate){
			final String sql=generateCreateTableSql(obj.getClass());
			prepare(sql);
			success=save(obj);
		}

		if(mConfig.getIsOutInformation()&&success) {
			if(isUpdate)
				Log.d(TAG,mConfig.getDatabaseName()+"<--update--"+tableName);
			else
			    Log.d(TAG, mConfig.getDatabaseName() + "<------" + tableName);
		}
		return success;
	}

	/**
	 * 生成创建表sql语句
	 * @param cla
	 * @return
	 */
	private String generateCreateTableSql(Class<?> cla){
		StringBuilder sb=new StringBuilder();
		DatabaseTable table=loadAttribute(cla);

		sb.append("create table if not exists '" + table.tableName + "' (");

		Iterator<String> iter=table.columnMap.keySet().iterator();
		while(iter.hasNext()){
			String key=iter.next();
			DatabaseTable.DatabaseColumn column=table.columnMap.get(key);

			if(column.isIgnore)
				continue;
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
		Field[] fields=Reflect.getAllField(cla);
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
				if(fieldInject.keyPrimary()) {
					dt.keyColumn = column;
					dt.keyFieldName=f.getName();
				}
				if(!Reflect.isInteger(type)&&!Reflect.isReal(type)&&!Reflect.isVarchar(type))
					throw new UnsupportedOperationException("不支持数组或者引用类成为任何键");
				column.isPrimaryKey=fieldInject.keyPrimary();
				column.autoincrement=fieldInject.autoincrement();
				column.isIgnore=fieldInject.ignore();
			}
			dt.columnMap.put(f.getName(),column);
		}
		return dt;
	}

	private SQLiteDatabase prepare(final String sql) throws SQLiteException{
		SQLiteDatabase database;
		SQLiteOpenHelper helper=new SQLiteOpenHelper(mContext,mConfig.getDatabaseName(),null,mConfig.getVersion()){

			@Override
			public void onCreate(SQLiteDatabase db) {

			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion,
					int newVersion) {
				if(mConfig.isOutInformation)
					Log.d(TAG,"发现数据库版本需要升级，开始自动升级");
				updateDatabase(db);
				if(mConfig.isOutInformation)
					Log.d(TAG,"数据库升级完毕");
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
				cursor.moveToNext();
				if(tableName.equals("android_metadata"))
					continue;
				if(tableName.equals("sqlite_sequence"))
					continue;
				checkColumnChanged(db,tableName);
//				Cursor tableCursor=db.rawQuery("pragma table_info("+"'"+tableName+"')", null);
//				if(tableCursor!=null){
//					tableCursor.moveToFirst();
//					checkColumnChanged(db,tableName);
//				}
//				cursor.moveToNext();
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
		//保存需要修改并且保留数据的列
		List<String> convertDatas=new ArrayList<>();
		//临时列表，用于保存需要修改的列
		List<String> temp=new ArrayList<>();
		Map<String,String> newColumn=new HashMap<>();
		boolean needAlter=false;

		try {
			//如果对象类不存在则删除这张表
			cla=Class.forName(tableName);
		} catch (ClassNotFoundException e) {
			db.execSQL("drop table "+tableName);
			if(mConfig.isOutInformation)
				Log.d(TAG,"删除表"+tableName);
			return;
		}
		fields=Reflect.getAllField(cla);
		for(Field field:fields) {
			field.setAccessible(true);
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
			DatabaseInject inject;
			Field field=fieldMap.remove(key);

			//也许类中某字段被删除了
			if(field==null){
				temp.add(key);
				continue;
			}
			inject=field.getAnnotation(DatabaseInject.class);
			if(column.isPrimaryKey){
				if(inject==null||!inject.keyPrimary()) {
					temp.add(column.columnName);
				}
			}
			else{
				if(inject!=null&&inject.keyPrimary())
					temp.add(column.columnName);
			}
			if(column.autoincrement){
				if (inject==null||!inject.autoincrement()) {
					temp.add(column.columnName);
				}
			}
			else{
				if(inject!=null&&inject.autoincrement())
					temp.add(column.columnName);
			}
			String fieldType;
			//如果是数组，判断组类型
//			if(field.getClass().isArray())
//				fieldType=field.getClass().getComponentType().getSimpleName();
//			else
//			    fieldType=field.getType().getSimpleName();
			fieldType=field.getType().getSimpleName();
			switch (column.type){
				case "integer":
					if(!Reflect.isInteger(fieldType)){
						temp.add(column.columnName);
					}
					break;
				case "real":
					if(!Reflect.isReal(fieldType)){
						if(!Reflect.isInteger(fieldType)) {
							temp.add(column.columnName);
						}
					}
					break;
				case "varchar":
					if(!Reflect.isVarchar(fieldType)){
						temp.add(column.columnName);
					}
					break;
				default:
					if(!field.getType().getName().equals(column.type)){
						temp.add(column.columnName);
					}
					break;
			}
			if(!temp.contains(column.columnName))
				convertDatas.add(column.columnName);
		}
		iter=fieldMap.keySet().iterator();
		while(iter.hasNext()){
			String key=iter.next();
			Field value=fieldMap.get(key);
			String fieldType=value.getType().getSimpleName();
			newColumn.put(key,Reflect.toSQLType(fieldType));
		}
		if(temp.size()>0||newColumn.size()>0) {
			if(temp.size()<=0){
				convertDatas.clear();
				convertDatas = null;
			}
			alterTable(db, cla, convertDatas, newColumn);
		}
		else
			if(mConfig.isOutInformation)
				Log.d(TAG,"类对象:"+cla.getSimpleName()+" 不需要修改");
	}

	/**
	 * 对一个存在的表和对象进行修改
	 *
	 * @param cla
	 * @param valueToValue 保留的列数据，如果是null代表保留所有列数据
	 * @param newColumn 新列名与类型映射组
	 */
	private void alterTable(SQLiteDatabase db,Class<?> cla, List<String> valueToValue,Map<String,String> newColumn){
		String tableName=cla.getCanonicalName();
		String tempName="temp_table_"+Long.toString(System.currentTimeMillis());
		Iterator<String> iter;

		if(valueToValue==null){
			if(newColumn!=null&&newColumn.size()>0){
				iter=newColumn.keySet().iterator();
				while(iter.hasNext()){
					String column=iter.next();
					String type=newColumn.get(column);
					db.execSQL("alter table '"+tableName+"' add "+column+" "+type);
				}
				if(mConfig.isOutInformation)
					Log.d(TAG,"表"+tableName+"增加"+Integer.toString(newColumn.size())+"列");
			}
		}
		else{
			StringBuilder sb=new StringBuilder();
			iter=valueToValue.iterator();
			while(iter.hasNext()) {
				String key=iter.next();
				if(key.equals("id"))
					continue;
				sb.append(key + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			db.execSQL("alter table '"+tableName+"' rename to '"+tempName+"'");
			db.execSQL(generateCreateTableSql(cla));
			//注意主键值
			db.execSQL("insert into '" + tableName + "' (" + sb.toString() + ") select " + sb.toString() + " from " + tempName);
			db.execSQL("drop table " + tempName);
			if(mConfig.isOutInformation)
				Log.d(TAG,"表"+tableName+"被调整");
		}
	}

	private DatabaseTable parse(SQLiteDatabase db,String tableName){
		Cursor cursor=db.rawQuery("select name,sql from sqlite_master where name='"+tableName+"'",null);
		if(cursor!=null){
			cursor.moveToFirst();
			final String name=cursor.getString(cursor.getColumnIndex("name"));
			String sql=cursor.getString(cursor.getColumnIndex("sql"));
			DatabaseTable dt=new DatabaseTable(name);
			sql=sql.substring(sql.indexOf('(')+1,sql.length()-1);
			String[] ss=sql.split(",");

			for(String s:ss){
				DatabaseTable.DatabaseColumn column=new DatabaseTable.DatabaseColumn();
				s=s.trim();
				column.columnName=s.substring(0, s.indexOf(' '));
				column.type=s.substring(s.indexOf(' ')+1,s.length());
				column.isPrimaryKey=s.contains("primary");
				column.autoincrement=s.contains("autoincrement");
				dt.columnMap.put(column.columnName,column);
			}
			cursor.close();
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
		if(cursor==null||cursor.isAfterLast())
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
		 * 数据库名＝default.db
		 */
		private DatabaseConfig(){
			isOutInformation=true;
			mVersion=1;
			mCurrentDatabase=DEFAULT_DATABASE_NAME+".db";
		}

		public void setOutInfomation(boolean isOut){
			isOutInformation=isOut;
		}

		/**
		 * 切换数据库，如果不存在会在之后的操作中被创建
		 */
		public void switchDatabase(String databaseName){
			mCurrentDatabase=databaseName+".db";
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