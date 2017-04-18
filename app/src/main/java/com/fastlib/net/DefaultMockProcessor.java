package com.fastlib.net;

import android.content.res.AssetManager;
import android.text.TextUtils;

import com.fastlib.db.SaveUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by sgfb on 17/2/6.
 * 一个简单的模拟数据返回
 */
public class DefaultMockProcessor implements MockProcess{
    private String mJsonData;
    private File mDataFile;
    private AssetManager mAssetManager;

    public DefaultMockProcessor(String json){
        mJsonData=json;
    }

    public DefaultMockProcessor(File file){
        this(file,null);
    }

    public DefaultMockProcessor(File path,AssetManager am){
        mDataFile=path;
        mAssetManager=am;
    }

    @Override
    public byte[] dataResponse(){
        try{
            if(!TextUtils.isEmpty(mJsonData)) return mJsonData.getBytes();
            else if(mAssetManager!=null) return SaveUtil.loadAssetsFile(mAssetManager,mDataFile.getAbsolutePath());
            else SaveUtil.loadFile(mDataFile.getAbsolutePath());
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}