package com.fastlib.net;

import android.content.Context;
import android.content.res.AssetManager;

import com.fastlib.db.SaveUtil;

import java.io.IOException;

/**
 * Created by sgfb on 17/2/6.
 * 一个简单的模拟数据返回
 */
public class DefaultMockProcessor implements MockProcess{
    private String mDataPath;
    private AssetManager mAssetManager;

    public DefaultMockProcessor(String path){
        this(path,null);
    }

    public DefaultMockProcessor(String path,AssetManager am){
        mDataPath=path;
        mAssetManager=am;
    }

    @Override
    public byte[] dataResponse(){
        try{
            if(mAssetManager!=null) return SaveUtil.loadAssetsFile(mAssetManager,mDataPath);
            else SaveUtil.loadFile(mDataPath);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}