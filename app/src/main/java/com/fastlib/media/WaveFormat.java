package com.fastlib.media;

import com.fastlib.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sgfb on 16/7/10.
 * .wav格式的音频信息
 */
public class WaveFormat implements AudioInfo{
    private File mFile;
    private int mAudioLength;
    private int mChannels;
    private int mBitRate;
    private int mSamplingRate;
    private int mFormatType;
    private int mPcmLength;


    public WaveFormat(File file){
        mFile=file;
        parse();
    }

    private void parse(){
        byte[] head=new byte[44];
        try {
            InputStream in=new FileInputStream(mFile);
            in.read(head);
            in.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        if(head.length<44)
            return;
        mFormatType = Utils.bytesToInt(head[20], head[21]);
        mChannels =Utils.bytesToInt(head[22], head[23]);
        mSamplingRate =Utils.bytesToInt(head[24], head[25], head[26], head[27]);
        mBitRate =Utils.bytesToInt(head[28], head[29], head[30], head[31]);
        mPcmLength =Utils.bytesToInt(head[34], head[35]);
        mAudioLength =Utils.bytesToInt(head[40], head[41], head[42], head[43]);
    }

    @Override
    public int getBitRate() {
        return mBitRate;
    }

    @Override
    public int getChannels() {
        return mChannels;
    }

    @Override
    public int getSamplingBit() {
        return mPcmLength;
    }

    @Override
    public int getSamplingRate() {
        return mSamplingRate;
    }

    @Override
    public String getDecoder() {
        return "WAVE";
    }

    @Override
    public String toString(){
        return super.toString();
    }

    public static boolean isWave(File file){
        try {
            InputStream in=new FileInputStream(file);
            byte[] data=new byte[4];
            in.skip(8);
            return in.read(data)>=4&&"WAVE".equals(new String(data));
        } catch (IOException e) {
            return false;
        }
    }
}
