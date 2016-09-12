package com.fastlib.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil{
    public static final String TAG=ImageUtil.class.getSimpleName();
    public static final int REQUEST_FROM_ALBUM=10000;
    public static final int REQUEST_FROM_CAMERA=10001;
    public static final int REQUEST_FROM_CROP=10002;
    public static final String KEY_LAST_IMAGE="lastImage";

    private ImageUtil(){
        //不实例化
    }

    private static void saveLastImage(Context context,Uri uri){
        SharedPreferences.Editor edit=context.getSharedPreferences(TAG,Context.MODE_PRIVATE).edit();
        edit.putString(KEY_LAST_IMAGE, uri.toString());
        edit.commit();
    }

    /**
     * 生成缩略图
     * @param path
     * @param limit
     * @return
     */
    public static File getThumbImageFile(String path,String parent,int limit,int quality) throws IOException{
        File f=new File(path);
        if(f.exists())
            System.out.println(f.length());
        else
            System.out.println("file not exists");
        Bitmap bitmap=getThumbBitmap(path,limit);
        File file=getTempFile(new File(parent));
        FileOutputStream fos =new FileOutputStream(file);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        byte[] bytes = stream.toByteArray();
        fos.write(bytes);
        fos.close();
        return file;
    }

    /**
     * 取得缩放后的图像，默认低质量
     * @param path 路径
     * @param limit 限制大小
     * @return
     */
    public static Bitmap getThumbBitmap(String path,int limit){
        return getThumbBitmap(path, limit, false);
    }

    /**
     * 取得缩放后的图像 开启renderscriptTargetApi 15 renderscriptSupportModeEnabled true
     * @param path 路径
     * @param limit 限制大小
     * @param highQuality 质量
     * @return
     */
    public static Bitmap getThumbBitmap(String path,int limit,boolean highQuality){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path,options);
        int max=Math.max(options.outHeight,options.outWidth);

        options.inJustDecodeBounds=false;
        if(max>limit)
            options.inSampleSize = max / limit+1;
        if(!highQuality)
            options.inPreferredConfig= Bitmap.Config.ARGB_4444;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 写图片文件 文件保存在 /data/data/PACKAGE_NAME/files 目录下
     *
     * @throws IOException
     */
    public static void saveImage(Context context, String fileName, Bitmap bitmap) throws IOException {
        saveImage(context, fileName, bitmap, 100);
    }

    public static void saveImage(Context context, String fileName, Bitmap bitmap, int quality) throws IOException {
        if (bitmap == null || fileName == null || context == null)
            return;

        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream);
        byte[] bytes = stream.toByteArray();
        fos.write(bytes);
        fos.close();
    }

    /**
     * Bitmap转Drawable
     * @param bitmap
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap){
        Drawable drawable=new BitmapDrawable(bitmap);
        return drawable;
    }

    /**
     * Drawable转Bitmat
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable){
        Bitmap bitmap=Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }

    public static void openAlbum(Fragment fragment){
        openAlbum(null, fragment, false);
    }

    /**
     * 预览相册
     * @param activity
     */
    public static void openAlbum(Activity activity){
        openAlbum(activity, null, false);
    }

    @TargetApi(18)
    public static void openAlbum(Activity activity,Fragment fragment,boolean multiChoose){
        Intent intent = new Intent();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        else
            intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.CATEGORY_OPENABLE, true);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiChoose);
        if(activity!=null)
            activity.startActivityForResult(intent, REQUEST_FROM_ALBUM);
        else
            fragment.startActivityForResult(intent,REQUEST_FROM_ALBUM);
    }

    public static void openCamera(Fragment fragment){
        openCamera(null, fragment, Uri.fromFile(getTempFile(null)));
    }

    public static void openCamera(Activity activity){
        openCamera(activity, null, Uri.fromFile(getTempFile(null))); //默认写在存储卡内
    }

    /**
     * 指定照片存储位置启动相机
     * @param activity
     * @param output
     */
    public static void openCamera(Activity activity,Fragment fragment,Uri output){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,output);
        if(activity!=null){
            saveLastImage(activity,output);
            activity.startActivityForResult(intent, REQUEST_FROM_CAMERA);
        }
        else {
            saveLastImage(fragment.getContext(),output);
            fragment.startActivityForResult(intent, REQUEST_FROM_CAMERA);
        }
    }

    /**
     * 裁剪图片
     * @param activity
     * @param data
     * @param crop
     */
    public static void startActionCrop(Activity activity,Uri data,int crop){
        startActionCrop(activity, data, crop, Uri.fromFile(getTempFile(null)));
    }

    /**
     * 裁剪图片
     * @param activity
     * @param data
     * @param crop
     * @param outPut
     */
    public static void startActionCrop(Activity activity, Uri data, int crop, Uri outPut){
        Intent intent = new Intent("com.android.camera.action.CROP",null);
        saveLastImage(activity,outPut);
        intent.setDataAndType(data, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,outPut);
        intent.putExtra("circleCrop", true);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", crop);// 输出图片大小
        intent.putExtra("outputY", crop);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        activity.startActivityForResult(intent,REQUEST_FROM_CROP);
    }

    /**
     * 创建可指定父级的临时文件
     * @param parent
     */
    public static File getTempFile(@Nullable File parent){
        File file=null;
        if(parent!=null&&parent.exists())
            file=new File(parent.getAbsolutePath()+File.separator+Long.toString(System.currentTimeMillis())+".jpg");
        else{
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+System.currentTimeMillis()+".jpg");
        }
        if(file!=null)
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return file;
    }

    /**
     * 获取来自某种动作的照片uri.如果授权失败或者没有选择任何照片，返回null
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    public static Uri getImageFromActive(Context context,int requestCode, int resultCode, Intent data){
        if(resultCode!=Activity.RESULT_OK)
            return null;

        switch (requestCode){
            case REQUEST_FROM_CAMERA:
                SharedPreferences sp=context.getSharedPreferences(TAG,Context.MODE_PRIVATE);
                Uri uri=Uri.parse(sp.getString(KEY_LAST_IMAGE,""));
                return uri;
            case REQUEST_FROM_ALBUM:
                return data.getData();
            default:
                return null;
        }
    }

    /**
     * 获取图片路径
     * @param context
     * @param uri
     * @return
     */
    public static String getImagePath(Context context,Uri uri){
        if(uri==null)
            return null;
        boolean isKitKat=Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT;

        if(isKitKat)
            return getImagePathForNewSdk(context,uri);
        else
            return getImagePathForOldSdk(context,uri);
    }

    /**
     * 获取图片路径(老sdk)
     *
     * @param context
     * @param uri
     */
    private static String getImagePathForOldSdk(Context context,Uri uri) {

        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            String ImagePath = cursor.getString(columnIndex);
            cursor.close();
            return ImagePath;
        }

        return uri.toString();
    }

    /**
     * 获取图片路径（新sdk）
     *
     * @param uri
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getImagePathForNewSdk(Context context, Uri uri){
        if(DocumentsContract.isDocumentUri(context,uri)){
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            final String selection = "_id=?";
            final String type = split[0];
            final String[] selectionArgs = new String[] {split[1]};
            Cursor cursor = null;

            if ("primary".equalsIgnoreCase(type))//小米兼容方案
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            try {
                cursor = context.getContentResolver().query(contentUri,new String[]{MediaStore.Images.Media.DATA}, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    return cursor.getString(index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }
        else{
            String scheme=uri.getScheme();
            if(scheme.equals("file"))
                return uri.getPath();
        }
        return null;
    }

    /**
     * 高斯模糊，默认值25
     * @param context
     * @param raw
     * @return
     */
    public static Bitmap blurBitmap(Context context,Bitmap raw){
        return blurBitmap(context,raw,25);
    }
    /**
     * 高斯模糊
     * @param context
     * @param raw
     * @param radius
     * @return
     */
    public static Bitmap blurBitmap(Context context,Bitmap raw,int radius){
        RenderScript rs= RenderScript.create(context);
        Bitmap bitmap=Bitmap.createBitmap(raw.getWidth(), raw.getHeight(), Bitmap.Config.ARGB_8888);
        Allocation allocIn=Allocation.createFromBitmap(rs, raw);
        Allocation allocOut=Allocation.createFromBitmap(rs,bitmap);
        ScriptIntrinsicBlur blur=ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        if(radius<0)
            radius=0;
        else if(radius>25)
            radius=25;
        blur.setRadius(radius);
        blur.setInput(allocIn);
        blur.forEach(allocOut);
        allocOut.copyTo(bitmap);
        rs.destroy();
        return bitmap;
    }

    /**
     * mp4文件取首帧
     * @param filePath
     * @return
     */
    private static Bitmap getVideFirstFrame(String filePath) {
        Bitmap bitmap;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        bitmap = retriever.getFrameAtTime();
        retriever.release();
        return bitmap;
    }

    public static void saveVideoFrame(Context context,String srcFilePath,String name) throws IOException {
        Bitmap bitmap=getVideFirstFrame(srcFilePath);
        if(bitmap!=null)
            saveImage(context,name,bitmap);
    }

    /**
     * 保存view截图到文件中
     * @param v
     * @param f
     */
    public static void saveViewToFile(View v,File f){
        saveViewToFile(v,f,1);
    }

    public static void saveViewToFile(View v,File f,float scale){
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        if(!f.exists())
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        Bitmap bitmap=v.getDrawingCache();
        if(bitmap!=null)
            try {
                bitmap=Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*scale),(int)(bitmap.getHeight()*scale),true);
                bitmap.compress(Bitmap.CompressFormat.PNG,100,new FileOutputStream(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        else
            System.out.println("保存view到文件中失败");
    }
}
