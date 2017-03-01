package com.fastlib.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fastlib.annotation.Database;
import com.fastlib.app.GlobalConfig;
import com.fastlib.bean.DatabaseTable;
import com.fastlib.net.NetQueue;
import com.fastlib.utils.Reflect;
import com.google.gson.Gson;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

/**
 * 封装一些与数据库交互的基本操作
 * @author sgfb
 */
public class FastDatabase{
    public final String TAG = FastDatabase.class.getSimpleName();
    public static final String DEFAULT_DATABASE_NAME = "default";

    private static DatabaseConfig sConfig=new DatabaseConfig();
    private CustomUpdate mCustomUpdate;
    private Context mContext;
    private RuntimeAttribute mAttribute;

    public FastDatabase(Context context){
        mContext = context.getApplicationContext();
        mAttribute = new RuntimeAttribute();
    }

    /**
     * 返回公共数据库
     * @param context
     * @return
     */
    public static FastDatabase getDefaultInstance(Context context) {
        FastDatabase database = new FastDatabase(context);
        database.toWhichDatabase(getDefaultDatabaseName());
        return database;
    }

    /**
     * 异步获取数据库请求第一条纪录
     *
     * @param cla
     * @param listener
     * @param <T>
     */
    public <T> void getFirstAsync(final Class<T> cla, final DatabaseListener<T> listener) {
        NetQueue.sRequestPool.execute(new Runnable() {
            @Override
            public void run(){
                Handler handle=new Handler(Looper.getMainLooper());
                final T data=getFirst(cla);
                handle.post(new Runnable() {
                    @Override
                    public void run(){
                        listener.onResult(data);
                    }
                });
            }
        });
    }

    /**
     * 获取数据库请求第一条记录
     * @param cla
     * @param <T>
     * @return
     */
    public <T> T getFirst(Class<T> cla) {
        List<T> all = limit(0, 1).get(cla);
        if (all != null && !all.isEmpty())
            return all.get(0);
        return null;
    }

