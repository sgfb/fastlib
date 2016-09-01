package com.fastlib.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.fastlib.app.FastApplication;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Downloadable;
import com.fastlib.net.Listener;
import com.fastlib.net.NetQueue;
import com.fastlib.net.Request;
import com.fastlib.net.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sgfb on 16/3/31.
 */
public class ImageCache {
    public static final String SAVE_PATH_EXTERNAL= Environment.getExternalStorageDirectory().getAbsolutePath()+"/data/huanyu/cache";
    public static final String SAVE_PATH_INTERNAL= FastApplication.getInstance().getCacheDir().getAbsolutePath();
    private static int count;

    public ImageCache(){}

    public static void startDownloadImage(final List<String> uri, final OnDownloadCompletion completion){
        final File dir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            dir=new File(SAVE_PATH_EXTERNAL);
        else
            dir=new File(SAVE_PATH_INTERNAL);
        if(!dir.exists())
            if(!dir.mkdirs())
                return;
        count=uri.size();
        Iterator<String> iter=uri.iterator();
        final String[] ss=new String[uri.size()];
        int index=0;
        while(iter.hasNext()){
            String s=iter.next();
            Request r=new Request("post",s);
            String name=s.replace('/',' ');
            File f=new File(dir,name);
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ss[index++]=dir.getAbsolutePath()+File.separator+name;
            r.setDownloadable(new DefaultDownload(f));
            r.setListener(new Listener() {
                @Override
                public void onResponseListener(Request r,String result){
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

    public interface OnImageCompletion{
        void completion(Bitmap bitmap);
    }

    public interface OnDownloadCompletion{
        void completion(String[] files);
    }
}