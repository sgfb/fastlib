package com.fastlib.db;

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
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

/**
 * orm数据库.提供一些与数据库交互的基本操作
 * @author sgfb
 */
public class FastDatabase{
	public final String TAG=FastDatabase.class.getSimpleName();
	public static final String DEFAULT_DATABASE_NAME="default";

	private static final Object slock=new Object();
	private static FastDatabase sOwer;
	private static DatabaseConfig sConfig;
	private static DatabaseAttribute sAttri;
	private CustomUpdate mCustomUpdate;

	private Context mContext;

	private FastDatabase(Context context){
		this(context,null);
	}

	private FastDatabase(Context context,DatabaseConfig config){
		mContext=context.getApplicationContext();
		sAttri=new DatabaseAttribute();
		sConfig =config;
		if(sConfig ==null)
			sConfig =new DatabaseConfig();
	}

	public static void build(Context context){
		if(sOwer==null){
			synchronized(slock){
				sOwer=new FastDatabase(context);
			}
		}
	}

	public static FastDatabase getInstance(){
		sAttri.defaultAttribute();
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
				if(sConfig.isOutInformation)
					Log.w(TAG,"没有注解主键的对象无法使用get(Object obj)");
				return null;
			}
			fieldKey.setAccessible(true);
			Object key=fieldKey.get(obj);
			List<T> list= (List<T>) get(obj.getClass(),fieldKey.getName(), Reflect.objToStr(key));
			if(list==null||list.size()==0){
				if(sConfig.isOutInformation)
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
		SQLiteDatabase database;
		Cursor cursor;
		List<Object> list=new ArrayList<>();
		String order="";

		if(!TextUtils.isEmpty(sAttri.getOrderBy())){
			order="order by "+sAttri.getOrderBy()+" "+(sAttri.isAsc()?"asc":"desc");
		}
		tableName=cla.getName();
		if(!tableExists(tableName)){
			Log.w(TAG, sConfig.getDatabaseName()+" 不存在表 "+tableName);
			return null;
		}
		database=prepare(null);
		if(TextUtils.isEmpty(where))
			cursor=database.rawQuery("select *from '"+tableName+"' "+order,null);
		else {
			if(whereValue==null)
				cursor=database.rawQuery("select *from '"+tableName+"' where "+where+" is null "+order,null);
			else
			    cursor = database.rawQuery("select *from '" + tableName + "' where " + where + "=? "+order, new String[]{whereValue});
		}
		if(cursor==null) {
			if(sConfig.isOutInformation)
				Log.w(TAG,"请求的数据不存在数据库");
			database.close();
			return null;
		}
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
						field.set(obj,gson.fromJson(json,field.getType()));
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
						field.setBoolean(obj,value>0);
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
			} catch (Exception e){
				if(sConfig.isOutInformation)
					Log.w(TAG,"数据库在取数据时发送异常:"+e.toString());
				database.close();
				return null;
			}
		}
		cursor.close();
		database.close();
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
		int count;

		tableName=cla.getName();
		if(!tableExists(tableName)){
			Log.w(TAG, "数据库 " + sConfig.getDatabaseName() + "中不存在表 " + tableName);
			return false;
		}
		database=prepare(null);
		if(whereValue==null)
			cursor=database.rawQuery("select *from '"+tableName+"' where+"+where+" is null",null);
		else
		    cursor=database.rawQuery("select *from '" + tableName + "' where " + where+"=?",new String[]{whereValue});
		cursor.moveToFirst();
		if(cursor.isAfterLast()){
			Log.w(TAG,"表中不存在 "+where+"值为"+whereValue+ "的数据");
			cursor.close();
			database.close();
			return false;
		}
		else{
			count=cursor.getCount();
			cursor.close();
			try{
				database.beginTransaction();
				database.execSQL("delete from '"+tableName+"' where "+where+"="+whereValue);
				database.setTransactionSuccessful();
				Log.i(TAG, sConfig.getDatabaseName()+"--d--"+Integer.toString(count)+"->"+tableName);
			}catch(SQLiteException e){
				return false;
			}finally{
				database.endTransaction();
				database.close();
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
		SQLiteDatabase database;
		String tableName;
		ContentValues cv=new ContentValues();
		Field[] fields;

		tableName=obj.getClass().getName();
		//如果表不存在或者表中没有这条数据，则返回false
		if(!tableExists(tableName)) {
			if(sConfig.isOutInformation)
				Log.d(TAG,"更新数据失败，表不存在");
			return false;
		}
		if(!tableHadData(tableName)) {
			if(sConfig.isOutInformation)
				Log.d(TAG,"更新数据失败，表中不含如何数据");
			return false;
		}
		database=prepare(null);
		if(!TextUtils.isEmpty(where)){
			Cursor cursor;
			if(whereValue==null)
				cursor=database.rawQuery("select *from '"+tableName+"' where "+where+" is null",null);
			else
			    cursor=database.rawQuery("select *from '"+tableName+"' where "+where+"=?",new String[]{whereValue});
			if(cursor==null||cursor.isAfterLast()) {
				if(sConfig.isOutInformation)
					Log.d(TAG,"更新数据失败，没有找到要更新的数据");
				if(cursor!=null)
					cursor.close();
				database.close();
				return false;
			}
			cursor.close();
		}
		fields=Reflect.getAllField(obj.getClass());

		for(Field field:fields){
			field.setAccessible(true);
			String type=field.getType().getSimpleName();
			DatabaseInject fieldInject=field.getAnnotation(DatabaseInject.class);
			String columnName;

			if(fieldInject!=null&&!TextUtils.isEmpty(fieldInject.columnName()))
				columnName=fieldInject.columnName();
			else
				columnName=field.getName();
			//自动增长主键过滤
			if(fieldInject!=null&&fieldInject.keyPrimary()&&fieldInject.autoincrement())
				continue;
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
				database.close();
				return false;
			}
		}

		try{
			database.beginTransaction();
			if(whereValue==null)
				database.update("'"+tableName+"'",cv,null,null);
			else
			    database.update("'" + tableName + "'",cv,where+"=?",new String[]{whereValue});
			database.setTransactionSuccessful();
			if(sConfig.isOutInformation)
				Log.d(TAG, sConfig.getDatabaseName()+"<--u-- "+tableName);
		}catch(SQLiteException e){
			if(sConfig.isOutInformation)
				Log.d(TAG,"更新数据失败，异常："+e.toString());
			return false;
		}finally{
			database.endTransaction();
			database.close();
		}
		return true;
	}

	/**
	 * 保存对象到数据库
	 * @param obj
	 * @return
	 */
	private boolean save(Object obj){
		Field[] fields=Reflect.getAllField(obj.getClass());
		ContentValues cv=new ContentValues();
		SQLiteDatabase db=prepare(null);
		String tableName=obj.getClass().getName();

		if(sAttri.getSaveMax()< Integer.MAX_VALUE){
			List<?> list=getAll(obj.getClass());
			if(list!=null&&list.size()>=sAttri.getSaveMax()){
				delete(list.get(0));
			}
		}
		for(Field field:fields){
			field.setAccessible(true);
			String type=field.getType().getSimpleName();
			DatabaseInject fieldInject=field.getAnnotation(DatabaseInject.class);
			String columnName;

			if(fieldInject!=null&&fieldInject.ignore())
				continue;
			try {
				if (fieldInject != null && fieldInject.keyPrimary() && fieldInject.autoincrement()) {
					int keyValue = field.getInt(obj);
					if (keyValue <= 0)
						continue;
				}
				if (fieldInject != null && !TextUtils.isEmpty(fieldInject.columnName()))
					columnName = fieldInject.columnName();
				else
					columnName = field.getName();
				if(columnName.contains("this"))
					continue;
				switch (type) {
					case "boolean":
						cv.put(columnName, field.getBoolean(obj));
						break;
					case "short":
						cv.put(columnName, field.getShort(obj));
						break;
					case "int":
						cv.put(columnName, field.getInt(obj));
						break;
					case "long":
						cv.put(columnName, field.getLong(obj));
						break;
					case "float":
						cv.put(columnName, field.getFloat(obj));
						break;
					case "double":
						cv.put(columnName, field.getDouble(obj));
						break;
					case "char":
						char c = field.getChar(obj);
						if (c == 0)
							cv.putNull(columnName);
						else
							cv.put(columnName, String.valueOf(c));
						break;
					case "String":
						String s = (String) field.get(obj);
						if (s == null)
							cv.putNull(columnName);
						else
							cv.put(columnName, s);
						break;
					default:
						Object pre = field.get(obj);
						Gson gson = new Gson();
						String json = gson.toJson(pre);

						if (pre == null)
							cv.putNull(columnName);
						else
							cv.put(columnName, json);
						break;
				}
			}catch(IllegalAccessException | IllegalArgumentException e){
				return false;
			}
		}
		try{
			db.beginTransaction();
			db.insert("'" + tableName + "'", null, cv);
			db.setTransactionSuccessful();
			if(sConfig.isOutInformation)
				Log.d(TAG, sConfig.getDatabaseName()+"<----"+tableName);
		}catch(SQLiteException e){
			return false;
		}
		finally {
			db.endTransaction();
			db.close();
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
		//如果表存在并且有主键，尝试获取这个对象，如果不是null并且主键值不为0则尝试更新
		if(tableExists(tableName)){
			DatabaseTable.DatabaseColumn keyColumn=table.keyColumn;
			if(keyColumn!=null){
				Field field;
				try {
					field = obj.getClass().getDeclaredField(table.keyFieldName);
					field.setAccessible(true);
					if(Reflect.isInteger(field.getType().getSimpleName())){
						int value=field.getInt(obj);
						if(value>0){
							Object data=get(obj);
							if(data!=null){
								isUpdate = true;
								update(obj,table.keyColumn.columnName,Reflect.objToStr(field.get(obj)));
							}
						}
					}
					else{
						Object data=get(obj);
						if(data!=null){
							isUpdate = true;
							update(obj,table.keyColumn.columnName,Reflect.objToStr(field.get(obj)));
						}
					}
				} catch (NoSuchFieldException e) {
					if(sConfig.isOutInformation)
						Log.w(TAG,"数据库saveOrUpdate时出现异常:"+e.toString());
					return false;
				} catch (IllegalAccessException e) {
					if(sConfig.isOutInformation)
						Log.w(TAG,"数据库saveOrUpdate时出现异常:"+e.toString());
					return false;
				}
			}
		}

		if(!isUpdate){
			final String sql=generateCreateTableSql(obj.getClass());
			SQLiteDatabase db=prepare(sql);
			db.close();
			success=save(obj);
		}
		return success;
	}

	/**
	 * 根据对象类删除表
	 * @param database 某数据库
	 * @param cla 对象类
	 */
	public void dropTable(String database,Class<?> cla){
		String table=cla.getCanonicalName();
		dropTable(database,table);
	}

	/**
	 * 删除表
	 * @param database 某数据库
	 * @param table 表名
	 */
	public void dropTable(String database,String table){
		SQLiteDatabase db=mContext.openOrCreateDatabase(database, Context.MODE_PRIVATE, null);
		if(tableExists(table)) {
			db.execSQL("drop table '" + table+"'");
			if(sConfig.isOutInformation)
			    Log.d(TAG,"删除表"+table);
		}
		else
		if(sConfig.isOutInformation)
			Log.d(TAG,"表"+table+"不存在");
	}

	public FastDatabase orderBy(boolean asc,String columnName){
		sAttri.setAsc(asc);
		sAttri.setOrderBy(columnName);
		return sOwer;
	}

	public FastDatabase limit(int limit){
		sAttri.setLimit(limit);
		return sOwer;
	}

	public FastDatabase maxSave(int max){
		sAttri.setSaveMax(max);
		return sOwer;
	}

	public FastDatabase toWhichDatabase(String databaseName){
		sAttri.setToDatabase(databaseName);
		return sOwer;
	}

	public void setCustomUpdate(CustomUpdate custom){
		mCustomUpdate=custom;
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
			if(column.columnName.contains("this"))
				continue;
			sb.append(column.columnName)
					.append(" " + column.type);
			if(column.isPrimaryKey)
				sb.append(" primary key");
			if(column.autoincrement) {
				if(!column.type.equals("integer"))
					throw new RuntimeException("自动增长只能用于int型数据");
				sb.append(" autoincrement");
			}
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
				type=f.getType().getName();
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
               final String databaseName=TextUtils.isEmpty(sAttri.getToDatabase())?sConfig.getDatabaseName():sAttri.getToDatabase();
		SQLiteOpenHelper helper=new SQLiteOpenHelper(mContext,databaseName,null, sConfig.getVersion()){

			@Override
			public void onCreate(SQLiteDatabase db) {

			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion,
								  int newVersion) {
				if(sConfig.isOutInformation)
					Log.d(TAG,"发现数据库版本需要升级，开始自动升级");
				if(mCustomUpdate!=null){
					if(sConfig.isOutInformation)
						Log.d(TAG,"使用自定义升级方案");
					mCustomUpdate.update(db,oldVersion,newVersion);
				}
				else
					updateDatabase(db);
				if(sConfig.isOutInformation)
					Log.d(TAG,"数据库升级完毕");
			}
		};

		database=helper.getWritableDatabase();
		if(!TextUtils.isEmpty(sql)) {
			try{
				database.execSQL(sql);
			}catch(SQLiteException e){
				Log.w(TAG,"prepare时异常:"+e.getMessage());
			}
		}
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
				//无视特殊表
				if(tableName.equals("android_metadata"))
					continue;
				if(tableName.equals("sqlite_sequence"))
					continue;
				checkTableChanged(db, tableName);
			}
			cursor.close();
		}
	}

	/**
	 * 检查表对类映射.如果增加新列可以直接操作，但是如果是更改主键或者修改列类型删除列就需要表重建
	 * @param tableName
	 */
	private void checkTableChanged(SQLiteDatabase db, String tableName){
		Class<?> cla;
		Field[] fields;
		Map<String,Field> fieldMap=new HashMap<>();
		List<String> convertDatas=new ArrayList<>(); //保存需要修改并且保留数据的列
		List<String> needChangeColumn=new ArrayList<>();
		Map<String,String> newColumn=new HashMap<>();

		try {
			//如果对象类不存在则删除这张表
			cla=Class.forName(tableName);
		} catch (ClassNotFoundException e) {
			db.execSQL("drop table '" + tableName+"'");
			if(sConfig.isOutInformation)
				Log.d(TAG,"删除表"+tableName);
			return;
		}
		fields=Reflect.getAllField(cla);
		for(Field field:fields) {
			field.setAccessible(true);
			String columnName=field.getName(); //列名以注解为优先，默认字段名
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
				needChangeColumn.add(key);
				continue;
			}
			//判断注解是否被修改
			inject=field.getAnnotation(DatabaseInject.class);
			if(column.isPrimaryKey){
				if(inject==null||!inject.keyPrimary()) {
					needChangeColumn.add(column.columnName);
				}
			}
			else{
				if(inject!=null&&inject.keyPrimary())
					needChangeColumn.add(column.columnName);
			}
			if(column.autoincrement){
				if (inject==null||!inject.autoincrement()) {
					needChangeColumn.add(column.columnName);
				}
			}
			else{
				if(inject!=null&&inject.autoincrement())
					needChangeColumn.add(column.columnName);
			}
			String fieldType;
			//如果是数组，判断组类型
//			if(field.getClass().isArray())
//				fieldType=field.getClass().getComponentType().getSimpleName();
//			else
//			    fieldType=field.getType().getSimpleName();
			//判断类型是否被修改.integer改为任何类型都可以被兼容,real只被varchar兼容,varchar被修改不兼容其他类型
			fieldType=field.getType().getSimpleName();
			switch (column.type){
				case "integer":
					if(!Reflect.isInteger(fieldType)){
						needChangeColumn.add(column.columnName);
						convertDatas.add(column.columnName);
					}
					break;
				case "real":
					if(!Reflect.isReal(fieldType)&&Reflect.isVarchar(fieldType)){
						needChangeColumn.add(column.columnName);
						convertDatas.add(column.columnName);
					}
					break;
				case "varchar":
					if(!Reflect.isVarchar(fieldType))
						needChangeColumn.add(column.columnName);
					break;
				default:
					if(!field.getType().getName().equals(column.type))
						needChangeColumn.add(column.columnName);
					break;
			}
		}
		iter=fieldMap.keySet().iterator();
		while(iter.hasNext()){
			String key=iter.next();
			if(key.contains("this"))
				continue;
			Field value=fieldMap.get(key);
			String fieldType=value.getType().getSimpleName();
			newColumn.put(key,Reflect.toSQLType(fieldType));
		}
		if(needChangeColumn.size()>0||newColumn.size()>0){
			boolean needChangeTable=(needChangeColumn.size()>0); //列被删除或字段类型被修改时重置表
			alterTable(db, cla, convertDatas, newColumn,needChangeTable);
		}
		else
			if(sConfig.isOutInformation)
				Log.d(TAG,"表 "+tableName+" 不需要修改");
	}

	/**
	 * 对一个存在的表进行修改
	 *
	 * @param cla
	 * @param valueToValue 保留列和数据
	 * @param newColumn 新列名与类型映射组
	 * @param needDelete 是否需要重建表
	 */
	public void alterTable(SQLiteDatabase db,Class<?> cla,List<String> valueToValue,Map<String,String> newColumn,boolean needDelete){
		String tableName=cla.getName();
		String tempName="temp_table_"+Long.toString(System.currentTimeMillis()); //数据转移用临时表
		Iterator<String> iter;

		if(!needDelete){
			if(newColumn!=null&&newColumn.size()>0){
				iter=newColumn.keySet().iterator();
				while(iter.hasNext()){
					String column=iter.next();
					String type=newColumn.get(column);
					db.execSQL("alter table '"+tableName+"' add "+column+" "+type);
				}
				if(sConfig.isOutInformation)
					Log.d(TAG,"表"+tableName+"增加"+Integer.toString(newColumn.size())+"列");
			}
		}
		else{
			if(valueToValue!=null&&valueToValue.size()>0){
				StringBuilder sb=new StringBuilder();
				iter=valueToValue.iterator();
				while(iter.hasNext()) {
					String key=iter.next();
					if(key.equals("id"))
						continue;
					sb.append(key + ",");
				}
				if(valueToValue.size()>0)
					sb.deleteCharAt(sb.length() - 1);
				db.execSQL("alter table '"+tableName+"' rename to '"+tempName+"'");
				db.execSQL(generateCreateTableSql(cla));
				//注意主键值
				db.execSQL("insert into '" + tableName + "' (" + sb.toString() + ") select " + sb.toString() + " from " + tempName);
				db.execSQL("drop table " + tempName);
			}
			else{
				db.execSQL("drop table '"+tableName+"'");
				db.execSQL(generateCreateTableSql(cla));
			}
			if(sConfig.isOutInformation)
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
		SQLiteDatabase db=prepare(null);
		Cursor cursor=db.rawQuery("select name from sqlite_master where type='table' and name=" + "'" + tableName + "'", null);
		boolean exists=!(cursor==null||cursor.getCount()<=0);

		if(cursor!=null)
		    cursor.close();
		db.close();
		return exists;
	}

	/**
	 * 判断表中是否有数据
	 * @param tableName
	 * @return
	 */
	private boolean tableHadData(String tableName){
		if(!tableExists(tableName))
			return false;
		SQLiteDatabase db=prepare(null);
		Cursor cursor=db.rawQuery("select rootpage from sqlite_master where type='table' and name='"+tableName+"'",null);
		if(cursor!=null){
			cursor.moveToFirst();
			int page=cursor.getInt(cursor.getColumnIndex("rootpage"));
			cursor.close();
			db.close();
			if(page>0)
				return true;
		}
		db.close();
		return false;
	}

	public void switchDatabase(String databaseName){
		sConfig.switchDatabase(databaseName);
	}

	public DatabaseConfig getConfig(){
		return sConfig;
	}

	/**
	 * 数据库配置
	 */
	public class DatabaseConfig{
		private boolean isOutInformation;
		private int mVersion;
		private String mCurrentDatabase;
		private String mPrefix;

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
			if(mPrefix==null)
				mPrefix="";
			mCurrentDatabase=mPrefix+databaseName+".db";
		}

		public String getDatabaseName(){
			return mCurrentDatabase;
		}

		public boolean getIsOutInformation(){
			return isOutInformation;
		}

		public void setVersion(int version){
			if(version<mVersion)
				throw new IllegalArgumentException("设置的版本小于等于当前版本");
			mVersion=version;
		}

		public int getVersion(){
			return mVersion;
		}

		public String getPrefix() {
			return mPrefix;
		}

		public void setPrefix(String prefix) {
			mPrefix = prefix;
		}
	}
}