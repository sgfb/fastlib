package com.fastlib;

import android.content.res.AssetManager;

import com.fastlib.net.mock.SimpleMockProcessor;

import java.io.File;

/**
 * Created by Administrator on 2018/7/27.
 */
public class TestMock extends SimpleMockProcessor{

    public TestMock(){
        super("hello");
    }

    public TestMock(String json) {
        super(json);
    }

    public TestMock(File file) {
        super(file);
    }

    public TestMock(File path, AssetManager am) {
        super(path, am);
    }
}
