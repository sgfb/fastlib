package com.fastlib.media;

import com.fastlib.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sgfb on 16/7/10.
 * mp3格式。暂时只解析MPEG1 Layer3
 */
public class Mp3Format implements AudioInfo{
    private final static int[] VERSION={0,1,0};
    private final static int[] LAYER={0,3,0};
    private final static String[] CRC={"CHECK","UNCHECK"};
    private final static int[][] BIT_RATE={{0,32,64,96,128,160,192,224,256,288,320,352,384,416,448},
                                           {0,32,48,56,64,80,96,112,128,160,192,224,256,320,384},
                                           {0,32,40,48,56,64,80,96,112,128,160,192,224,256,320},
                                           {0,32,64,96,128,160,192,224,256,288,320,352,384,416,448},
                                           {0,32,48,56,64,80,96,112,128,160,192,224,256,320,384},
                                           {0,8,16,24,32,64,80,56,64,128,160,112,128,256,320}};//MPEG1 L1,L2,L3.MPEG2 & MPEG2.5 L1,L2,L3
    private final static int[][] SAMPLING_RATE={{44100,48000,32000,0},
                                                {22050,24000,16000,0},
                                                {11025,12000,8000,0}};//MPEG1,2,2.5
    private final static String[] FRAME_CONVERSE={"UNCONVERSE","CONVERSE"};
    private final static int[] CHANNEL={2,2,2,1};//立体声Stereo,Joint Stereo，双声道,单声道

    private int mVersion;
    private int mLayer;
    private int mBitRate;
    private int mChannel;
    private int mSamplingBit=16;
    private int mSamplingRate;

    public Mp3Format(File file){
        parse(file);
    }

    private void parse(File file){
        try {
            InputStream in=new FileInputStream(file);
            byte[] data=new byte[1024];
            while(in.read(data)!=-1){
                for(int i=0;i<data.length;i++){
                    if(data[i]==((byte)0xff)){
                        byte[] head=new byte[3];
                        if(i>=data.length-3){
                            if(in.read(head)<3){
                                in.close();
                                return;
                            }
                        }
                        else{
                            head[0]=data[i+1];
                            head[1]=data[i+2];
                            head[2]=data[i+3];
                        }
                        if(head[0]==((byte)0xfb)){
                            int versionIndex=Utils.getSomeBits(head[0], 61, 62)>>3;
                            int layerIndex=Utils.getSomeBits(head[0], 62, 64)>>1;
                            int bitRateIndex=Utils.getSomeBits(head[1], 57, 61)>>4;
                            int samplingRateIndex=Utils.getSomeBits(head[1], 61, 63)>>2;
                            int channelIndex=Utils.getSomeBits(head[2], 57, 59)>>6;
                            mVersion=VERSION[versionIndex];
                            mLayer=LAYER[layerIndex];
                            mBitRate=BIT_RATE[2][bitRateIndex];
                            mSamplingRate=SAMPLING_RATE[0][samplingRateIndex];
                            mChannel=CHANNEL[channelIndex];
                            in.close();
                            return;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getBitRate() {
        return mBitRate;
    }

    @Override
    public int getChannels() {
        return mChannel;
    }

    @Override
    public int getSamplingBit() {
        return mSamplingBit;
    }

    @Override
    public int getSamplingRate() {
        return mSamplingRate;
    }

    @Override
    public String getDecoder() {
        return "MP3";
    }

    public int getVersion() {
        return mVersion;
    }

    public int getLayer() {
        return mLayer;
    }

    public static boolean isMp3(File file){
        try {
            InputStream in=new FileInputStream(file);
            byte[] data=new byte[3];
            return in.read(data)>=3&&"ID3".equals(new String(data));
        } catch (IOException e){
            return false;
        }
    }
}
