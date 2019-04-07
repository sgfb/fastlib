package com.fastlib;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.LogRecord;

/**
 * Create by sgfb on 2019/03/19
 * E-Mail:602687446@qq.com
 */
public class VideoEncoder {
    private MediaCodec mMediaCodec;
    private Handler mEncoderHandler;
    private HandlerThread mHandlerThread=new HandlerThread("VideoEncoder");
    private Surface mSurface;
    private final static ArrayBlockingQueue<byte[]> mInputQueue=new ArrayBlockingQueue<>(16);
    private final static ArrayBlockingQueue<byte[]> mOutputQueue=new ArrayBlockingQueue<>(16);
    private MediaCodec.Callback mCallback=new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
            ByteBuffer inputBuffer=mMediaCodec.getInputBuffer(index);
            inputBuffer.clear();
            byte[] data=mInputQueue.poll();
            int length=0;
            if(data!=null){
                inputBuffer.put(data);
                length=data.length;
            }
            mMediaCodec.queueInputBuffer(index,0,length,0,0);
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            ByteBuffer outputBuffer=codec.getOutputBuffer(index);
            if(outputBuffer!=null&&info.size>0){
                byte[] buffer=new byte[outputBuffer.remaining()];
                outputBuffer.get(buffer);
                try {
                    mOutputQueue.put(buffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                codec.releaseOutputBuffer(index,false);
            }
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
            e.printStackTrace();
        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

        }
    };

    public VideoEncoder(){
        try {
            mMediaCodec=MediaCodec.createEncoderByType("video/avc");
            mHandlerThread.start();
            mEncoderHandler=new Handler(mHandlerThread.getLooper());

            MediaFormat format=MediaFormat.createVideoFormat("video/avc",480,800);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_BIT_RATE,480*800);
            format.setInteger(MediaFormat.KEY_FRAME_RATE,40);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,1);
            mMediaCodec.configure(format,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
            mMediaCodec.setCallback(mCallback,mEncoderHandler);
            mSurface=mMediaCodec.createInputSurface();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        mMediaCodec.start();
    }

    public void close(){
        if(mMediaCodec!=null){
            mMediaCodec.stop();
            mMediaCodec.setCallback(null);
            mInputQueue.clear();
            mOutputQueue.clear();
            mMediaCodec.release();
            mMediaCodec=null;
        }
    }

    public boolean isClosed(){
        return mMediaCodec==null;
    }

    public void inputFrameToEncoder(byte[] data){
        System.out.println("inputFrame:"+mInputQueue.offer(data));
    }

    public byte[] pollFrameFromEncoder(){
        return mOutputQueue.poll();
    }

    public Surface getSurface(){
        return mSurface;
    }
}