    /**
     * 异步获取数据库记录
     * @param cla
     * @param listener
     * @param <T>
     */
    public <T> void getAsync(final Class<T> cla,final DatabaseListListener<T> listener){
        NetQueue.sRequestPool.execute(new Runnable(){
            @Override
            public void run(){
                Handler handler=new Handler(Looper.getMainLooper());
                final List<T> list=get(cla);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onResult(list);
                    }
                });
            }
        });
    }

    /**
     * 获取表中数据
     *
     * @param cla
     * @return
     */
    public <T> List<T> get(Class<T> cla) {
        String tableName = cla.getCanonicalName();
        if (!tableExists(tableName)){
            Log.w(TAG, sConfig.getDatabaseName() + " 不存在表 " + tableName);
            return null;
        }
        SQLiteDatabase database;
        Cursor cursor;
        String order = "";
        String selectColumn = getSelectColumn(cla);
        String filters; //额外过滤条件
        String condition = ""; //基础过滤条件
        String key = getKeyName(cla);
        List<String> selectionArgs = new ArrayList<>();
        List<T> list = new ArrayList<>();

        //调整表名,特殊内部嵌套类
        tableName = tableName.replace("$", ".");
        filters = getFilters(key, mAttribute.getFilterCommand(), selectionArgs);
        //排序条件
        if (!TextUtils.isEmpty(mAttribute.getOrderBy()))
            order = "order by " + mAttribute.getOrderBy() + " " + (mAttribute.isAsc() ? "asc" : "desc");
        else{
            //如果请求倒序并且没有指定属性，默认主键倒序(如果主键存在的话)
            if(!mAttribute.isAsc()&&!TextUtils.isEmpty(key))
                order="order by "+key+" "+(mAttribute.isAsc()?"asc":"desc");
        }
        database = prepare(null);
        String complete = "select " + selectColumn + " from '" + tableName + "' " + condition + filters + " " + order + " limit " + mAttribute.getStart() + "," + mAttribute.getEnd() + " ";
        String[] args = selectionArgs.isEmpty() ? null : selectionArgs.toArray(new String[]{});
        cursor = database.rawQuery(complete, args);
        if (cursor == null) {
            if (GlobalConfig.SHOW_LOG)
                Log.w(TAG, "请求的数据不存在数据库");
            database.close();
            return null;
        }
        cursor.moveToFirst();
        Gson gson = new Gson();
        while (!cursor.isAfterLast()) {
            try {
                T obj = cla.newInstance();
                Field[] fields = cla.getDeclaredFields();
                for (Field field : fields) {
                    Database inject = field.getAnnotation(Database.class);
                    field.setAccessible(true);
                    String type = field.getType().getSimpleName();
                    int columnIndex = cursor.getColumnIndex(field.getName());

                    if (columnIndex == -1)
                        continue;
                    if (inject != null && inject.ignore()) //跳过忽视字段
                        continue;
                    if (type.contains("this"))
                        continue;
                    if (type.contains("$"))
                        continue;
                    if (field.getType().isArray()) {
                        String json = cursor.getString(columnIndex);
                        field.set(obj, gson.fromJson(json, field.getType()));
                        continue;
                    }

                    switch (type) {
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
                            int value = cursor.getInt(columnIndex);
                            field.setBoolean(obj, value > 0);
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
                            String json = cursor.getString(columnIndex);
                            try {
                                Object preObj = gson.fromJson(json, field.getType());
                                field.set(obj, preObj);
                            } catch (RuntimeException e) {
                                continue;
                            }
                            break;
                    }
                }
                list.add(obj);
                cursor.moveToNext();
            } catch (Exception e) {
                if (GlobalConfig.SHOW_LOG)
                    Log.w(TAG, "数据库在取数据时发生异常:" + e.toString());
                database.close();
                return null;
            }
        }
        cursor.close();
        database.close();
        return list;
    }

    /**
     * 删除对象(obj对象必需有主键)
     *
     * @param obj
     * @return
     */
    public boolean delete(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        Field primaryField = null;
        String columnValue;

        //是否有主键
        for (Field field : fields) {
            field.setAccessible(true);
            Database tableInject = field.getAnnotation(Database.class);
            if (tableInject != null && tableInject.keyPrimary()) {
                primaryField = field;
                break;
            }
        }

        if (primaryField != null) {
            try {
                switch (primaryField.getType().getSimpleName()) {
                    case "short":
                        columnValue = Short.toString(primaryField.getShort(obj));
                        break;
                    case "int":
                        columnValue = Integer.toString(primaryField.getInt(obj));
                        break;
                    case "String":
                        columnValue = (String) primaryField.get(obj);
                        break;
                    case "long":
                        columnValue = Long.toString(primaryField.getLong(obj));
                        break;
                    case "float":
                        columnValue = Float.toString(primaryField.getFloat(obj));
                        break;
                    case "double":
                        columnValue = Double.toString(primaryField.getDouble(obj));
                        break;
                    default:
                        Log.w(TAG, "不支持 short,int,long,String,float,double 之外的类型做为主键");
                        return false;
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            Log.w(TAG, "错误的使用了delete(Object obj),obj没有注解主键");
            return false;
        }
        mAttribute.setFilterCommand(new And(FilterCondition.equal(columnValue)));
        return delete(obj.getClass());
    }

    /**
     * 删除数据
     *
     * @param cla
     * @return
     */
    public boolean delete(Class<?> cla) {
        String tableName = cla.getName();
        if (!tableExists(tableName)) {
            Log.w(TAG, "数据库 " + sConfig.getDatabaseName() + "中不存在表 " + tableName);
            return false;
        }
        Cursor cursor;
        SQLiteDatabase database;
        String filters;
        String key = getKeyName(cla);
        List<String> selectionArgs = new ArrayList<>();
        int count; //查到对应行的总数

        tableName = tableName.replace("$", ".");
        filters = getFilters(key, mAttribute.getFilterCommand(), selectionArgs);
        database = prepare(null);
        String complete = "select *from '" + tableName + "' " + filters + " limit " + mAttribute.getStart() + "," + mAttribute.getEnd() + " ";
        String[] args = selectionArgs.isEmpty() ? null : selectionArgs.toArray(new String[]{});
        cursor = database.rawQuery(complete, args);
        cursor.moveToFirst();
        if (cursor.isAfterLast()) {
            Log.w(TAG, "表中不存在要删除的数据");
            cursor.close();
            database.close();
            return false;
        } else {
            count = cursor.getCount();
            cursor.close();
            try {
                database.beginTransaction();
                String deleteCommand = "delete from '" + tableName + "' " + filters;
                if (args != null)
                    for (String replaceStr : args)
                        deleteCommand = deleteCommand.replaceFirst("[?]", "'" + replaceStr + "'");
                database.execSQL(deleteCommand);
                database.setTransactionSuccessful();
                Log.i(TAG, (TextUtils.isEmpty(mAttribute.getWhichDatabase()) ? sConfig.getDatabaseName() : mAttribute.getWhichDatabase()) + "--d--" + Integer.toString(count) + "->" + tableName);
            } catch (SQLiteException e) {
                return false;
            } finally {
                database.endTransaction();
                database.close();
            }
            return true;
        }
    }

    /**
     * 更新数据
     *
     * @param obj
     * @return 是否成功更新
     */
    public boolean update(@NonNull Object obj) {
        SQLiteDatabase database;
        String tableName;
        String filter;
        ContentValues cv = new ContentValues();
        Field[] fields;
        List<String> args = new ArrayList<>();
        String[] ss;
        int count; //总更新了多少数据

        tableName = obj.getClass().getCanonicalName();
        //如果表不存在或者表中没有这条数据，则返回false
        if (!tableExists(tableName)) {
            if (GlobalConfig.SHOW_LOG)
                Log.d(TAG, "更新数据失败，表不存在");
            return false;
        }
        if (!tableHadData(tableName)) {
            if (GlobalConfig.SHOW_LOG)
                Log.d(TAG, "更新数据失败，表中不含如何数据");
            return false;
        }
        database = prepare(null);
        //先检测数据是否存在
        filter = getFilters(getKeyName(obj.getClass()), mAttribute.getFilterCommand(), args);
        Cursor cursor = database.rawQuery("select *from '" + tableName + "'" + filter, ss = args.toArray(new String[]{}));
        cursor.moveToFirst();
        if (cursor.isAfterLast()) {
            if (GlobalConfig.SHOW_LOG)
                Log.d(TAG, "更新数据失败,没有找到要更新的数据");
            cursor.close();
            database.close();
            return false;
        }
        count = cursor.getCount();
        cursor.close();
        //开始遍历所有字段来更新数据
        fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String type = field.getType().getSimpleName();
            Database fieldInject = field.getAnnotation(Database.class);
            String columnName;

            if (fieldInject != null && !TextUtils.isEmpty(fieldInject.columnName()))
                columnName = fieldInject.columnName();
            else
                columnName = field.getName();
            //自动增长主键过滤
            if (fieldInject != null && fieldInject.keyPrimary() && fieldInject.autoincrement())
                continue;
            if (columnName.contains("this"))
                continue;
            if (columnName.contains("$"))
                continue;
            try {
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
            } catch (IllegalAccessException | IllegalArgumentException e) {
                database.close();
                return false;
            }
        }

        try {
            //削掉前面的where
            filter = filter.substring(6, filter.length());
            database.beginTransaction();
            database.update("'" + tableName + "'", cv, filter, ss);
            database.setTransactionSuccessful();
            if (GlobalConfig.SHOW_LOG)
                Log.d(TAG, (TextUtils.isEmpty(mAttribute.getWhichDatabase()) ? sConfig.getDatabaseName() : mAttribute.getWhichDatabase()) + "<--u-" + count + "- " + tableName);
        } catch (SQLiteException e) {
            if (GlobalConfig.SHOW_LOG)
                Log.d(TAG, "更新数据失败，异常：" + e.toString());
            return false;
        } finally {
            database.endTransaction();
            database.close();
        }
        return true;
    }

    /**
     * 保存对象到数据库
     *
     * @param array
     * @return
     */
    private boolean save(Object[] array) {
        if (array == null || array.length <= 0)
            return false; //没什么对象可存应该返回false的吗？
        Object availableObj = null;

        for (Object obj : array) //取首个非null对象
            if (obj != null) {
                availableObj = obj;
                break;
            }
        if (availableObj == null)
            return false;
        SQLiteDatabase db = prepare(null);
        ContentValues cv = new ContentValues();
        Field[] fields = availableObj.getClass().getDeclaredFields();
        String tableName = availableObj.getClass().getName();

        try {
            db.beginTransaction();
            for (Object obj : array) {
                if (obj == null) //跳过null对象
                    continue;
                cv.clear();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Database fieldInject = field.getAnnotation(Database.class);
                    String columnName;
                    Class<?> type=field.getType();

                    if (fieldInject != null && fieldInject.ignore())
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
                        if (columnName.contains("this"))
                            continue;
                        if (columnName.contains("$"))
                            continue;
                        if(type==boolean.class)
                            cv.put(columnName,field.getBoolean(obj));
                        else if(type==int.class)
                            cv.put(columnName,field.getInt(obj));
                        else if(type==long.class)
                            cv.put(columnName,field.getLong(obj));
                        else if(type==float.class)
                            cv.put(columnName,field.getFloat(obj));
                        else if(type==double.class)
                            cv.put(columnName,field.getDouble(obj));
                        else if(type==char.class){
                            char c = field.getChar(obj);
                            if (c == 0)
                                cv.putNull(columnName);
                            else
                                cv.put(columnName, String.valueOf(c));
                        }
                        else if(type==short.class)
                            cv.put(columnName,field.getShort(obj));
                        else if(type==String.class)
                            cv.put(columnName,(String)field.get(obj));
                        else if(type==byte.class)
                            cv.put(columnName,field.getByte(obj));
                        else if(type==byte[].class)
                            cv.put(columnName,(byte[])field.get(obj));
                        else{
                            Object pre = field.get(obj);
                            Gson gson = new Gson();
                            String json = gson.toJson(pre);

                            if (pre == null)
                                cv.putNull(columnName);
                            else
                                cv.put(columnName, json);
                        }
                    } catch (IllegalAccessException | IllegalArgumentException e){
                        if(GlobalConfig.SHOW_LOG)
                            Log.d(TAG,"更新数据失败:"+e.getMessage());
                        return false;
                    }
                }
                db.insert("'" + tableName + "'", null, cv);
                if (GlobalConfig.SHOW_LOG)
                    Log.d(TAG, (TextUtils.isEmpty(mAttribute.getWhichDatabase()) ? sConfig.getDatabaseName() : mAttribute.getWhichDatabase()) + "<----" + tableName);
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e){
            if(GlobalConfig.SHOW_LOG)
                Log.d(TAG,"更新数据失败:"+e.getMessage());
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
        return true;
    }

    /**
     * 判断再集中后存储
     *
     * @param objs
     * @return
     */
    private boolean saveOrUpdate(Object[] objs) {
        Object obj = null;
        String tableName;
        boolean isUpdate = false;
        boolean success = true;

        if (objs == null || objs.length <= 0)
            return false;
        for (Object object : objs) {
            if (object != null) {
                obj = object;
                break;
            }
        }
        //有可能一个数组全是null,这种情况直接跳出
        if (obj == null)
            return false;
        DatabaseTable table = loadAttribute(obj.getClass());
        tableName = table.tableName;
        //如果表存在并且有主键，尝试获取这个对象，如果不是null(如果是整型且值不为0)则尝试更新
        if (tableExists(tableName)) {
            DatabaseTable.DatabaseColumn keyColumn = table.keyColumn;
            if (keyColumn != null) {
                Field field;
                try {
                    field = obj.getClass().getDeclaredField(table.keyFieldName);
                    field.setAccessible(true);
                    Object keyValue = field.get(obj);
                    if (field.getType()==int.class||field.getType()==long.class){
                        long lKeyValue=field.getType()==int.class?(int)keyValue:(long)keyValue;
                        if (lKeyValue > 0) {
                            mAttribute.setFilterCommand(new And(FilterCondition.equal(keyValue.toString())));
                            List<?> data = get(obj.getClass());
                            mAttribute.setFilterCommand(null);
                            if (data != null && data.size() > 0) {
                                isUpdate = true;
                                success = addFilter(new And(FilterCondition.equal(Reflect.objToStr(keyValue)))).update(obj);
                            }
                        }
                    } else {
                        mAttribute.setFilterCommand(new And(FilterCondition.equal(keyValue.toString())));
                        List<?> data = get(obj.getClass());
                        mAttribute.setFilterCommand(null);
                        if (data != null && data.size() > 0) {
                            isUpdate = true;
                            success = addFilter(new And(FilterCondition.equal(Reflect.objToStr(keyValue)))).update(obj);
                        }
                    }
                } catch (NoSuchFieldException e) {
                    if (GlobalConfig.SHOW_LOG)
                        Log.w(TAG, "数据库saveOrUpdate时出现异常:" + e.toString());
                    return false;
                } catch (IllegalAccessException e) {
                    if (GlobalConfig.SHOW_LOG)
                        Log.w(TAG, "数据库saveOrUpdate时出现异常:" + e.toString());
                    return false;
                }
            }
        }

        if (!isUpdate) {
            final String sql = generateCreateTableSql(obj.getClass());
            SQLiteDatabase db = prepare(sql);
            db.close();
            success = save(objs);
        }
        return success;
    }


    /**
     * 保存或修改对象.对没有指定主键的对象只有保存没有更新.支持传入数组，列表和映射
     *
     * @param obj
     * @return
     */
    public boolean saveOrUpdate(Object obj) {
        if (obj == null)
            return false;
        Object[] objs;
        if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            objs = collection.toArray();
        } else if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            Iterator<?> iter = map.keySet().iterator();
            int index = 0;
            objs = new Object[map.size()];
            while (iter.hasNext())
                objs[index++] = map.get(iter.next());
        } else if (obj.getClass().isArray())
            objs = (Object[]) obj;
        else {
            //也许obj是一个普通引用
            objs = new Object[1];
            objs[0] = obj;
        }
        return saveOrUpdate(objs);
    }

    /**
     * 删除当前数据库的某表
     *
     * @param cla
     */
    public void dropTable(Class<?> cla) {
        dropTable(getConfig().getDatabaseName(), cla);
    }

    /**
     * 根据对象类删除表
     *
     * @param database 某数据库
     * @param cla      对象类
     */
    public void dropTable(String database, Class<?> cla) {
        String table = cla.getCanonicalName();
        dropTable(database, table);
    }

    /**
     * 删除表
     *
     * @param database 某数据库
     * @param table    表名
     */
    public void dropTable(String database, String table) {
        SQLiteDatabase db = mContext.openOrCreateDatabase(database, Context.MODE_PRIVATE, null);
        if (tableExists(table)) {
            db.execSQL("drop table '" + table + "'");
            if (GlobalConfig.SHOW_LOG)
                Log.d(TAG, "删除表" + table);
        } else if (GlobalConfig.SHOW_LOG)
            Log.d(TAG, "表" + table + "不存在");
    }

    /**
     * 取数据时根据主键排序
     *
     * @param asc
     * @return current database
     */
    public FastDatabase orderBy(boolean asc) {
        mAttribute.setOrderAsc(asc);
        mAttribute.orderBy(""); //空字符串代表使用主键字段
        return this;
    }

    /**
     * 排序
     *
     * @param asc        如果是true为升序，反之降序
     * @param columnName
     * @return
     */
    public FastDatabase orderBy(boolean asc, String columnName) {
        mAttribute.setOrderAsc(asc);
        mAttribute.orderBy(columnName);
        return this;
    }

    /**
     * 取数据时行限制
     *
     * @param start
     * @param end
     * @return current database
     */
    public FastDatabase limit(int start, int end) {
        mAttribute.limit(start, end);
        return this;
    }

    /**
     * 仅取某些列字段
     *
     * @param columns
     * @return current database
     */
    public FastDatabase select(String... columns) {
        mAttribute.setSelectColumn(columns);
        return this;
    }

    /**
     * 不要某些列字段
     *
     * @param columns
     * @return current database
     */
    public FastDatabase unselect(String... columns) {
        mAttribute.setUnselectColumn(columns);
        return this;
    }

    /**
     * 仅单次保存数据到指定数据库而不转换数据库
     *
     * @param databaseName
     * @return
     */
    public FastDatabase toWhichDatabase(String databaseName) {
        mAttribute.setToWhichDatabase(databaseName);
        return this;
    }

    /**
     * 增加过滤条件
     *
     * @param command
     * @return
     */
    public FastDatabase addFilter(FilterCommand command) {
        mAttribute.setFilterCommand(command);
        return this;
    }

    public void setCustomUpdate(CustomUpdate custom) {
        mCustomUpdate = custom;
    }

    /**
     * 获取主键名
     *
     * @param cla
     * @return
     */
    private String getKeyName(Class<?> cla) {
        Field[] fields = cla.getDeclaredFields();
        String key = null;
        for (Field f : fields) {
            Database columnInject = f.getAnnotation(Database.class);
            if (columnInject != null && columnInject.keyPrimary()) {
                key = f.getName();
                break;
            }
        }
        return TextUtils.isEmpty(key) ? null : key;
    }

    /**
     * 生成创建表sql语句
     * @param cla
     * @return
     */
    private String generateCreateTableSql(Class<?> cla) {
        StringBuilder sb = new StringBuilder();
        DatabaseTable table = loadAttribute(cla);

        sb.append("create table if not exists '" + table.tableName + "' (");

        Iterator<String> iter = table.columnMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            DatabaseTable.DatabaseColumn column = table.columnMap.get(key);

            if (column.isIgnore)
                continue;
            if (column.columnName.contains("this"))
                continue;
            if (column.columnName.contains("$"))
                continue;
            sb.append(column.columnName)
                    .append(" " + column.type);
            if (column.isPrimaryKey)
                sb.append(" primary key");
            if (column.autoincrement){
                if (!column.type.equals("integer"))
                    throw new RuntimeException("自动增长只能用于整型数据");
                sb.append(" autoincrement");
            }
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    private DatabaseTable loadAttribute(Class<?> cla) {
        DatabaseTable dt = new DatabaseTable();
        Field[] fields = cla.getDeclaredFields();
        dt.tableName = cla.getName();

        for (Field f : fields) {
            Database fieldInject = f.getAnnotation(Database.class);
            DatabaseTable.DatabaseColumn column = new DatabaseTable.DatabaseColumn();
            String type = f.getType().getSimpleName();

            if (f.getClass().isArray())
                type = f.getType().getName();
            column.columnName = f.getName();
            column.type = Reflect.toSQLType(type);
            if (fieldInject != null) {
                if (!TextUtils.isEmpty(fieldInject.columnName()))
                    column.columnName = fieldInject.columnName();
                if (fieldInject.keyPrimary()) {
                    dt.keyColumn = column;
                    dt.keyFieldName = f.getName();
                }
                column.isPrimaryKey = fieldInject.keyPrimary();
                column.autoincrement = fieldInject.autoincrement();
                column.isIgnore = fieldInject.ignore();
                if (column.isPrimaryKey && !Reflect.isInteger(type) && !Reflect.isReal(type) && !Reflect.isVarchar(type))
                    throw new UnsupportedOperationException("不支持数组或者引用类成为任何键");
            }
            dt.columnMap.put(f.getName(), column);
        }
        return dt;
    }

    private SQLiteDatabase prepare(final String sql) throws SQLiteException {
        SQLiteDatabase database;
        final String databaseName = TextUtils.isEmpty(mAttribute.getWhichDatabase()) ? sConfig.getDatabaseName() : mAttribute.getWhichDatabase();
        SQLiteOpenHelper helper = new SQLiteOpenHelper(mContext, databaseName, null, sConfig.getVersion()) {

            @Override
            public void onCreate(SQLiteDatabase db) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion,
                                  int newVersion) {
                if (GlobalConfig.SHOW_LOG)
                    Log.d(TAG, "发现数据库版本需要升级，开始自动升级");
                if (mCustomUpdate != null) {
                    if (GlobalConfig.SHOW_LOG)
                        Log.d(TAG, "使用自定义升级方案");
                    mCustomUpdate.update(db, oldVersion, newVersion);
                } else
                    updateDatabase(db);
                if (GlobalConfig.SHOW_LOG)
                    Log.d(TAG, "数据库升级完毕");
            }
        };

        database = helper.getWritableDatabase();
        if (!TextUtils.isEmpty(sql)) {
            try {
                database.execSQL(sql);
            } catch (SQLiteException e) {
                Log.w(TAG, "prepare时异常:" + e.getMessage());
            }
        }
        return database;
    }

    /**
     * 遍历所有table反射对象来作对比调整
     *
     * @param db
     */
    private void updateDatabase(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table'", null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String tableName = cursor.getString(0);
                cursor.moveToNext();
                //无视特殊表
                if (tableName.equals("android_metadata"))
                    continue;
                if (tableName.equals("sqlite_sequence"))
                    continue;
                checkTableChanged(db, tableName);
            }
            cursor.close();
        }
    }

    /**
     * 检查表对类映射.如果增加新列可以直接操作，但是如果是更改主键或者修改列类型删除列就需要表重建
     *
     * @param tableName
     */
    private void checkTableChanged(SQLiteDatabase db, String tableName) {
        boolean needRebuildTable = false;//列被删除或字段类型被修改时重置表
        Class<?> cla;
        Field[] fields;
        Map<String, Field> fieldMap = new HashMap<>();
        List<String> convertDatas = new ArrayList<>(); //调整表结构时需要保留数据的列
//		List<String> needChangeColumn=new ArrayList<>();
        Map<String, String> newColumn = new HashMap<>();

        try {
            //如果对象类不存在则删除这张表
            cla = Class.forName(tableName);
        } catch (ClassNotFoundException e) {
            db.execSQL("drop table '" + tableName + "'");
            if (GlobalConfig.SHOW_LOG)
                Log.d(TAG, "删除表" + tableName);
            return;
        }
        fields = cla.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String columnName = field.getName(); //列名以注解为优先，默认字段名
            Database inject = field.getAnnotation(Database.class);
            if (inject != null) {
                if (inject.ignore())
                    continue;
                if (!TextUtils.isEmpty(inject.columnName()))
                    columnName = inject.columnName();
            }
            fieldMap.put(columnName, field);
        }
        DatabaseTable table = parse(db, tableName);
        Iterator<String> iter = table.columnMap.keySet().iterator();

        while (iter.hasNext()) {
            String key = iter.next();
            DatabaseTable.DatabaseColumn column = table.columnMap.get(key);
            Database inject;
            Field field = fieldMap.remove(key);
            convertDatas.add(column.columnName);

            //也许类中某字段被删除了,重建表
            if (field == null) {
                needRebuildTable = true;
                convertDatas.remove(column.columnName);
                continue;
            }
            //判断注解是否被修改
            inject = field.getAnnotation(Database.class);
            if (!column.isPrimaryKey) {
                if (inject != null && inject.keyPrimary()) {
                    convertDatas.remove(column.columnName);
                    needRebuildTable = true; //不能保证某字段在成为主键之前数据唯一
                }
            } else {
                if (inject == null || !inject.keyPrimary())
                    needRebuildTable = true;
            }
            if (!column.autoincrement) {
                if (inject != null && inject.autoincrement()) {
                    convertDatas.remove(column.columnName);
                    needRebuildTable = true; //不能保证某字段在成为主键之前数据唯一
                }
            } else {
                if (inject == null || !inject.autoincrement())
                    needRebuildTable = true;
            }
            String fieldType;
            //如果是数组，判断组类型
//			if(field.getClass().isArray())
//				fieldType=field.getClass().getComponentType().getSimpleName();
//			else
//			    fieldType=field.getType().getSimpleName();
            //判断类型是否被修改.integer改为任何类型都可以被兼容,real只被varchar兼容,varchar不兼容其他类型
            fieldType = field.getType().getSimpleName();
            switch (column.type) {
                case "integer":
                    if (!Reflect.isInteger(fieldType))
                        needRebuildTable = true;
                    break;
                case "real":
                    if (!Reflect.isReal(fieldType)) {
                        needRebuildTable = true;
                        if (!Reflect.isVarchar(fieldType))
                            convertDatas.remove(column.columnName);
                    }
                    break;
                case "varchar":
                    if (!Reflect.isVarchar(fieldType)) {
                        needRebuildTable = true;
                        convertDatas.remove(column.columnName);
                    }
                    break;
                default:
                    if (!field.getType().getName().equals(column.type)) {
                        needRebuildTable = true;
                        convertDatas.remove(column.columnName);
                    }
                    break;
            }
        }
        //数据库表与类字段映射完后多余的字段将作为表新字段加入
        iter = fieldMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            if (key.contains("this"))
                continue;
            if (key.contains("$"))
                continue;
            Field value = fieldMap.get(key);
            String fieldType = value.getType().getSimpleName();
            newColumn.put(key, Reflect.toSQLType(fieldType));
        }
        if (needRebuildTable || newColumn.size() > 0)
            alterTable(db, cla, convertDatas, newColumn, needRebuildTable);
        else if (GlobalConfig.SHOW_LOG)
            Log.d(TAG, "表 " + tableName + " 不需要修改");
    }

    /**
     * 对一个存在的表进行修改
     *
     * @param cla
     * @param valueToValue     保留列和数据
     * @param newColumn        新列名与类型映射组
     * @param needRebuildTable 是否需要重建表
     */
    public void alterTable(SQLiteDatabase db, Class<?> cla, List<String> valueToValue, Map<String, String> newColumn, boolean needRebuildTable) {
        String tableName = cla.getCanonicalName();
        String tempName = "temp_table_" + Long.toString(System.currentTimeMillis()); //数据转移用临时表
        Iterator<String> iter;

        if (!needRebuildTable) {
            if (newColumn != null && newColumn.size() > 0) {
                iter = newColumn.keySet().iterator();
                while (iter.hasNext()) {
                    String column = iter.next();
                    String type = newColumn.get(column);
                    db.execSQL("alter table '" + tableName + "' add " + column + " " + type);
                }
                if (GlobalConfig.SHOW_LOG)
                    Log.d(TAG, "表" + tableName + "增加" + Integer.toString(newColumn.size()) + "列");
            }
        } else {
            if (valueToValue != null && valueToValue.size() > 0) {
                StringBuilder sb = new StringBuilder();
                iter = valueToValue.iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    if (key.equals("id"))
                        continue;
                    sb.append(key + ",");
                }
                if (valueToValue.size() > 0)
                    sb.deleteCharAt(sb.length() - 1);
                db.execSQL("alter table '" + tableName + "' rename to '" + tempName + "'");
                db.execSQL(generateCreateTableSql(cla));
                //注意主键值
                db.execSQL("insert into '" + tableName + "' (" + sb.toString() + ") select " + sb.toString() + " from " + tempName);
                db.execSQL("drop table " + tempName);
            } else {
                db.execSQL("drop table '" + tableName + "'");
                db.execSQL(generateCreateTableSql(cla));
            }
            if (GlobalConfig.SHOW_LOG)
                Log.d(TAG, "表" + tableName + "被调整");
        }
    }

    private DatabaseTable parse(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("select name,sql from sqlite_master where name='" + tableName + "'", null);
        if (cursor != null) {
            cursor.moveToFirst();
            final String name = cursor.getString(cursor.getColumnIndex("name"));
            String sql = cursor.getString(cursor.getColumnIndex("sql"));
            DatabaseTable dt = new DatabaseTable(name);
            sql = sql.substring(sql.indexOf('(') + 1, sql.length() - 1);
            String[] ss = sql.split(",");

            for (String s : ss) {
                DatabaseTable.DatabaseColumn column = new DatabaseTable.DatabaseColumn();
                s = s.trim();
                column.columnName = s.substring(0, s.indexOf(' '));
                column.type = s.substring(s.indexOf(' ')).trim();
                if (column.type.indexOf(' ') != -1)
                    column.type = column.type.substring(0, column.type.indexOf(' ')).trim();
                column.isPrimaryKey = s.contains("primary");
                column.autoincrement = s.contains("autoincrement");
                dt.columnMap.put(column.columnName, column);
            }
            cursor.close();
            return dt;
        }
        return null;
    }

    /**
     * 编译过滤语句
     *
     * @param key           主键名
     * @param filterCommand 过滤命令
     * @param args          对应值
     * @return
     */
    private String getFilters(String key, FilterCommand filterCommand, List<String> args) {
        StringBuilder command = new StringBuilder("");
        if (filterCommand == null)
            return command.toString();
        command.append(" where ").append(filterCommand.getFilterCondition().getExpression(key));
        if (filterCommand.getFilterCondition().getType() != FilterCondition.TYPE_NOT_NULL && filterCommand.getFilterCondition().getType() != FilterCondition.TYPE_NULL)
            args.add(filterCommand.getFilterCondition().getValue());
        filterCommand = filterCommand.getNext();
        while (filterCommand != null) {
            command.append(" ").append(filterCommand.getType() == FilterCommand.TYPE_AND ? "and" : "or").append(" ");
            command.append(filterCommand.getFilterCondition().getExpression(key));
            if (filterCommand.getFilterCondition().getType() != FilterCondition.TYPE_NOT_NULL && filterCommand.getFilterCondition().getType() != FilterCondition.TYPE_NULL)
                args.add(filterCommand.getFilterCondition().getValue());
            filterCommand = filterCommand.getNext();
        }
        return command.toString();
    }

    /**
     * 过滤要取的列
     *
     * @return 要取的列
     */
    private String getSelectColumn(Class<?> cla) {
        StringBuilder sb = new StringBuilder();
        String[] unSelect = mAttribute.getUnselectColumn();
        String[] select = mAttribute.getSelectColumn();
        if ((unSelect == null || unSelect.length == 0)) {
            if ((select == null || select.length == 0))
                return "*";
            else {
                for (String s : select)
                    sb.append(s).append(",");
            }
        } else {
            List<String> fieldsName = getFieldsNameWithoutIgnore(cla); //在需要时使用反射,提高性能
            if (fieldsName != null && !fieldsName.isEmpty()) {
                for (String filter : unSelect)
                    fieldsName.remove(filter);
                for (String s : fieldsName)
                    sb.append(s).append(",");
            }
        }
        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);
        else
            sb.append("*");
        return sb.toString();
    }

    /**
     * 获取没有注解Ignore的所有字段
     *
     * @param cla
     * @return 没有注解Ignore的所有字段
     */
    private static List<String> getFieldsNameWithoutIgnore(Class<?> cla) {
        Field[] fields = cla.getDeclaredFields();

        if (fields == null || fields.length == 0)
            return null;
        List<String> fieldsName = new ArrayList<>(fields.length);
        for (int i = 0; i < fields.length; i++) {
            Database inject = fields[i].getAnnotation(Database.class);
            if (inject != null && inject.ignore())
                continue;
            fieldsName.add(fields[i].getName());
        }
        return fieldsName;
    }

    /**
     * 判断表是否存在
     *
     * @param tableName
     * @return
     */
    private boolean tableExists(String tableName) {
        SQLiteDatabase db = prepare(null);
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' and name=" + "'" + tableName + "'", null);
        boolean exists = !(cursor == null || cursor.getCount() <= 0);

        if (cursor != null)
            cursor.close();
        db.close();
        return exists;
    }

    /**
     * 判断表中是否有数据
     *
     * @param tableName
     * @return
     */
    private boolean tableHadData(String tableName) {
        if (!tableExists(tableName))
            return false;
        SQLiteDatabase db = prepare(null);
        Cursor cursor = db.rawQuery("select rootpage from sqlite_master where type='table' and name='" + tableName + "'", null);
        if (cursor != null) {
            cursor.moveToFirst();
            int page = cursor.getInt(cursor.getColumnIndex("rootpage"));
            cursor.close();
            db.close();
            if (page > 0)
                return true;
        }
        db.close();
        return false;
    }

    /**
     * 指定操作的数据库,直到程序重新运行或者再调用此方法转换操作数据库对象
     * @param databaseName
     */
    public void switchDatabase(String databaseName) {
        sConfig.switchDatabase(databaseName);
    }

    public static String getDefaultDatabaseName() {
        return sConfig.getPrefix() + DEFAULT_DATABASE_NAME + ".db";
    }

    public static DatabaseConfig getConfig() {
        return sConfig;
    }

    /**
     * 数据库配置
     */
    public static class DatabaseConfig {
        private int mVersion;
        private String mCurrentDatabase;
        private String mPrefix;

        /**
         * 默认的数据库配置为
         * 版本＝1
         * 日志输出＝true
         * 数据库名＝default.db
         */
        private DatabaseConfig() {
            mVersion = 1;
            mCurrentDatabase = DEFAULT_DATABASE_NAME + ".db";
        }

        /**
         * 切换数据库，如果不存在会在之后的操作中被创建
         */
        public void switchDatabase(String databaseName) {
            if (mPrefix == null)
                mPrefix = "";
            mCurrentDatabase = mPrefix + databaseName + ".db";
        }

        public String getDatabaseName() {
            return mCurrentDatabase;
        }

        public void setVersion(int version) {
            if (version < mVersion)
                throw new IllegalArgumentException("设置的版本小于等于当前版本");
            mVersion = version;
        }

        public int getVersion() {
            return mVersion;
        }

        public String getPrefix() {
            if (mPrefix == null)
                return "";
            return mPrefix;
        }

        public void setPrefix(String prefix) {
            mPrefix = prefix;
        }
    }
}