package com.fastlib.alpha;

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
import java.util.concurrent.BlockingQueue;

/**
 * Create by sgfb on 2019/03/19
 * E-Mail:602687446@qq.com
 */
public class VideoDecoder{
    private BlockingQueue<byte[]> mData=new ArrayBlockingQueue<>(16);
    private MediaCodec mMediaCodec;
    private Handler mDecoderHandler;
    private HandlerThread mHandlerThread=new HandlerThread("decoder");
    private MediaCodec.Callback mCallback=new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
            ByteBuffer inputBuffer=codec.getInputBuffer(index);
            inputBuffer.clear();

            try {
                byte[] data = mData.take();
                inputBuffer.put(data);
                int length=data.length;
                mMediaCodec.queueInputBuffer(index,0,length,0,0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            ByteBuffer buffer=codec.getOutputBuffer(index);
            byte[] data=new byte[buffer.remaining()];
            buffer.get(data);
            codec.releaseOutputBuffer(index,true);
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
            e.printStackTrace();
        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

        }
    };

    public VideoDecoder(){
        mHandlerThread.start();
        mDecoderHandler=new Handler(mHandlerThread.getLooper());
    }

    public void setSurface(Surface surface){
        mMediaCodec.setInputSurface(surface);
    }

    public void start(Surface surface){
        MediaFormat format=MediaFormat.createVideoFormat("video/avc",480,800);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE,480*800);
        format.setInteger(MediaFormat.KEY_FRAME_RATE,80);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,1);

        try {
            mMediaCodec=MediaCodec.createDecoderByType("video/avc");
            mMediaCodec.setCallback(mCallback,mDecoderHandler);
            mMediaCodec.configure(format,surface,null,0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaCodec.start();
    }

    public void close(){
        if(mMediaCodec!=null){
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec=null;
        }
    }

    public void addData(byte[] data){
        try {
            mData.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
