package com.fastlib.media;

import java.io.File;

/**
 * Created by sgfb on 16/7/13.
 */
public class Encoder{
    private File mFile;

    public Encoder(File file){
        mFile=file;
    }

    public AudioInfo getAudioInfo(){
        if(WaveFormat.isWave(mFile))
            return new WaveFormat(mFile);
        if(Mp3Format.isMp3(mFile))
            return new Mp3Format(mFile);
        AacFormat aac=new AacFormat(mFile);
        if(aac.isAvailable())
            return aac;
        return null;
    }
}
