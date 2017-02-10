package com.fastlib.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sgfb on 16/4/19.
 * 音频播放器封装
 */
public class AudioPlayer{
    public static final String TAG =AudioPlayer.class.getSimpleName();
    public static final int STATE_UNPREPARED=1;
    public static final int STATE_PAUSE=2;
    public static final int STATE_PLAY=3;
    public static final int STATE_CLOSE=4;

    private static AudioPlayer mOwer;
    private static int sAutoIncrement=1;
    private Map<Integer,SingleMedia> mMap;

    private AudioPlayer(){
        mMap=new HashMap<>();
    }

    public static synchronized AudioPlayer getInstance(){
        if(mOwer==null)
            mOwer=new AudioPlayer();
        return mOwer;
    }

    public synchronized int getPlayerLink(final Context context,final Uri uri,final OnProgressListener l){
        final int callbackValue=sAutoIncrement++;
        final SingleMedia sm=new SingleMedia();
        sm.mUri=uri;
        sm.mProgressListener=new ArrayList<>();
        if(l!=null)
            sm.mProgressListener.add(l);
        sm.mCreatedListener=new OnCreatedPlayer(){
            @Override
            public void onCreated(MediaPlayer mp){
                if(mp==null){
                    for(int i=0;i<sm.mProgressListener.size();i++)
                        sm.mProgressListener.get(i).onError("未知异常");
                    return;
                }
                sm.mMediaPlayer=mp;
                sm.state=STATE_PAUSE;
                for(int i=0;i<sm.mProgressListener.size();i++)
                    sm.mProgressListener.get(i).onPrepareCompletion(mp.getDuration());
                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp,int percent){
                        int progress=(int) ((double)mp.getCurrentPosition()/mp.getDuration()*100)+1;
                        if(sm.isClose){
                            close(callbackValue);
                            return;
                        }
                        for(int i=0;i<sm.mProgressListener.size();i++){
                            OnProgressListener l=sm.mProgressListener.get(i);
                            l.onBuffing(percent);
                            l.onProgress(mp.getCurrentPosition(),progress);
                        }
                    }
                });
//                startOrPause(callbackValue);  //缓冲好后自动开始
            }
        };
        mMap.put(callbackValue,sm);
        new Thread(){
            @Override
            public void run(){
                final String scheme = uri.getScheme();
                MediaPlayer mp;
                if ("file".equals(scheme)) {
                    String path = uri.getPath();
                    mp =new MediaPlayer();
                    try {
                        mp.setDataSource(path);
                        mp.prepare();
                    } catch (IOException e) {
                        Log.d(TAG, "初始化失败，寻找不到本地文件");
                    }
                }
                else
                    mp=MediaPlayer.create(context,uri);
                mMap.get(callbackValue).mCreatedListener.onCreated(mp);
            }
        }.start();
        return callbackValue;
    }

    public void startOrPause(int id){
        SingleMedia sm=mMap.get(id);
        if(sm==null){
            Log.d(TAG,"id为"+Integer.toString(id)+"的SingleMedia不存在");
            return;
        }
        if(sm.isClose){
            close(id);
            return;
        }
        if(sm.state==STATE_PAUSE||sm.state==STATE_PLAY){
            if(sm.state==STATE_PLAY) {
                sm.mMediaPlayer.pause();
                sm.state=STATE_PAUSE;
            }
            else{
                sm.mMediaPlayer.start();
                sm.state=STATE_PLAY;
            }
            for(int i=0;i<sm.mProgressListener.size();i++)
                sm.mProgressListener.get(i).onStateChanged(sm.state);
        }
    }

    /**
     * 关闭音频
     * @param id
     */
    public void close(int id){
        SingleMedia sm=mMap.get(id);
        if(sm==null) {
            Log.d(TAG,"id为"+Integer.toString(id)+"的SingleMedia不存在");
            return;
        }
        if(sm.state==STATE_UNPREPARED){
            sm.isClose=true;
            return;
        }
        sm.state=STATE_CLOSE;
        sm.mMediaPlayer.stop();
        sm.mMediaPlayer.release();
        sm.mMediaPlayer=null;
        for(int i=0;i<sm.mProgressListener.size();i++)
            sm.mProgressListener.get(i).onStateChanged(STATE_CLOSE);
        mMap.remove(id);
    }

    /**
     * 跳转播放位置
     * @param id
     * @param percent
     */
    public void seekTo(int id,float percent){
        SingleMedia sm=mMap.get(id);
        if(sm==null){
            Log.d(TAG,"id为"+Integer.toString(id)+"的SingleMedia不存在");
            return;
        }
        if(sm.state== STATE_UNPREPARED ||sm.state==STATE_CLOSE){
            Log.d(TAG,"播放器未准备好或者已关闭");
            return;
        }
        sm.mMediaPlayer.seekTo((int) (sm.mMediaPlayer.getDuration() * (percent / 100)));
        if(sm.state!=STATE_PLAY){
            sm.mMediaPlayer.start();
            sm.state=STATE_PLAY;
            for(int i=0;i<sm.mProgressListener.size();i++)
                sm.mProgressListener.get(i).onStateChanged(STATE_PLAY);
        }
    }

    /**
     * 增加某音频监听
     * @param id
     * @param l
     */
    public void addProgressListener(int id,OnProgressListener l){
        SingleMedia sm=mMap.get(id);
        if(sm==null){
            Log.w(TAG, "ID为" + id + "的音频不存在");
            return;
        }
        if(l==null)
            return;
        sm.mProgressListener.add(l);
    }

    /**
     * 移除某音频监听
     * @param id
     * @param l
     */
    public void removeProgressListener(int id,OnProgressListener l){
        SingleMedia sm=mMap.get(id);
        if(sm==null){
            Log.w(TAG, "ID为" + id + "的音频不存在");
            return;
        }
        sm.mProgressListener.remove(l);
    }

    public MediaPlayer getMediaPlayer(int id){
        SingleMedia sm=mMap.get(id);
        if(sm==null)
            return null;
        return sm.mMediaPlayer;
    }

    /**
     * 音频是否存在
     * @param id
     * @return
     */
    public boolean exists(int id){
        SingleMedia sm=mMap.get(id);
        return sm!=null&&sm.mMediaPlayer!=null;
    }

    /**
     * 对应id的音频是否正在播放
     * @param id
     * @return
     */
    public boolean playing(int id){
        if(!exists(id))
            return false;
        return mMap.get(id).mMediaPlayer.isPlaying();
    }

    /**
     * 根据uri返回id.如果不存在返回－1
     * @param uri
     * @return
     */
    public int getIdByUri(Uri uri){
        Iterator<Integer> iter=mMap.keySet().iterator();
        while(iter.hasNext()){
            int key=iter.next();
            SingleMedia sm=mMap.get(key);
            if(sm.mUri.equals(uri))
                return key;
        }
        return -1;
    }

    /**
     * 返回某音频播放位置.音频不存在返回0
     * @param id
     * @return 0~100
     */
    public int getProgress(int id){
        SingleMedia sm=mMap.get(id);
        if(sm==null)
            return 0;
        MediaPlayer mp=sm.mMediaPlayer;
        return (int) ((double)mp.getCurrentPosition()/mp.getDuration()*100)+1;
    }

    interface OnCreatedPlayer{
        void onCreated(MediaPlayer mp);
    }

    public interface OnProgressListener{
        void onStateChanged(int state);
        void onBuffing(int percent);

        /**
         * 准备完成
         * @param length 长度,毫秒值
         */
        void onPrepareCompletion(int length);

        /**
         * 播放进度
         * @param position 毫秒值
         * @param percent 百分比
         */
        void onProgress(int position, int percent);
        void onError(String message);
    }

    final class SingleMedia{
        int state= STATE_UNPREPARED;
        boolean isClose;
        Uri mUri;
        MediaPlayer mMediaPlayer;
        List<OnProgressListener> mProgressListener;
        OnCreatedPlayer mCreatedListener;
    }
}
