package com.fastlib;

import android.Manifest;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.fastlib.annotation.Bind;
import com.fastlib.annotation.ContentView;
import com.fastlib.app.FastActivity;
import com.fastlib.utils.ImageUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

@ContentView(R.layout.act_main)
public class MainActivity extends FastActivity{
    @Bind(R.id.keyboard)
    MyKeyboard mKeyboard;

    @Override
    protected void alreadyPrepared() {

    }

    @Bind(R.id.bt)
    private void bt() {
        mKeyboard.setKeyboard(new Keyboard(this,R.xml.keyboard));
        mKeyboard.setPreviewEnabled(false);
        mKeyboard.setVisibility(View.VISIBLE);
    }
}