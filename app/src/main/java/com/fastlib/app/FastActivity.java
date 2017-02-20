package com.fastlib.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.LocalData;
import com.fastlib.bean.PermissionRequest;
import com.fastlib.db.And;
import com.fastlib.db.FastDatabase;
import com.fastlib.db.FilterCondition;
import com.fastlib.db.SaveUtil;
import com.fastlib.net.Request;
import com.fastlib.utils.ImageUtil;
import com.fastlib.utils.N;
import com.fastlib.utils.ViewInject;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static com.fastlib.annotation.LocalData.GiverType.INTENT_PARENT;

/**
 * Created by sgfb on 16/9/5.
 * Activity基本封装
 */
public class FastActivity extends AppCompatActivity {
    protected ThreadPoolExecutor mThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

    private boolean isGatingPhoto; //是否正在获取图像
    private Thread mMainThread;
    private PhotoResultListener mPhotoResultListener;
    private List<Pair<Field, LocalData>> mChildActivityGiver = new ArrayList<>();
    private List<Request> mRequests = new ArrayList<>();
    private Map<String, PermissionRequest> mPermissionMap = new HashMap<>();
    private SparseArray<Object[]> mToggleData = new SparseArray<>(); //触发后读取数据缓存点

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainThread = Thread.currentThread();
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                EventObserver.getInstance().subscribe(this);
            }
        });
    }

    /**
     * 启动网络请求
     *
     * @param request
     */
    protected void net(Request request) {
        if (!mRequests.contains(request))
            mRequests.add(request);
        request.setHost(this).setExecutor(mThreadPool).start(false);
    }

    public void addRequest(Request request) {
        if (!mRequests.contains(request))
            mRequests.add(request);
    }

    /**
     * 启动一个任务链
     *
     * @param tc
     */
    protected void startTasks(TaskChain tc) {
        TaskChain.processTaskChain(this, mThreadPool, mMainThread, tc);
    }

    /**
     * 开启获取相册照片
     *
     * @param photoResultListener
     */
    protected void openAlbum(final PhotoResultListener photoResultListener) {
        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, new Runnable() {
            @Override
            public void run() {
                isGatingPhoto = true;
                mPhotoResultListener = photoResultListener;
                ImageUtil.openAlbum(FastActivity.this);
            }
        }, new Runnable() {
            @Override
            public void run() {
                N.showShort(FastActivity.this, "请开启读存储卡权限");
            }
        });
    }

    /**
     * 开启相机获取照片并且指定存储位置
     *
     * @param photoResultListener
     * @param path
     */
    protected void openCamera(final PhotoResultListener photoResultListener, final String path) {
        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Runnable() {
            @Override
            public void run() {
                requestPermission(Manifest.permission.CAMERA, new Runnable() {
                    @Override
                    public void run() {
                        isGatingPhoto = true;
                        mPhotoResultListener = photoResultListener;
                        if (TextUtils.isEmpty(path))
                            ImageUtil.openCamera(FastActivity.this);
                        else
                            ImageUtil.openCamera(FastActivity.this, Uri.fromFile(new File(path)));
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        N.showShort(FastActivity.this, "请开启使用照相机权限");
                    }
                });
            }
        }, new Runnable() {
            @Override
            public void run() {
                N.showShort(FastActivity.this, "请开启写存储卡权限");
            }
        });
    }

    /**
     * 开启相机获取照片
     *
     * @param photoResultListener
     */
    protected void openCamera(PhotoResultListener photoResultListener) {
        openCamera(photoResultListener, null);
    }

    /**
     * 6.0后请求权限
     *
     * @param permission
     * @param grantedAfterProcess
     * @param deniedAfterProcess
     */
    protected void requestPermission(String permission, Runnable grantedAfterProcess, Runnable deniedAfterProcess) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)
            grantedAfterProcess.run();
        else {
            if (!mPermissionMap.containsKey(permission)) {
                int requestCode = mPermissionMap.size() + 1;
                mPermissionMap.put(permission, new PermissionRequest(requestCode, grantedAfterProcess, deniedAfterProcess));
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Pair<Field, LocalData> pair : mChildActivityGiver)
            try {
                loadLocalDataFromIntent(pair.first, pair.second);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        if (isGatingPhoto) {
            isGatingPhoto = false;
            if (resultCode != Activity.RESULT_OK)
                return;
            Uri photoUri = ImageUtil.getImageFromActive(this, requestCode, resultCode, data);
            if (photoUri != null) {
                String photoPath = ImageUtil.getImagePath(this, photoUri);
                if (mPhotoResultListener != null)
                    mPhotoResultListener.onPhotoResult(photoPath);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            PermissionRequest pr = mPermissionMap.remove(permissions[i]);
            if (pr != null) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    pr.hadPermissionProcess.run();
                else
                    pr.deniedPermissionProcess.run();
                break;
            }
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setContentViewAfter();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setContentViewAfter();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setContentViewAfter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventObserver.getInstance().unsubscribe(this);
        mThreadPool.shutdownNow();
        mThreadPool.purge();
        for (Request request : mRequests)
            request.clear();
    }

    private void setContentViewAfter() {
        ViewInject.inject(this, mThreadPool);
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                localDataInject();
            }
        });
    }

    /**
     * 本地数据注入
     */
    public void localDataInject() {
        Field[] fields = getClass().getDeclaredFields();
        Method[] methods = getClass().getDeclaredMethods();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                field.setAccessible(true);
                LocalData lr = field.getAnnotation(LocalData.class);
                if (lr == null)
                    continue;
                try {
                    switch (lr.from()[0]) {
                        case INTENT_PARENT:
                            loadLocalDataFromIntent(field, lr);
                            break;
                        case INTENT_CHILD:
                            mChildActivityGiver.add(new Pair<>(field, lr));
                            break;
                        case SP:
                            loadLocalDataFromSp(field, lr);
                            break;
                        case DATABASE:
                            loadLocalDataFromDatabase(field, lr);
                            break;
                        case ASSETS:
                            loadLocalDataFromFile(field, lr, true);
                            break;
                        case FILE:
                            loadLocalDataFromFile(field, lr, false);
                            break;
                        default:
                            break;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //仅查看是否有"触发数据"
        if (methods != null && methods.length > 0) {
            for (final Method m : methods) {
                m.setAccessible(true);
                final LocalData ld = m.getAnnotation(LocalData.class);
                final Bind bind = m.getAnnotation(Bind.class);
                if (ld != null && bind != null) {
                    View v = findViewById(bind.value()[0]);
                    switch (bind.bindType()) {
                        case CLICK:
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    invokeToggleCallback(v, m, ld, bind.bindType(), this, null, null, null);
                                }
                            });
                            break;
                        case LONG_CLICK:
                            v.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    invokeToggleCallback(v, m, ld, bind.bindType(), null, this, null, null);
                                    return false;
                                }
                            });
                            break;
                        case ITEM_CLICK:
                            ((AdapterView) v).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    invokeToggleCallback(parent, m, ld, bind.bindType(), null, null, this, null);
                                }
                            });
                            break;
                        case ITEM_LONG_CLICK:
                            ((AdapterView) v).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                    invokeToggleCallback(parent, m, ld, bind.bindType(), null, null, null, this);
                                    return false;
                                }
                            });
                    }
                }
            }
        }
    }

    private void invokeToggleCallback(View v, Method m, LocalData ld, Bind.BindType type, View.OnClickListener clickListener, View.OnLongClickListener longClickListtener,
                                      AdapterView.OnItemClickListener itemClickListener, AdapterView.OnItemLongClickListener itemLongClickListener) {
        Object[] data = mToggleData.get(v.getId());
        Class<?>[] paramTypes = m.getParameterTypes();
        try {
            if (data == null) { //如果没有则读取一份进入缓存
                if (GlobalConfig.SHOW_LOG)
                    System.out.println("缓存中没有触发数据");
                //截断触发事件直到数据读取完毕
                switch (type) {
                    case CLICK:
                        v.setOnClickListener(null);
                        break;
                    case LONG_CLICK:
                        v.setOnLongClickListener(null);
                        break;
                    case ITEM_CLICK:
                        ((AdapterView) v).setOnItemClickListener(null);
                        break;
                    case ITEM_LONG_CLICK:
                        ((AdapterView) v).setOnItemLongClickListener(null);
                        break;
                }
                data = loadLocalData(ld, paramTypes.length == 1 ? paramTypes[0] : (paramTypes[0] == View.class ? paramTypes[1] : paramTypes[0]));
                switch (type) {
                    case CLICK:
                        v.setOnClickListener(clickListener);
                        break;
                    case LONG_CLICK:
                        v.setOnLongClickListener(longClickListtener);
                        break;
                    case ITEM_CLICK:
                        ((AdapterView) v).setOnItemClickListener(itemClickListener);
                        break;
                    case ITEM_LONG_CLICK:
                        ((AdapterView) v).setOnItemLongClickListener(itemLongClickListener);
                        break;
                }
                mToggleData.append(v.getId(), data);
                v.setOnClickListener(clickListener);
            } else if(GlobalConfig.SHOW_LOG)
                System.out.println("缓存中有触发数据");
            //View必须在第一个，接下来是参数对象数组
            flatInvoke(m, v, data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void flatInvoke(Method m, View v, Object[] data) throws InvocationTargetException, IllegalAccessException {
        switch (data.length) {
            case 1:
                m.invoke(FastActivity.this, v, data[0]);
                break;
            case 2:
                m.invoke(FastActivity.this, v, data[0], data[1]);
                break;
            case 3:
                m.invoke(FastActivity.this, v, data[0], data[1], data[2]);
                break;
            case 4:
                m.invoke(FastActivity.this, v, data[0], data[1], data[2], data[3]);
                break;
            case 5:
                m.invoke(FastActivity.this, v, data[0], data[1], data[2], data[3], data[4]);
                break;
            case 6:
                m.invoke(FastActivity.this, v, data[0], data[1], data[2], data[3], data[4], data[5]);
                break;
            case 7:
                m.invoke(FastActivity.this, v, data[0], data[1], data[2], data[3], data[4], data[5], data[6]);
                break;
            case 8:
                m.invoke(FastActivity.this, v, data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
                break;
            case 9:
                m.invoke(FastActivity.this, v, data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8]);
            case 10:
                m.invoke(FastActivity.this, v, data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9]);
                break;
            default:
                break;
        }
    }

    /**
     * 读取本地数据，不支持子Activity返回的Intent
     *
     * @param ld
     * @param param
     * @return
     */
    private Object[] loadLocalData(LocalData ld, Class<?> param) {
        Object[] datas = new Object[ld.value().length];
        for (int i = 0; i < datas.length; i++) {
            switch (ld.from()[i]) {
                case INTENT_PARENT:
                    datas[i] = loadLocalDataFromIntent(i, ld, param);
                    break;
                case SP:
                    datas[i] = loadLocalDataFromSp(i, ld, param);
                    break;
                case FILE:
                    datas[i] = loadLocalDataFromFile(i, ld, param, false);
                    break;
                case ASSETS:
                    datas[i] = loadLocalDataFromFile(i, ld, param, true);
                    break;
                case DATABASE:
                    datas[i] = loadLocalDataFromDatabase(i, ld, param);
                    break;
            }
        }
        return datas;
    }

    private Object loadLocalDataFromDatabase(int position, LocalData ld, Class<?> paramsType) {
        return FastDatabase.getDefaultInstance(this).addFilter(new And(FilterCondition.equal(ld.value()[position]))).getFirst(paramsType);
    }

    private Object loadLocalDataFromFile(int position, LocalData ld, Class<?> paramsType, boolean fromAssets) {
        try {
            Gson gson = new Gson();
            byte[] data = fromAssets ? SaveUtil.loadAssetsFile(getAssets(), ld.value()[position]) : SaveUtil.loadFile(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ld.value());
            if (paramsType == byte[].class)
                return data;
            else if (paramsType == String.class)
                return new String(data);
            else
                return gson.fromJson(new String(data), paramsType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object loadLocalDataFromSp(int position, LocalData ld, Class<?> paramType) {
        SharedPreferences sp = getSharedPreferences("fast", MODE_PRIVATE);
        if (paramType == boolean.class || paramType == Boolean.class)
            return sp.getBoolean(ld.value()[position], false);
        else if (paramType == int.class || paramType == Integer.class)
            return sp.getInt(ld.value()[position], -1);
        else if (paramType == long.class || paramType == Long.class)
            return sp.getLong(ld.value()[position], -1);
        else if (paramType == float.class || paramType == Float.class)
            return sp.getFloat(ld.value()[position], -1.1f);
        else if (paramType == String.class)
            return sp.getString(ld.value()[position], null);
        else if (paramType == Set.class)
            return sp.getStringSet(ld.value()[position], null);
        else
            return null;
    }

    private Object loadLocalDataFromIntent(int position, LocalData ld, Class<?> paramType) {
        if (paramType == boolean.class)
            return getIntent().getBooleanExtra(ld.value()[position], false);
        else if (paramType == boolean[].class)
            return getIntent().getBooleanArrayExtra(ld.value()[position]);
        else if (paramType == byte.class)
            return getIntent().getByteExtra(ld.value()[position], (byte) -1);
        else if (paramType == byte[].class)
            return getIntent().getByteArrayExtra(ld.value()[position]);
        else if (paramType == char.class)
            return getIntent().getCharExtra(ld.value()[position], '0');
        else if (paramType == char[].class)
            return getIntent().getCharArrayExtra(ld.value()[position]);
        else if (paramType == short.class)
            return getIntent().getShortExtra(ld.value()[position], (short) -1);
        else if (paramType == short[].class)
            return getIntent().getShortArrayExtra(ld.value()[position]);
        else if (paramType == int.class)
            return getIntent().getIntExtra(ld.value()[position], -1);
        else if (paramType == int[].class)
            return getIntent().getIntArrayExtra(ld.value()[position]);
        else if (paramType == float.class)
            return getIntent().getFloatExtra(ld.value()[position], -1.1f);
        else if (paramType == float[].class)
            return getIntent().getFloatArrayExtra(ld.value()[position]);
        else if (paramType == double.class)
            return getIntent().getDoubleExtra(ld.value()[position], -1.1);
        else if (paramType == double[].class)
            return getIntent().getDoubleArrayExtra(ld.value()[position]);
        else if (paramType == String.class)
            return getIntent().getStringExtra(ld.value()[position]);
        else if (paramType == String[].class)
            return getIntent().getStringArrayExtra(ld.value()[position]);
        else
            return getIntent().getSerializableExtra(ld.value()[position]);
    }

    /**
     * 从数据库中加载数据到属性中(仅支持FastDatabase)
     *
     * @param field
     * @param lr
     * @throws IllegalAccessException
     */
    private void loadLocalDataFromDatabase(Field field, LocalData lr) throws IllegalAccessException {
        Class<?> type = field.getType();
        Object obj = FastDatabase.getDefaultInstance(this).addFilter(new And(FilterCondition.equal(lr.value()[0]))).getFirst(type);
        field.set(this, obj);
    }

    /**
     * 从SharedPreferences中加载数据到属性中
     *
     * @param field
     * @param lr
     * @throws IllegalAccessException
     */
    private void loadLocalDataFromSp(Field field, LocalData lr) throws IllegalAccessException {
        SharedPreferences sp = getSharedPreferences("fast", MODE_PRIVATE);
        Class<?> type = field.getType();
        if (type == boolean.class)
            field.setBoolean(this, sp.getBoolean(lr.value()[0], false));
        else if (type == Boolean.class)
            field.set(this, sp.getBoolean(lr.value()[0], false));
        else if (type == int.class)
            field.setInt(this, sp.getInt(lr.value()[0], -1));
        else if (type == Integer.class)
            field.set(this, sp.getInt(lr.value()[0], -1));
        else if (type == float.class)
            field.setFloat(this, sp.getFloat(lr.value()[0], -1));
        else if (type == Float.class)
            field.set(this, sp.getFloat(lr.value()[0], -1));
        else if (type == long.class)
            field.setLong(this, sp.getLong(lr.value()[0], -1));
        else if (type == Long.class)
            field.set(this, sp.getLong(lr.value()[0], -1));
        else if (type == String.class)
            field.set(this, sp.getString(lr.value()[0], null));
    }

    /**
     * 从文件中加载数据到属性中.取文件时默认取外磁卡位置
     *
     * @param field
     * @param lr
     * @param fromAssets
     * @throws IOException
     * @throws IllegalAccessException
     */
    private void loadLocalDataFromFile(Field field, LocalData lr, boolean fromAssets) throws IOException, IllegalAccessException {
        byte[] data = fromAssets ? SaveUtil.loadAssetsFile(getAssets(), lr.value()[0]) : SaveUtil.loadFile(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + lr.value());
        if (data == null)
            return;
        Class<?> type = field.getType();
        if (type == byte[].class)
            field.set(this, data);
        else if (type == String.class)
            field.set(this, new String(data));
        else {
            Gson gson = new Gson();
            field.set(this, gson.fromJson(new String(data), type));
        }
    }

    /**
     * 从Intent中加载本地数据
     *
     * @param field
     * @param lr
     * @throws IllegalAccessException
     */
    private void loadLocalDataFromIntent(Field field, LocalData lr) throws IllegalAccessException {
        Class<?> type = field.getType();
        if (type == byte.class)
            field.setByte(this, getIntent().getByteExtra(lr.value()[0], (byte) -1));
        else if (type == Byte.class)
            field.set(this, getIntent().getByteExtra(lr.value()[0], (byte) -1));
        else if (type == byte[].class)
            field.set(this, getIntent().getByteArrayExtra(lr.value()[0]));
        else if (type == char.class)
            field.set(this, getIntent().getCharExtra(lr.value()[0], '0'));
        else if (type == char[].class)
            field.set(this, getIntent().getCharArrayExtra(lr.value()[0]));
        else if (type == boolean.class)
            field.setBoolean(this, getIntent().getBooleanExtra(lr.value()[0], false));
        else if (type == Boolean.class)
            field.set(this, getIntent().getBooleanExtra(lr.value()[0], false));
        else if (type == boolean[].class)
            field.set(this, getIntent().getBooleanArrayExtra(lr.value()[0]));
        else if (type == short.class)
            field.set(this, getIntent().getShortExtra(lr.value()[0], (short) -1));
        else if (type == Short.class)
            field.set(this, getIntent().getShortExtra(lr.value()[0], (short) -1));
        else if (type == short[].class)
            field.set(this, getIntent().getShortArrayExtra(lr.value()[0]));
        else if (type == int.class)
            field.setInt(this, getIntent().getIntExtra(lr.value()[0], -1));
        else if (type == Integer.class)
            field.set(this, getIntent().getIntExtra(lr.value()[0], -1));
        else if (type == int[].class)
            field.set(this, getIntent().getIntArrayExtra(lr.value()[0]));
        else if (type == float.class)
            field.setFloat(this, getIntent().getFloatExtra(lr.value()[0], -1));
        else if (type == Float.class)
            field.set(this, getIntent().getFloatExtra(lr.value()[0], -1));
        else if (type == float[].class)
            field.set(this, getIntent().getFloatArrayExtra(lr.value()[0]));
        else if (type == long.class)
            field.setLong(this, getIntent().getLongExtra(lr.value()[0], -1));
        else if (type == Long.class)
            field.set(this, getIntent().getLongExtra(lr.value()[0], -1));
        else if (type == long[].class)
            field.set(this, getIntent().getLongArrayExtra(lr.value()[0]));
        else if (type == double.class)
            field.setDouble(this, getIntent().getDoubleExtra(lr.value()[0], -1));
        else if (type == Double.class)
            field.setDouble(this, getIntent().getDoubleExtra(lr.value()[0], -1));
        else if (type == double[].class)
            field.set(this, getIntent().getDoubleArrayExtra(lr.value()[0]));
        else if (type == String.class)
            field.set(this, getIntent().getStringExtra(lr.value()[0]));
        else
            field.set(this, getIntent().getSerializableExtra(lr.value()[0]));
    }


    public void startActivity(Class<? extends Activity> cla) {
        startActivity(new Intent(this, cla));
    }

    /**
     * 请求相机或相册时图像回调接口
     */
    public interface PhotoResultListener {
        void onPhotoResult(String path);
    }
}