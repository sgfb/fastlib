package com.fastlib.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import com.fastlib.app.FastApplication;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sgfb on 16/3/31.
 * 批量下载，图像加载
 */
public class DownloadServer{
    public static final String TAG=DownloadServer.class.getCanonicalName();

    public DownloadServer(){}

    /**
     * 批量下载
     * @param context
     * @param uris
     * @param completion
     */
    public static void startBatchDownload(final Context context, final List<String> uris, final OnDownloadedListener completion){
        final File dir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            dir=new File(context.getExternalCacheDir().getAbsolutePath()+File.separator+"file");
        else
            dir=new File(context.getCacheDir().getAbsolutePath()+File.separator+"file");
        if(!dir.exists())
            if(!dir.mkdirs())
                return;
        final int count=uris.size();
        Iterator<String> iter=uris.iterator();
        final String[] ss=new String[uris.size()];
        int index=0;
        clearDownloadedCount(context);
        while(iter.hasNext()){
            String s=iter.next();
            Request r=new Request("get",s);
            String name=s.replace('/',' ');
            File f=new File(dir,name);
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ss[index++]=dir.getAbsolutePath()+File.separator+name;
            r.setHost(context);
            r.setDownloadable(new DefaultDownload(f));
            r.setListener(new Listener(){
                @Override
                public void onResponseListener(Request r,String result){
                    int downloadedCount=getDownloadedCount(context);
                    increaseDownloadCount(context,downloadedCount);
                    completion.partCompletion(ss[downloadedCount]);
                    if(downloadedCount>=count-1)
                        completion.completion(ss);
                }

                @Override
                public void onErrorListener(Request r,String error) {
                    System.out.println(error);
                }
            });
            NetQueue.getInstance().netRequest(r);
        }
    }

    private static int getDownloadedCount(Context context){
        SharedPreferences sp=context.getSharedPreferences(FastApplication.NAME_SHAREPREFERENCES,Context.MODE_PRIVATE);
        return sp.getInt("downloadedCount",0);
    }

    private static void increaseDownloadCount(Context context,int downloaded){
        SharedPreferences sp=context.getSharedPreferences(FastApplication.NAME_SHAREPREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt("downloadedCount",downloaded+1).apply();
    }

    private static void clearDownloadedCount(Context context){
        SharedPreferences sp=context.getSharedPreferences(FastApplication.NAME_SHAREPREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt("downloadedCount",0).apply();
    }

    public static void getImage(final File file, final String uri, final OnImageCompletion l){
        new Thread(){
            @Override
            public void run(){
                try {
                    URL url=new URL(uri);
                    HttpURLConnection con= (HttpURLConnection) url.openConnection();
                    con.connect();
                    InputStream input=con.getInputStream();
                    OutputStream out=new FileOutputStream(file);
                    byte[] data=new byte[1024];
                    int len;
                    while((len=input.read(data))>-1)
                        out.write(data,0,len);
                    out.close();
                    input.close();
                    l.completion(BitmapFactory.decodeFile(file.getAbsolutePath()));
                } catch (MalformedURLException e) {
                    System.out.println(e);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }.start();
    }

    /**
     * 图像下载回调
     */
    public interface OnImageCompletion{
        void completion(Bitmap bitmap);
    }

    /**
     * 多文件下载时回调
     */
    public interface OnDownloadedListener {
        void partCompletion(String filePath);
        void completion(String[] files);
    }
}