package com.fastlib.media;

import com.fastlib.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sgfb on 16/7/13.
 */
public class AacFormat implements AudioInfo{
    private static final int[] SAMPLING_RATE={96000,88200,64000,48000,44100,32000,24000,22050,16000,12000,11025,8000,7350,0,0,0};
    private static final int[] CHANNEL={0,1,2,3,4,5,6,8,15};

    private boolean isAvailable=true;
    private int mId;
    private int mLayer;
    private int mSamplingRate;
    private int mChannel;
    private int mProtectionAbsent;
    private int mProfile;

    public AacFormat(File file){
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
                        int flag=(0xf0&head[0])>>4;
                        if(flag==0xf){
                            int id=Utils.getSomeBits(head[0], 61, 62)>>3;
                            int layerIndex=Utils.getSomeBits(head[0], 62, 64)>>1;
                            int samplingRateIndex=Utils.getSomeBits(head[2],59,63)>>2;
                            int channelIndex=Utils.getSomeBits(head[2],64,65)<<2;
                            channelIndex|=(Utils.getSomeBits(head[2],57,59)>>6);
                            mSamplingRate=SAMPLING_RATE[samplingRateIndex];
                            mChannel=CHANNEL[channelIndex];
                            in.close();
                            return;
                        }
                    }
                }
            }
            isAvailable=false;
        } catch (IOException e) {
            e.printStackTrace();
            isAvailable=false;
        }
    }

    @Override
    public int getBitRate() {
        return 0;
    }

    @Override
    public int getChannels() {
        return mChannel;
    }

    @Override
    public int getSamplingBit() {
        return 16;
    }

    @Override
    public int getSamplingRate() {
        return mSamplingRate;
    }

    @Override
    public String getDecoder() {
        return "AAC";
    }

    public boolean isAvailable(){
        return isAvailable;
    }
}