package com.fastlib;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.annotation.Event;
import com.fastlib.app.module.FastActivity;
import com.fastlib.app.task.ThreadPoolManager;
import com.fastlib.bean.event.EventDownloading;
import com.fastlib.image_manager.request.Callback2ImageView;
import com.fastlib.image_manager.request.ImageRequest;
import com.fastlib.net.DefaultDownload;
import com.fastlib.net.Request;
import com.fastlib.net.listener.SimpleListener;
import com.fastlib.utils.ContextHolder;
import com.fastlib.utils.N;
import com.fastlib.utils.monitor.MonitorService;
import com.fastlib.widget.LinearDecoration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("all")
@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    final int WIDTH=480;
    final int HEIGHT=800;
    @Bind(R.id.list)
    RecyclerView mList;
    @Bind(R.id.surfaceView)
    SurfaceView mSurfaceView;
    final String mImageUrl="http://cn.best-wallpaper.net/wallpaper/3840x2160/1705/Earth-our-home-planet-space-black-background_3840x2160.jpg";
    MediaProjectionManager mMpm;
    MediaProjection mMediaProjection;
    MediaCodec mMediaCodec;
    MediaCodec mShowMediaCodec;
    MediaCodec.BufferInfo mBufferInfo=new MediaCodec.BufferInfo();
    File mFile;
    MediaMuxer mMediaMuxer;
    VirtualDisplay mVd;
    AtomicBoolean mQuit = new AtomicBoolean(false);
    int videoTrackIndex = -1;
    boolean muxerStarted = false;
    Surface mSurface;
//    final String mImageUrl="https://static.oschina.net/uploads/img/201901/31055503_3yCJ.png";

    @Override
    public void alreadyPrepared(){
        mFile=new File(Environment.getExternalStorageDirectory(),"a.mp4");
        try {
            mFile.createNewFile();
//            mMediaMuxer=new MediaMuxer(mFile.getAbsolutePath(),MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ContextHolder.init(getApplicationContext());
        requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new Runnable() {
            @Override
            public void run() {

            }
        }, new Runnable() {
            @Override
            public void run() {

            }
        });
        mMpm= (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mMpm.createScreenCaptureIntent(),1);
    }

    @Bind(R.id.bt)
    private void bt(){
//        mList.setAdapter(new MyAdapter());
        new Thread() {
            @Override
            public void run() {
                try {
                    mVd = mMediaProjection.createVirtualDisplay("fastlib-display",
                            WIDTH, HEIGHT, 240, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                            mSurface, null, null);
                    recordVirtualDisplay();
                } finally {
                    release();
                }
            }
        }.start();

//        moveTaskToBack(true);
    }

    @Bind(R.id.bt2)
    private void bt2(){
//        ImageRequest.create(mImageUrl)
//                .bindOnHostLifeCycle(this)
//                .setCallbackParcel(new Callback2ImageView(mImage))
//                .start();
        mQuit.set(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1&&resultCode==RESULT_OK){
            mMediaProjection = mMpm.getMediaProjection(resultCode,data);
            try {
                prepareEncoder();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void recordVirtualDisplay() {
        while (!mQuit.get()) {
            int index = mMediaCodec.dequeueOutputBuffer(mBufferInfo, 10000);
            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                resetOutputFormat();
            } else if (index >= 0) {
                encodeToVideoTrack(index);
                mMediaCodec.releaseOutputBuffer(index, false);
            }
        }
    }

    private void encodeToVideoTrack(int index) {
        ByteBuffer encodedData = mMediaCodec.getOutputBuffer(index);

        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0)
            mBufferInfo.size = 0;
        if (mBufferInfo.size == 0) {
            encodedData = null;
        }
        if (encodedData != null) {
            encodedData.position(mBufferInfo.offset);
            encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
            byte[] data=new byte[encodedData.remaining()];
            encodedData.flip();
            System.out.println(encodedData.remaining());
//            showFrame(data);
//            mMediaMuxer.writeSampleData(videoTrackIndex, encodedData, mBufferInfo);
        }
    }

    private void showFrame(byte[] buf){
        System.out.println(buf.length);
        ByteBuffer[] inputBuffers=mShowMediaCodec.getInputBuffers();
        int inputBufferIndex=mShowMediaCodec.dequeueInputBuffer(-1);
        if(inputBufferIndex>=0){
            ByteBuffer inputBuffer=inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(buf,0,buf.length);
            mShowMediaCodec.queueInputBuffer(inputBufferIndex,0,buf.length,0,0);
            MediaCodec.BufferInfo bi=new MediaCodec.BufferInfo();
            int outputBufferIndex=mShowMediaCodec.dequeueOutputBuffer(bi,100);
            while(outputBufferIndex>=0){
                mShowMediaCodec.releaseOutputBuffer(outputBufferIndex,true);
                outputBufferIndex=mShowMediaCodec.dequeueOutputBuffer(bi,0);
            }
        }
    }

    private void resetOutputFormat() {
        MediaFormat newFormat = mMediaCodec.getOutputFormat();
        videoTrackIndex = mMediaMuxer.addTrack(newFormat);
        mMediaMuxer.start();
        muxerStarted = true;
    }

    private void prepareEncoder() throws IOException {
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", WIDTH, HEIGHT);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, 6000000);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);

        mMediaCodec = MediaCodec.createEncoderByType("video/avc");
        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface=mMediaCodec.createInputSurface();

        mShowMediaCodec=MediaCodec.createDecoderByType("video/avc");
        mShowMediaCodec.configure(format,mSurfaceView.getHolder().getSurface(),null,0);
        mShowMediaCodec.start();
        mMediaCodec.start();
    }

    private void release() {
        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
            mShowMediaCodec.stop();
            mShowMediaCodec.release();
            mShowMediaCodec=null;
        }
        if (mVd != null) {
            mVd.release();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
        if (mMediaMuxer != null) {
            mMediaMuxer.release();
            mMediaMuxer = null;
        }
    }
}